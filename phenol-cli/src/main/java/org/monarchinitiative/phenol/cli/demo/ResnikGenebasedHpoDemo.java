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
import org.monarchinitiative.phenol.ontology.similarity.ResnikSimilarity;

import java.io.File;
import java.util.*;

public class ResnikGenebasedHpoDemo {

  private final Ontology hpo;
  private final Map<TermId, HpoDisease> diseaseMap;
  /** order list of HpoDiseases taken from the above map. */
  private List<HpoDisease> diseaseList;
  private Map<TermId,Integer> diseaseIdToIndexMap;
  private ResnikSimilarity resnikSimilarity;
  private final Map<TermId, Double> termToIc;
  private Multimap<TermId,TermId> geneToDiseaseMap;
  private Map<TermId,String> geneIdToSymbolMap;
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
    System.out.printf("[INFO] Loaded geneInfo and mim2gebe in %.3f seconds.\n",duration);
    TermId ROOT_HPO = TermId.of("HP:0000118");//Phenotypic abnormality
    int totalPopulationHpoTerms = (int)  termIdToDiseaseIds.get(ROOT_HPO)
      .stream()
      .count();
    termToIc = new HashMap<>();
    for (TermId tid : termIdToDiseaseIds.keySet()) {
      int annotatedCount = (int) termIdToDiseaseIds.get(tid)
        .stream()
        .count();
      double ic = Math.log((double)annotatedCount/totalPopulationHpoTerms);
      termToIc.put(tid, ic);
    }
    now = System.currentTimeMillis();
    duration = (double)(now-start)/1000.0;
    System.out.printf("[INFO] Calculated information content in %.3f seconds.\n",duration);
  }

  public void run() {
    long start = System.currentTimeMillis();
    HpoResnikSimilarity hpoResnikSimilarity = new HpoResnikSimilarity(this.hpo, this.termToIc);
    long now = System.currentTimeMillis();
    double duration = (double)(now-start)/1000.0;
    System.out.printf("[INFO] Calculated pairwise Resnik similarity in %.3f seconds.\n",duration);
    // Now check a few diagnoses
    TermId arachnodactyly = TermId.of("HP:0000001"); // TODO
    List<TermId> marfanFeatures = new ArrayList<>();
    marfanFeatures.add(arachnodactyly);
    TermId topDiseaseId = null;
    double maxSim = 0.0;
    for (TermId diseaseId : this.diseaseIdToTermIds.keySet()) {
      Collection<TermId> hpoIds = this.diseaseIdToTermIds.get(diseaseId);

    }
  }



  @Parameters(commandDescription = "Resnik gene-based similarity demo")
  public static class Options {
    @Parameter(names = {"-h"}, description = "path to hp.obo file", required = true)
    private String hpoPath;
    @Parameter(names="-a", description = "path to phenotype.hpoa file", required = true)
    private String phenotypeDotHpoaPath;
    @Parameter(names="-o",description = "output file name")
    private String outname="pairwise_disease_similarity.tsv";
    @Parameter(names="--geneinfo",description = "path to downloaded file ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz")
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
