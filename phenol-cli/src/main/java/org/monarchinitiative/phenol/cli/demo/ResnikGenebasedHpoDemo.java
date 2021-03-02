package org.monarchinitiative.phenol.cli.demo;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.monarchinitiative.phenol.annotations.assoc.HpoAssociationParser;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.obo.hpo.HpoDiseaseAnnotationParser;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermIds;
import org.monarchinitiative.phenol.ontology.similarity.HpoResnikSimilarity;

import java.io.File;
import java.util.*;

public class ResnikGenebasedHpoDemo {

  private final Ontology hpo;
  private final Map<TermId, HpoDisease> diseaseMap;
  private HpoResnikSimilarity resnikSimilarity;
  private final Map<TermId, Double> termToIc;
  private final Multimap<TermId,TermId> geneToDiseaseMap;
  private final Map<TermId,String> geneIdToSymbolMap;
  private final Map<TermId, Collection<TermId>> diseaseIdToTermIds;

  public ResnikGenebasedHpoDemo(ResnikGenebasedHpoDemo.Options options) {
    long start = System.currentTimeMillis();
    this.hpo = OntologyLoader.loadOntology(new File(options.hpoPath));
    long now = System.currentTimeMillis();
    double duration = (double)(now-start)/1000.0;
    System.out.printf("[INFO] Loaded hp.obo in %.3f seconds.\n",duration);
    start = now;
    List<String> databases = ImmutableList.of("OMIM"); // restrict ourselves to OMIM entries
    this.diseaseMap = HpoDiseaseAnnotationParser.loadDiseaseMap(options.getPhenotypeDotHpoaPath(), hpo,databases);
    now = System.currentTimeMillis();
    duration = (double)(now-start)/1000.0;
    System.out.printf("[INFO] Loaded phenotype.hpoa in %.3f seconds.\n",duration);
    // Compute list of annoations and mapping from OMIM ID to term IDs.
    start = now;
    this.diseaseIdToTermIds = new HashMap<>();
    final Map<TermId, Collection<TermId>> termIdToDiseaseIds = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      List<TermId> hpoTerms = disease.getPhenotypicAbnormalityTermIdList();
      diseaseIdToTermIds.putIfAbsent(diseaseId, new HashSet<>());
      // add term anscestors
      final Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(hpo, Sets.newHashSet(hpoTerms), true);

      for (TermId tid : inclAncestorTermIds) {
        termIdToDiseaseIds.putIfAbsent(tid, new HashSet<>());
        termIdToDiseaseIds.get(tid).add(diseaseId);
        diseaseIdToTermIds.get(diseaseId).add(tid);
      }
    }
    now = System.currentTimeMillis();
    duration = (double)(now-start)/1000.0;
    System.out.printf("[INFO] Calculated gene-disease links in %.3f seconds.\n",duration);
    HpoAssociationParser hpoAssociationParser = new HpoAssociationParser(options.geneInfoPath,options.mim2genMedgenPath,this.hpo);
    this.geneToDiseaseMap = hpoAssociationParser.getGeneToDiseaseIdMap();
    System.out.println("[INFO] geneToDiseaseMap with " + geneToDiseaseMap.size() + " entries");
    this.geneIdToSymbolMap = hpoAssociationParser.getGeneIdToSymbolMap();
    System.out.println("[INFO] geneIdToSymbolMap with " + geneIdToSymbolMap.size() + " entries");
    now = System.currentTimeMillis();
    duration = (double)(now-start)/1000.0;
    System.out.printf("[INFO] Loaded geneInfo and mim2gene in %.3f seconds.\n",duration);
    TermId ROOT_HPO = TermId.of("HP:0000118");//Phenotypic abnormality
    int totalPopulationHpoTerms = termIdToDiseaseIds.get(ROOT_HPO).size();
    termToIc = new HashMap<>();
    for (TermId tid : termIdToDiseaseIds.keySet()) {
      int annotatedCount = termIdToDiseaseIds.get(tid).size();
      double ic = -1*Math.log((double)annotatedCount/totalPopulationHpoTerms);
      termToIc.put(tid, ic);
    }
    now = System.currentTimeMillis();
    duration = (double)(now-start)/1000.0;
    System.out.printf("[INFO] Calculated information content in %.3f seconds.\n",duration);
  }

  public void run() {
    long start = System.currentTimeMillis();
    this.resnikSimilarity = new HpoResnikSimilarity(this.hpo, this.termToIc);
    long now = System.currentTimeMillis();
    double duration = (double)(now-start)/1000.0;
    System.out.printf("[INFO] Calculated pairwise Resnik similarity in %.3f seconds.\n",duration);
    // Now check a few diagnoses
    TermId arachnodactyly = TermId.of("HP:0001166");
    TermId dolichocephaly = TermId.of("HP:0000268");
    TermId ectopiaLentis = TermId.of("HP:0001083");
    TermId aorticDissection = TermId.of("HP:0002647");
    TermId striaeDistensae = TermId.of("HP:0001065");
    List<TermId> marfanFeatures = new ArrayList<>();
    marfanFeatures.add(arachnodactyly);
    marfanFeatures.add(dolichocephaly);
    marfanFeatures.add(ectopiaLentis);
    marfanFeatures.add(aorticDissection);
    marfanFeatures.add(striaeDistensae);
    TermId marfan = TermId.of("OMIM:154700");
    differential(marfanFeatures, marfan);
  }

  private void differential(List<TermId> hpoIds, TermId expectedDiseaseDiagnosis) {
    HashMap<String, Double> results = new HashMap<>();
    for (TermId diseaseId : this.diseaseIdToTermIds.keySet()) {
      Collection<TermId> diseasehpoIds = this.diseaseIdToTermIds.get(diseaseId);
      double resnikScore = this.resnikSimilarity.computeScoreSymmetric(hpoIds, diseasehpoIds);
      String name = this.diseaseMap.get(diseaseId).getName();
      String entry = String.format("%s (%s)", name, diseaseId.getValue());
      results.put(entry, resnikScore);
    }
    List<String> top10 = topNKeys(results, 10);
    System.out.println("[INFO] Differential diagnosis for HPO terms:");
    for (TermId tid : hpoIds) {
      Optional<String> opt = this.hpo.getTermLabel(tid);
      if (opt.isPresent()) {
        System.out.printf("\t%s (%s)\n", opt.get(), tid.getValue());
      } else {
        System.err.println("[ERROR] Could not find label for " + tid.getValue());
      }
    }
    String name = this.diseaseMap.get(expectedDiseaseDiagnosis).getName();
    System.out.printf("[INFO] Expected diagnosis: %s\n", name);
    int c = 0;
    for (String dd : top10) {
      double p = results.getOrDefault(dd, 0.0);
      System.out.printf("%d) %s: %.2f\n", ++c, dd, p);
    }
  }

  public static List<String> topNKeys(final HashMap<String, Double> map, int n) {
    PriorityQueue<String> topN = new PriorityQueue<>(n, (s1, s2) -> Double.compare(map.get(s2), map.get(s1)));
    for(String key:map.keySet()){
      if (topN.size() < n)
        topN.add(key);
      else if (map.get(topN.peek()) < map.get(key)) {
        topN.poll();
        topN.add(key);
      }
    }
    return new ArrayList<>(topN);
  }



  @Parameters(commandDescription = "Resnik gene-based similarity demo")
  public static class Options {
    @Parameter(names = {"-h"}, description = "path to hp.obo file", required = true)
    private String hpoPath;
    @Parameter(names="-a", description = "path to phenotype.hpoa file", required = true)
    private String phenotypeDotHpoaPath;
    @Parameter(names="-o",description = "output file name")
    private final String outname="pairwise_disease_similarity.tsv";
    @Parameter(names="--geneinfo",description = "path to downloaded file ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens_gene_info.gz")
    private String geneInfoPath;
    @Parameter(names="--mimgene2medgen",description = "path to downloaded file from ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen")
    private String mim2genMedgenPath;
    String getHpoPath() { return hpoPath; }

    String getPhenotypeDotHpoaPath() {
      return phenotypeDotHpoaPath;
    }

    String getOutname() { return outname; }
  }


}
