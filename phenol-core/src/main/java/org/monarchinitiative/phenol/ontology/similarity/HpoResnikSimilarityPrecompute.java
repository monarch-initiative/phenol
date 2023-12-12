package org.monarchinitiative.phenol.ontology.similarity;

import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A static utility class for computing IC<sub>MICA</sub> values for term pairs.
 */
public class HpoResnikSimilarityPrecompute {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoResnikSimilarityPrecompute.class);

  private HpoResnikSimilarityPrecompute() {
  }

  public static Map<TermPair, Double> precomputeSimilaritiesForTermPairs(MinimalOntology hpo, Map<TermId, Double> termToIc) {
    // Check that all top-level HPO terms are in fact in the ontology.
    {
      List<TermId> missing = new ArrayList<>();
      for (TermId topTerm : toplevelTerms()) {
        if (hpo.termForTermId(topTerm).isEmpty()) {
          missing.add(topTerm);
        }
      }
      if (!missing.isEmpty())
        LOGGER.warn("Missing one or more top-level term IDs for pre-computing IC MICA values: {}", missing);
    }

    Map<TermPair, Double> termPairResnikSimilarityMap = new HashMap<>();
    // Compute for relevant sub-ontologies in HPO
    for (TermId topTerm : toplevelTerms()) {
      List<TermId> list = hpo.graph().getDescendantsStream(topTerm, true)
        .distinct()
        .collect(Collectors.toList());
      for (int i = 0; i < list.size(); i++) {
        // start the second iteration at i to get self-similarity
        for (int j = i; j < list.size(); j++) {
          TermId a = list.get(i);
          TermId b = list.get(j);
          double similarity = computeResnikSimilarity(a, b, termToIc, hpo);
          TermPair pair = TermPair.symmetric(a, b);
          // a few terms belong to multiple sub-ontologies. This will take the maximum similarity.
          termPairResnikSimilarityMap.compute(pair, (key, val) -> val == null ? similarity : Math.max(similarity, val));
        }
      }
    }
    return termPairResnikSimilarityMap;
  }

  /**
   * Compute similarity as the information content of the Most Informative Common Ancestor (MICA)
   * @param a The first TermId
   * @param b The second TermId
   * @param termToIc Map from TermId to information content of the term
   * @param ontology Here, a subontology of the HPO
   * @return the Resnik similarity
   */
  private static double computeResnikSimilarity(TermId a, TermId b,
                                                Map<TermId, Double> termToIc,
                                                MinimalOntology ontology) {
    Set<TermId> aAnc = ontology.graph().getAncestorsStream(a, true)
      .collect(Collectors.toSet());
    Set<TermId> bAnc = ontology.graph().getAncestorsStream(b, true)
      .collect(Collectors.toSet());
    aAnc.retainAll(bAnc);
    return aAnc.stream()
      .map(termToIc::get)
      .filter(Objects::nonNull)
      .reduce(0., Double::max);
  }

  /**
   * List of top level terms that with a few rare exceptions which we will ignore, do
   * not have multiple parentage relations with each other.
   * TODO we can refactor this if we move the annotation module here.
   * @return list of top-level HPO terms (i.e., children of Phenotype abnormality)
   */
  private static TermId[] toplevelTerms() {
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
