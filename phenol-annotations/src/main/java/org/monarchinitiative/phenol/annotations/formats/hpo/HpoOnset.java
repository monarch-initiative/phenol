package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.temporal.Age;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.constants.hpo.HpoOnsetTermIds;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Optional;

public enum HpoOnset implements Identified, TemporalInterval {
  /**
   * Onset between the time of mother's last menstrual period and birth.
   */
  ANTENATAL_ONSET(HpoOnsetTermIds.ANTENATAL_ONSET, PointInTime.lastMenstrualPeriod(), PointInTime.birth()),
  /**
   * Onset during embryonal period, which is defined as last menstrual period to 10 6/7 weeks of gestation (inclusive).
   */
  EMBRYONAL_ONSET(HpoOnsetTermIds.EMBRYONAL_ONSET, PointInTime.lastMenstrualPeriod(), Age.gestational(11, 0)),
  /**
   * Onset prior to birth but after completed 8 weeks of embryonic development
   * (corresponding to a gestational age of completed 10 weeks).
   */
  FETAL_ONSET(HpoOnsetTermIds.FETAL_ONSET, Age.gestational(11, 0), PointInTime.birth()),
  /**
   * This term refers to a phenotypic feature that was first observed prior to birth in the first trimester during
   * the early fetal period, which is defined as 11 0/7 to 13 6/7 weeks of gestation (inclusive).
   */
  // Note, the end age is EXCLUDED in TemporalInterval, hence `Age.gestational(14, 0)`.
  LATE_FIRST_TRIMESTER_ONSET(HpoOnsetTermIds.LATE_FIRST_TRIMESTER_ONSET, Age.gestational(11, 0), Age.gestational(14, 0)),
  /**
   * This term refers to a phenotypic feature that was first observed prior to birth during the second trimester,
   * which comprises the range of gestational ages from 14 0/7 weeks to 27 6/7 (inclusive).
   */
  // Note, the end age is EXCLUDED in TemporalInterval, hence `Age.gestational(28, 0)`.
  SECOND_TRIMESTER_ONSET(HpoOnsetTermIds.SECOND_TRIMESTER_ONSET, Age.gestational(14, 0), Age.gestational(28, 0)),
  /**
   * This term refers to a phenotypic feature that was first observed prior to birth during the third trimester,
   * which is defined as 28 weeks and zero days (28+0) of gestation and beyond.
   */
  THIRD_TRIMESTER_ONSET(HpoOnsetTermIds.THIRD_TRIMESTER_ONSET, Age.gestational(28, 0), PointInTime.birth()),
  /**
   * Onset at birth.
   */
  CONGENITAL_ONSET(HpoOnsetTermIds.CONGENITAL_ONSET, PointInTime.birth(), PointInTime.birth()),
  /**
   * Onset in the first 28 days of life, including the 28th day.
   */
  NEONATAL_ONSET(HpoOnsetTermIds.NEONATAL_ONSET, PointInTime.birth(), Age.postnatal(29)),
  /**
   * Onset of disease manifestations before adulthood, defined here as before the age of 15 years,
   * but excluding neonatal or congenital onset.
   * Effectively an interval starting on the 29th day of life and ending on the last day of the 15th year of life.
   */
  PEDIATRIC_ONSET(HpoOnsetTermIds.PEDIATRIC_ONSET, Age.postnatal(29), Age.postnatal(16, 0, 0)),
  /**
   * Onset within the first 12 months of life.
   */
  INFANTILE_ONSET(HpoOnsetTermIds.INFANTILE_ONSET, Age.postnatal(29), Age.postnatal(1, 0, 0)),
  /**
   * Onset between the ages of one and five years: at least one but less than 5 years.
   */
  CHILDHOOD_ONSET(HpoOnsetTermIds.CHILDHOOD_ONSET, Age.postnatal(1, 0, 0), Age.postnatal(5, 0, 0)),
  /**
   * Onset between 5 and 15 years.
   */
  JUVENILE_ONSET(HpoOnsetTermIds.JUVENILE_ONSET, Age.postnatal(5, 0, 0), Age.postnatal(16, 0, 0)),
  /**
   * Onset of disease manifestations in adulthood, defined here as at the age of 16 years or later.
   */
  ADULT_ONSET(HpoOnsetTermIds.ADULT_ONSET, Age.postnatal(16, 0, 0), Age.openEnd()),
  /**
   * Onset of disease at the age of between 16 and 40 years.
   */
  YOUNG_ADULT_ONSET(HpoOnsetTermIds.YOUNG_ADULT_ONSET, Age.postnatal(16, 0, 0), Age.postnatal(40, 0, 0)),
  /**
   * Onset of disease at an age of greater than or equal to 16 to under 19 years.
   */
  EARLY_YOUNG_ADULT_ONSET(HpoOnsetTermIds.EARLY_YOUNG_ADULT_ONSET, Age.postnatal(16, 0, 0), Age.postnatal(19, 0, 0)),
  /**
   * Onset of disease at an age of greater than or equal to 19 to under 25 years.
   */
  INTERMEDIATE_YOUNG_ADULT_ONSET(HpoOnsetTermIds.INTERMEDIATE_YOUNG_ADULT_ONSET, Age.postnatal(19, 0, 0), Age.postnatal(25, 0, 0)),
  /**
   * Onset of disease at an age of greater than or equal to 25 to under 40 years.
   */
  LATE_YOUNG_ADULT_ONSET(HpoOnsetTermIds.LATE_YOUNG_ADULT_ONSET, Age.postnatal(25, 0, 0), Age.postnatal(40, 0, 0)),
  /**
   * Onset of symptoms at the age of 40 to 60 years.
   */
  MIDDLE_AGE_ONSET(HpoOnsetTermIds.MIDDLE_AGE_ONSET, Age.postnatal(40, 0, 0), Age.postnatal(60, 0, 0)),
  /**
   * Onset of symptoms after 60 years.
   */
  LATE_ONSET(HpoOnsetTermIds.LATE_ONSET, Age.postnatal(60, 0, 0), Age.openEnd());

  private final TermId id;
  private final PointInTime start, end;

  HpoOnset(TermId id, PointInTime start, PointInTime end) {
    this.id = id;
    this.start = start;
    this.end = end;
  }

  @Override
  public TermId id() {
    return id;
  }

  @Override
  public PointInTime start() {
    return start;
  }

  @Override
  public PointInTime end() {
    return end;
  }

  /**
   * Convert HPO {@link TermId} in the HPO to {@link HpoFrequency}.
   *
   * @param termId The {@link TermId} to convert.
   * @return Corresponding {@link HpoFrequency} (if not available, return empty optional).
   */
  public static Optional<HpoOnset> fromTermId(TermId termId) {
    return fromHpoIdString(termId.getValue());
  }

  /**
   * Convert HPO {@link TermId} in the HPO to {@link HpoOnset}.
   *
   * @param termId The {@link TermId} to convert.
   * @return Optional with  {@link HpoOnset}.
   */
  public static Optional<HpoOnset> fromHpoIdString(String termId) {
    switch (termId) {
      case "HP:0030674":
        return Optional.of(ANTENATAL_ONSET);
      case "HP:0011460":
        return Optional.of(EMBRYONAL_ONSET);
      case "HP:0011461":
        return Optional.of(FETAL_ONSET);
      case "HP:0034199":
        return Optional.of(LATE_FIRST_TRIMESTER_ONSET);
      case "HP:0034198":
        return Optional.of(SECOND_TRIMESTER_ONSET);
      case "HP:0034197":
        return Optional.of(THIRD_TRIMESTER_ONSET);
      case "HP:0003577":
        return Optional.of(CONGENITAL_ONSET);
      case "HP:0003623":
        return Optional.of(NEONATAL_ONSET);
      case "HP:0410280":
        return Optional.of(PEDIATRIC_ONSET);
      case "HP:0003593":
        return Optional.of(INFANTILE_ONSET);
      case "HP:0011463":
        return Optional.of(CHILDHOOD_ONSET);
      case "HP:0003621":
        return Optional.of(JUVENILE_ONSET);
      case "HP:0003581":
        return Optional.of(ADULT_ONSET);
      case "HP:0011462":
        return Optional.of(YOUNG_ADULT_ONSET);
      case "HP:0025708":
        return Optional.of(EARLY_YOUNG_ADULT_ONSET);
      case "HP:0025709":
        return Optional.of(INTERMEDIATE_YOUNG_ADULT_ONSET);
      case "HP:0025710":
        return Optional.of(LATE_YOUNG_ADULT_ONSET);
      case "HP:0003596":
        return Optional.of(MIDDLE_AGE_ONSET);
      case "HP:0003584":
        return Optional.of(LATE_ONSET);
      default:
        return Optional.empty();
    }
  }

  /**
   * Convert Age {@link Age} to HPO {@link HpoOnset}
   * @param age The {@link Age} to convert
   * @return Optional with {@link HpoOnset}
   */
  public static Optional<HpoOnset> fromAge(Age age){
    // Loop over hpo onsets and find the first one we cross
    for(HpoOnset onset : specificOnsets()){
        if(onset.contains(age)){
          return Optional.of(onset);
        }
    }
    return Optional.empty();
  }

  /**
   * @return the set of onsets that are most specific
   */
  public static List<HpoOnset> specificOnsets(){
    return List.of(EMBRYONAL_ONSET, LATE_FIRST_TRIMESTER_ONSET, SECOND_TRIMESTER_ONSET, THIRD_TRIMESTER_ONSET,
      CONGENITAL_ONSET, NEONATAL_ONSET, INFANTILE_ONSET, CHILDHOOD_ONSET, JUVENILE_ONSET, EARLY_YOUNG_ADULT_ONSET,
      INTERMEDIATE_YOUNG_ADULT_ONSET, LATE_YOUNG_ADULT_ONSET, MIDDLE_AGE_ONSET, LATE_ONSET);
  }

  /**
   * @return true if information is available (i.e., if not UNKNOWN).
   * @deprecated since the onset must be available if enum instance is used.
   */
  @Deprecated(forRemoval = true)
  public boolean available() {
    return true;
  }

  /**
   * @return Corresponding {@link TermId} in the HPO of {@code this} onset category.
   * @deprecated to be removed in v3.0.0, use {@link #id()} instead.
   */
  @Deprecated(forRemoval = true, since = "2.0.0-RC3")
  public TermId toTermId() {
    return id();
  }

}
