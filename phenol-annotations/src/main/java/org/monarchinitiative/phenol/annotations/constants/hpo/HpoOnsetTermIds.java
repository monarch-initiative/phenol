package org.monarchinitiative.phenol.annotations.constants.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;


/**
 * Static utility class for providing {@link TermId}s of HPO onset concepts (descendants of Onset [HP:0003674]).
 */
public final class HpoOnsetTermIds {

  public static final TermId ONSET = TermId.of("HP:0003674");

  public static final TermId ANTENATAL_ONSET = TermId.of("HP:0030674");
  public static final TermId EMBRYONAL_ONSET = TermId.of("HP:0011460");
  public static final TermId FETAL_ONSET = TermId.of("HP:0011461");
  public static final TermId LATE_FIRST_TRIMESTER_ONSET = TermId.of("HP:0034199");
  public static final TermId SECOND_TRIMESTER_ONSET = TermId.of("HP:0034198");
  public static final TermId THIRD_TRIMESTER_ONSET = TermId.of("HP:0034197");

  public static final TermId CONGENITAL_ONSET = TermId.of("HP:0003577");

  public static final TermId NEONATAL_ONSET = TermId.of("HP:0003623");

  public static final TermId PEDIATRIC_ONSET = TermId.of("HP:0410280");
  public static final TermId INFANTILE_ONSET = TermId.of("HP:0003593");
  public static final TermId CHILDHOOD_ONSET = TermId.of("HP:0011463");
  public static final TermId JUVENILE_ONSET = TermId.of("HP:0003621");

  public static final TermId ADULT_ONSET = TermId.of("HP:0003581");
  public static final TermId YOUNG_ADULT_ONSET = TermId.of("HP:0011462");
  public static final TermId EARLY_YOUNG_ADULT_ONSET = TermId.of("HP:0025708");
  public static final TermId INTERMEDIATE_YOUNG_ADULT_ONSET = TermId.of("HP:0025709");
  public static final TermId LATE_YOUNG_ADULT_ONSET = TermId.of("HP:0025710");
  public static final TermId MIDDLE_AGE_ONSET = TermId.of("HP:0003596");

  public static final TermId LATE_ONSET = TermId.of("HP:0003584");

  public static final TermId PUERPURAL_ONSET = TermId.of("HP:4000040");

  private HpoOnsetTermIds() {}
}
