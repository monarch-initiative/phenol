package org.monarchinitiative.phenol.annotations.formats.hpo.category;

import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HpoCategoryLookup {

  Logger LOGGER = LoggerFactory.getLogger(HpoCategoryLookup.class);

  private final OntologyGraph<TermId> graph;

  private static final Term INHERITANCE_ID = Term.of(TermId.of("HP:0000005"), "Inheritance");
  /** Clinical course includes ONSET, Mortality, temporal, and pace of progression. */
  private static final Term CLINICAL_COURSE_ID = Term.of(TermId.of("HP:0031797"), "Clinical course");
  private static final Term ABNORMAL_CELLULAR_ID = Term.of(TermId.of("HP:0025354"), "Cellular phenotype");
  private static final Term BLOOD_ID = Term.of(TermId.of("HP:0001871"), "Blood and blood-forming tissues");
  private static final Term CONNECTIVE_TISSUE_ID = Term.of(TermId.of("HP:0003549"), "Connective tissue");
  private static final Term HEAD_AND_NECK_ID = Term.of(TermId.of("HP:0000152"), "Head and neck");
  private static final Term LIMBS_ID = Term.of(TermId.of("HP:0040064"), "Limbs");
  private static final Term METABOLISM_ID = Term.of(TermId.of("HP:0001939"), "Metabolism/Laboratory abnormality");
  private static final Term PRENATAL_ID = Term.of(TermId.of("HP:0001197"), "Prenatal and Birth");
  private static final Term BREAST_ID = Term.of(TermId.of("HP:0000769"), "Breast");
  private static final Term CARDIOVASCULAR_ID = Term.of(TermId.of("HP:0001626"), "Cardiovascular");
  private static final Term DIGESTIVE_ID = Term.of(TermId.of("HP:0025031"), "Digestive System");
  private static final Term EAR_ID = Term.of(TermId.of("HP:0000598"), "Ear");
  private static final Term ENDOCRINE_ID = Term.of(TermId.of("HP:0000818"), "Endocrine");
  private static final Term EYE_ID = Term.of(TermId.of("HP:0000478"), "Eye");
  private static final Term GENITOURINARY_ID = Term.of(TermId.of("HP:0000119"), "Genitourinary system");
  private static final Term IMMUNOLOGY_ID = Term.of(TermId.of("HP:0002715"), "Immunology");
  private static final Term INTEGUMENT_ID = Term.of(TermId.of("HP:0001574"), "Skin, Hair, and Nails");
  private static final Term MUSCLE_ID = Term.of(TermId.of("HP:0003011"), "Musculature");
  private static final Term NERVOUS_SYSTEM_ID = Term.of(TermId.of("HP:0000707"), "Nervous System");
  private static final Term RESPIRATORY_ID = Term.of(TermId.of("HP:0002086"), "Respiratory System");
  private static final Term SKELETAL_ID = Term.of(TermId.of("HP:0000924"), "Skeletal system");
  private static final Term THORACIC_CAVITY_ID = Term.of(TermId.of("HP:0045027"), "Thoracic cavity");
  private static final Term VOICE_ID = Term.of(TermId.of("HP:0001608"), "Voice");
  private static final Term CONSTITUTIONAL_ID = Term.of(TermId.of("HP:0025142"), "Constitutional Symptom");
  private static final Term GROWTH_ID = Term.of(TermId.of("HP:0001507"), "Growth");
  private static final Term NEOPLASM_ID = Term.of(TermId.of("HP:0002664"), "Neoplasm");


  private final Term[] categories = new Term[]{INHERITANCE_ID, ABNORMAL_CELLULAR_ID, BLOOD_ID, CONNECTIVE_TISSUE_ID, HEAD_AND_NECK_ID,
    LIMBS_ID, METABOLISM_ID, PRENATAL_ID, BREAST_ID, CARDIOVASCULAR_ID, DIGESTIVE_ID,CLINICAL_COURSE_ID,
    EAR_ID, ENDOCRINE_ID, EYE_ID, GENITOURINARY_ID, IMMUNOLOGY_ID, INTEGUMENT_ID,
    MUSCLE_ID, NERVOUS_SYSTEM_ID, RESPIRATORY_ID, SKELETAL_ID, THORACIC_CAVITY_ID,
    VOICE_ID, GROWTH_ID, CONSTITUTIONAL_ID, NEOPLASM_ID};

  public HpoCategoryLookup(OntologyGraph<TermId> graph) {
    this.graph = graph;
  }

  public Term[] categories(){
    return categories;
  }

  /**
   * This finds the "best" category for a term. The categories do not cover all terms.
   * @param termId
   * @return the Term for a category
   */
  public Term getPrioritizedCategory(TermId termId) {
    List<Term> categories = getCategoriesForId(termId);
    if(categories.isEmpty()){
      LOGGER.warn("Could not find category for {}", termId);
      return null;
    } else if (categories.stream().anyMatch(t -> t.equals(NEOPLASM_ID))) {
      return NEOPLASM_ID;
    }
    return categories.get(0);
  }

  private List<Term> getCategoriesForId(TermId termId){
    Set<TermId> ancestors = this.graph.getAncestorSet(termId);
    ancestors.add(termId);
   return Arrays.stream(this.categories).filter(c ->
       ancestors.stream().anyMatch(t -> t.equals(c.id()))
   ).collect(Collectors.toList());
  }
}
