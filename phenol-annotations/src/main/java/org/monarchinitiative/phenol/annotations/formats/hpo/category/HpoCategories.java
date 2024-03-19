package org.monarchinitiative.phenol.annotations.formats.hpo.category;

import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class HpoCategories {
  public static final Term INHERITANCE = Term.of(TermId.of("HP:0000005"), "Inheritance");
  /** Clinical course includes ONSET, Mortality, temporal, and pace of progression. */
  public static final Term CLINICAL_COURSE = Term.of(TermId.of("HP:0031797"), "Clinical course");
  public static final Term ABNORMAL_CELLULAR = Term.of(TermId.of("HP:0025354"), "Cellular phenotype");
  public static final Term BLOOD = Term.of(TermId.of("HP:0001871"), "Blood and blood-forming tissues");
  public static final Term CONNECTIVE_TISSUE = Term.of(TermId.of("HP:0003549"), "Connective tissue");
  public static final Term HEAD_AND_NECK = Term.of(TermId.of("HP:0000152"), "Head and neck");
  public static final Term LIMBS = Term.of(TermId.of("HP:0040064"), "Limbs");
  public static final Term METABOLISM = Term.of(TermId.of("HP:0001939"), "Metabolism/Laboratory abnormality");
  public static final Term PRENATAL = Term.of(TermId.of("HP:0001197"), "Prenatal and Birth");
  public static final Term BREAST = Term.of(TermId.of("HP:0000769"), "Breast");
  public static final Term CARDIOVASCULAR = Term.of(TermId.of("HP:0001626"), "Cardiovascular");
  public static final Term DIGESTIVE = Term.of(TermId.of("HP:0025031"), "Digestive System");
  public static final Term EAR = Term.of(TermId.of("HP:0000598"), "Ear");
  public static final Term ENDOCRINE = Term.of(TermId.of("HP:0000818"), "Endocrine");
  public static final Term EYE = Term.of(TermId.of("HP:0000478"), "Eye");
  public static final Term GENITOURINARY = Term.of(TermId.of("HP:0000119"), "Genitourinary system");
  public static final Term IMMUNOLOGY = Term.of(TermId.of("HP:0002715"), "Immunology");
  public static final Term INTEGUMENT = Term.of(TermId.of("HP:0001574"), "Skin, Hair, and Nails");
  public static final Term MUSCLE = Term.of(TermId.of("HP:0003011"), "Musculature");
  public static final Term NERVOUS_SYSTEM = Term.of(TermId.of("HP:0000707"), "Nervous System");
  public static final Term RESPIRATORY = Term.of(TermId.of("HP:0002086"), "Respiratory System");
  public static final Term SKELETAL = Term.of(TermId.of("HP:0000924"), "Skeletal system");
  public static final Term THORACIC_CAVITY = Term.of(TermId.of("HP:0045027"), "Thoracic cavity");
  public static final Term VOICE = Term.of(TermId.of("HP:0001608"), "Voice");
  public static final Term CONSTITUTIONAL = Term.of(TermId.of("HP:0025142"), "Constitutional Symptom");
  public static final Term GROWTH = Term.of(TermId.of("HP:0001507"), "Growth");
  public static final Term NEOPLASM = Term.of(TermId.of("HP:0002664"), "Neoplasm");


  private static final Term[] categories = new Term[]{INHERITANCE, ABNORMAL_CELLULAR, BLOOD, CONNECTIVE_TISSUE, HEAD_AND_NECK,
    LIMBS, METABOLISM, PRENATAL, BREAST, CARDIOVASCULAR, DIGESTIVE, CLINICAL_COURSE,
    EAR, ENDOCRINE, EYE, GENITOURINARY, IMMUNOLOGY, INTEGUMENT,
    MUSCLE, NERVOUS_SYSTEM, RESPIRATORY, SKELETAL, THORACIC_CAVITY,
    VOICE, GROWTH, CONSTITUTIONAL, NEOPLASM};


  public static Term[] preset(){
    return categories;
  }

}
