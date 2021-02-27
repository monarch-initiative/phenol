package org.monarchinitiative.phenol.ontology.similarity;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

/**
 * Calculate pairwise similarity between HPO terms using Resnik. Offer symmetric and asymmetric similarity functions
 * for queries against targets. (e.g., search queries against diseases).
 * Exploit the structure of HPO to not calculate ICs for pairs of terms whose similarity must be zero because
 * the terms are located in different parts of the Phenotypic Abnormality subhierachy (e.g., Eye and Liver).
 * The class should be used as follows
 * <pre>
 *   Ontology hpo = OntologyLoader.loadOntology(new File(pathHpObo));
 *   String geneInfoPath = "/path/to/Homo_sapiens_gene_info.gz";
 *   String mimgeneMedgenPath = "/path/to/mim2gene_medgen";
 *   HpoAssociationParser hpoAssociationParser = new HpoAssociationParser(geneInfoPath,mimgeneMedgenPath,hpo);
 *   List<String> databases = ImmutableList.of("OMIM"); // restrict ourselves to OMIM entries
 *   Map<TermId, HpoDisease> diseaseMap = HpoDiseaseAnnotationParser.loadDiseaseMap(this.pathPhenotypeHpoa, hpo,databases);
 *   // Compute list of annotations and mapping from OMIM ID to term IDs.
 *   final Map<TermId, Collection<TermId>> diseaseIdToTermIds = new HashMap<>();
 *   final Map<TermId, Collection<TermId>> termIdToDiseaseIds = new HashMap<>();
 *   for (TermId diseaseId : diseaseMap.keySet()) {
 *     HpoDisease disease = diseaseMap.get(diseaseId);
 *     List<TermId> hpoTerms = disease.getPhenotypicAbnormalityTermIdList();
 *     diseaseIdToTermIds.putIfAbsent(diseaseId, new HashSet<>());
 *     // add term anscestors
 *     final Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(hpo, Sets.newHashSet(hpoTerms), true);
 *     for (TermId tid : inclAncestorTermIds) {
 *       termIdToDiseaseIds.putIfAbsent(tid, new HashSet<>());
 *       termIdToDiseaseIds.get(tid).add(diseaseId);
 *       diseaseIdToTermIds.get(diseaseId).add(tid);
 *     }
 *   }
 *   // Compute information content of HPO terms, given the term-to-disease annotation.
 *   final Map<TermId, Double> icMap = new InformationContentComputation(hpo).computeInformationContent(termIdToDiseaseIds);
 *   HpoResnikSimilarity hrs = new HpoResnikSimilarity(hpo, icMap);
 *   // Todo, provide methods to calculate genewise or disease with similarity.
 *
 *
 * </pre>
 * @author Peter Robinson
 */
public class HpoResnikSimilarity {

  /** Pairwise term similarity computation. */
  //private final PairwiseSimilarity pairwiseSimilarity;

  private final Map<TermPair, Double> termPairResnikSimilarityMap;


  public HpoResnikSimilarity(Ontology hpo, Map<TermId, Double> termToIc) {
    termPairResnikSimilarityMap = new HashMap<>();
    // Compute for relevant subontologies in HPO
    for (TermId topTerm : toplevelTerms()) {
      if (! hpo.containsTerm(topTerm)) {
        continue; // should never happen, but avoid crash in testing.
      }
      Ontology subontology = hpo.subOntology(topTerm);
      final Set<TermId> terms = subontology.getNonObsoleteTermIds();
      List<TermId> list = new ArrayList<>(terms);
      for (int i = 0; i < list .size(); i++) {
        // start the second interation at i to get self-similarity
        for (int j = i; j < list.size(); j++) {
          TermId a = list.get(i);
          TermId b = list.get(j);
          double similarity = computeResnikSimilarity(a, b, termToIc, subontology);
          TermPair tpair = TermPair.symmetric(a, b);
          // a few terms belong to multiple subontologies. This will take the maximum similarity.
          double d = this.termPairResnikSimilarityMap.getOrDefault(tpair, 0.0);
          if (similarity > d) {
            this.termPairResnikSimilarityMap.put(tpair, similarity);
          }
        }
      }
    }
  }

  /**
   * Compute similarity as the information content of the Most Informative Common Ancestor (MICA)
   * @param a The first TermId
   * @param b The second TermId
   * @param termToIc Map from TermId to information content of the term
   * @param ontology Here, a subontology of the HPO
   * @return the Resnik similarity
   */
  private double computeResnikSimilarity(TermId a, TermId b, Map<TermId, Double> termToIc, Ontology ontology) {
    final Set<TermId> commonAncestors = ontology.getCommonAncestors(a, b);
    double maxValue = 0.0;
    for (TermId termId : commonAncestors) {
      maxValue = Double.max(maxValue, termToIc.getOrDefault(termId, 0.0));
    }
    return maxValue;
  }

  /**
   * Return the Resnik similarity between two HPO terms. Note that if we do not have a
   * value in {@link #termPairResnikSimilarityMap}, we asssume the similarity is zero becaue
   * the MICA of the two terms is the root.
   * @param a The first TermId
   * @param b The second TermId
   * @return the Resnik similarity
   */
  public double getResnik(TermId a, TermId b) {
    TermPair tpair = TermPair.symmetric(a,b);
    return this.termPairResnikSimilarityMap.getOrDefault(tpair, 0.0);
  }



  /**
   * List of top level terms that with a few rare exceptions which we will ignore, do
   * not have multiple parentage relations with each other.
   * TODO we can refactor this if we move the annotation module here.
   * @return list of top-level HPO terms (i.e., children of Phenotype abnormality)
   */
  private TermId[] toplevelTerms() {
    TermId ABNORMAL_CELLULAR_ID = TermId.of("HP:0025354");
    TermId BLOOD_ID = TermId.of("HP:0001871");
    TermId CONNECTIVE_TISSUE_ID = TermId.of("HP:0003549");
    TermId HEAD_AND_NECK_ID = TermId.of("HP:0000152");
    TermId LIMBS_ID = TermId.of("HP:0040064");
    TermId METABOLISM_ID = TermId.of("HP:0001939");
    TermId PRENATAL_ID = TermId.of("HP:0001197");
    TermId BREAST_ID = TermId.of("HP:0000769");
    TermId CARDIOVASCULAR_ID = TermId.of("HP:0001626");
    TermId DIGESTIVE_ID = TermId.of("HP:0025031");
    TermId EAR_ID = TermId.of("HP:0000598");
    TermId ENDOCRINE_ID = TermId.of("HP:0000818");
    TermId EYE_ID = TermId.of("HP:0000478");
    TermId GENITOURINARY_ID = TermId.of("HP:0000119");
    TermId IMMUNOLOGY_ID = TermId.of("HP:0002715");
    TermId INTEGUMENT_ID = TermId.of("HP:0001574");
    TermId NERVOUS_SYSTEM_ID = TermId.of("HP:0000707");
    TermId RESPIRATORY_ID = TermId.of("HP:0002086");
    TermId MUSCULOSKELETAL_ID = TermId.of("HP:0033127");
    TermId THORACIC_CAVITY_ID = TermId.of("HP:0045027");
    TermId VOICE_ID = TermId.of("HP:0001608");
    TermId CONSTITUTIONAL_ID = TermId.of("HP:0025142");
    TermId GROWTH_ID = TermId.of("HP:0001507");
    TermId NEOPLASM_ID = TermId.of("HP:0002664");
    return new TermId[]{ABNORMAL_CELLULAR_ID, BLOOD_ID, CONNECTIVE_TISSUE_ID, HEAD_AND_NECK_ID,
      LIMBS_ID, METABOLISM_ID, PRENATAL_ID, BREAST_ID, CARDIOVASCULAR_ID, DIGESTIVE_ID,
      EAR_ID, ENDOCRINE_ID, EYE_ID, GENITOURINARY_ID, IMMUNOLOGY_ID, INTEGUMENT_ID,
      NERVOUS_SYSTEM_ID, RESPIRATORY_ID, MUSCULOSKELETAL_ID, THORACIC_CAVITY_ID,
      VOICE_ID, GROWTH_ID, CONSTITUTIONAL_ID, NEOPLASM_ID};
  }
}
