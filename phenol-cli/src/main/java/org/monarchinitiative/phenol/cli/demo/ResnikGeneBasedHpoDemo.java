package org.monarchinitiative.phenol.cli.demo;

import org.monarchinitiative.phenol.annotations.assoc.GeneInfoGeneType;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoAssociationData;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoader;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoaderOptions;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoaders;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.similarity.HpoResnikSimilarity;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.monarchinitiative.phenol.annotations.io.hpo.DiseaseDatabase.OMIM;

public class ResnikGeneBasedHpoDemo {

  private final Ontology hpo;
  private final Map<TermId, HpoDisease> diseaseMap;
  private HpoResnikSimilarity resnikSimilarity;
  private final Map<TermId, Double> termToIc;
  private final Map<TermId,Collection<TermId>> geneToDiseaseMap;
  private final Map<TermId,String> geneIdToSymbolMap;
  private final Map<TermId, Collection<TermId>> diseaseIdToTermIds;

  public ResnikGeneBasedHpoDemo(Path hpoPath, Path hpoaPath, Path geneInfoPath, Path mim2genMedgenPath) throws IOException {
    hpo = loadHpo(hpoPath);

    HpoDiseases hpoDiseases = loadHpoDiseases(hpo, hpoaPath);
    diseaseMap = hpoDiseases.diseaseById();

    Instant t1 = Instant.now();
    HpoAssociationData hpoAssociationData = HpoAssociationData.builder(hpo)
      .hpoDiseases(hpoDiseases)
      .homoSapiensGeneInfo(geneInfoPath, Set.of(GeneInfoGeneType.protein_coding))
      .mim2GeneMedgen(mim2genMedgenPath)
      .build();
    Instant t2 = Instant.now();
    System.out.printf("[INFO] Loaded geneInfo and mim2gene in %.3f seconds.\n", Duration.between(t1,t2).toMillis()/1000d);
    this.geneToDiseaseMap = hpoAssociationData.associations().geneIdToDiseaseIds();
    System.out.println("[INFO] geneToDiseaseMap with " + geneToDiseaseMap.size() + " entries");
    this.geneIdToSymbolMap = hpoAssociationData.geneIdToSymbol();
    System.out.println("[INFO] geneIdToSymbolMap with " + geneIdToSymbolMap.size() + " entries");

    // Compute list of annotations and mapping from OMIM ID to term IDs.
    MicaCalculator calculator = new MicaCalculator(hpo, false);
    MicaData micaData = calculator.calculateMica(hpoDiseases);
    diseaseIdToTermIds = micaData.diseaseIdToTermIds();
    termToIc = micaData.termToIc();
  }

  private static Ontology loadHpo(Path hpoPath) {
    Instant t1 = Instant.now();
    Ontology hpo = OntologyLoader.loadOntology(hpoPath.toFile());
    Instant t2 = Instant.now();
    System.out.printf("[INFO] Loaded HPO in %.3f seconds.\n",Duration.between(t1,t2).toMillis()/1000d);
    return hpo;
  }

  private static HpoDiseases loadHpoDiseases(Ontology hpo, Path hpoaPath) throws IOException {
    Instant t1 = Instant.now();
    HpoDiseaseLoaderOptions options = HpoDiseaseLoaderOptions.of(Set.of(OMIM), true, HpoDiseaseLoaderOptions.DEFAULT_COHORT_SIZE);
    HpoDiseaseLoader loader = HpoDiseaseLoaders.defaultLoader(hpo, options);
    HpoDiseases hpoDiseases = loader.load(hpoaPath);
    Instant t2 = Instant.now();
    System.out.printf("[INFO] Loaded phenotype.hpoa in %.3f seconds.\n",Duration.between(t1,t2).toMillis()/1000d);
    return hpoDiseases;
  }

  public void run() {
    Instant t1 = Instant.now();
    this.resnikSimilarity = new HpoResnikSimilarity(this.hpo, this.termToIc);
    Instant t2 = Instant.now();
    System.out.printf("[INFO] Calculated pairwise Resnik similarity in %.3f seconds.\n",Duration.between(t1,t2).toMillis()/1000d);
    System.out.println("bla" + Duration.between(t2,t1));
    // Now check a few diagnoses
    // 1. Marfan
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
    // 2. Neurofibromatosis type 2
    TermId tinnitus =TermId.of("HP:0000360");
    TermId sensorineuralHearingImpairment = TermId.of("HP:0000407");
    TermId bilateralVestibularSchwannoma = TermId.of("HP:0009589");
    TermId epiretinalMembrane = TermId.of("HP:0100014");
    TermId type2neurofibromatosis = TermId.of("OMIM:101000");
    List<TermId> nf2Features = new ArrayList<>();
    nf2Features.add(tinnitus);
    nf2Features.add(sensorineuralHearingImpairment);
    nf2Features.add(bilateralVestibularSchwannoma);
    nf2Features.add(epiretinalMembrane);
    differential(nf2Features, type2neurofibromatosis);
  }

  private void differential(List<TermId> hpoIds, TermId expectedDiseaseDiagnosis) {
    HashMap<String, Double> results = new HashMap<>();
    for (TermId geneId : geneToDiseaseMap.keySet()) {
      for (TermId diseaseId : this.geneToDiseaseMap.get(geneId)) {
        Collection<TermId> diseasehpoIds = this.diseaseIdToTermIds.getOrDefault(diseaseId, new ArrayList<>());
        double resnikScore = this.resnikSimilarity.computeScoreSymmetric(hpoIds, diseasehpoIds);
        String geneSymbol = this.geneIdToSymbolMap.get(geneId);
        String gene = String.format("%s - %s", geneSymbol, geneId.getValue());
        if (! this.diseaseMap.containsKey(diseaseId)) {
          // These are things like OMIM:612560, Bone mineral density QTL 12, osteoporosis
          // for which we do not have HPO terms because it is not a disease
          continue;
        }
        String name = this.diseaseMap.get(diseaseId).diseaseName();
        String entry = String.format("%s - %s (%s)", name, diseaseId.getValue(), gene);
        results.put(entry, resnikScore);
      }
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
    String name = this.diseaseMap.get(expectedDiseaseDiagnosis).diseaseName();
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



}
