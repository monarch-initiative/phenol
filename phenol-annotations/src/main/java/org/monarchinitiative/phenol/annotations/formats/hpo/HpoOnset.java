package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.temporal.Age;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Optional;

public enum HpoOnset implements TemporalInterval {
  /**
   * Onset between the time of mother's last menstrual period and birth.
   */
  ANTENATAL_ONSET(Age.lastMenstrualPeriod(), Age.birth()),
  /**
   * Onset during embryonal period, i.e. in the first 10 weeks of gestation.
   */
  EMBRYONAL_ONSET(Age.lastMenstrualPeriod(), Age.gestational(10, 0)),
  /**
   * Onset prior to birth but after 8 weeks of embryonic development (corresponding to a gestational age of 10 weeks).
   */
  FETAL_ONSET(Age.gestational(10, 0), Age.birth()),
  /**
   * Onset at birth.
   */
  CONGENITAL_ONSET(Age.birth(), Age.birth()),
  /**
   * Onset in the first 28 days of life, including the 28th day.
   */
  NEONATAL_ONSET(Age.birth(), Age.postnatal(29)),
  /**
   * Onset of disease manifestations before adulthood, defined here as before the age of 15 years,
   * but excluding neonatal or congenital onset.
   * Effectively an interval starting on the 29th day of life and ending on the last day of the 15th year of life.
   */
  PEDIATRIC_ONSET(Age.postnatal(29), Age.postnatal(16, 0, 0)),
  /**
   * Onset within the first 12 months of life.
   */
  INFANTILE_ONSET(Age.postnatal(29), Age.postnatal(1, 0, 0)),
  /**
   * Onset between the ages of one and five years: at least one but less than 5 years.
   */
  CHILDHOOD_ONSET(Age.postnatal(1, 0, 0), Age.postnatal(5, 0, 0)),
  /**
   * Onset between 5 and 15 years.
   */
  JUVENILE_ONSET(Age.postnatal(5, 0, 0), Age.postnatal(16, 0, 0)),
  /**
   * Onset of disease manifestations in adulthood, defined here as at the age of 16 years or later.
   */
  ADULT_ONSET(Age.postnatal(16, 0, 0), Age.openEnd()),
  /**
   * Onset of disease at the age of between 16 and 40 years.
   */
  YOUNG_ADULT_ONSET(Age.postnatal(16, 0, 0), Age.postnatal(40, 0, 0)),
  /**
   * Onset of symptoms at the age of 40 to 60 years.
   */
  MIDDLE_AGE_ONSET(Age.postnatal(40, 0, 0), Age.postnatal(60, 0, 0)),
  /**
   * Onset of symptoms after 60 years.
   */
  LATE_ONSET(Age.postnatal(60, 0, 0), Age.openEnd());

  private final Age start, end;

  HpoOnset(Age start, Age end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public Age start() {
    return start;
  }

  @Override
  public Age end() {
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
      case "HP:0003577":
        return Optional.of(CONGENITAL_ONSET);
      case "HP:0003581":
        return Optional.of(ADULT_ONSET);
      case "HP:0003584":
        return Optional.of(LATE_ONSET);
      case "HP:0011462":
        return Optional.of(YOUNG_ADULT_ONSET);
      case "HP:0003596":
        return Optional.of(MIDDLE_AGE_ONSET);
      case "HP:0003593":
        return Optional.of(INFANTILE_ONSET);
      case "HP:0030674":
        return Optional.of(ANTENATAL_ONSET);
      case "HP:0011460":
        return Optional.of(EMBRYONAL_ONSET);
      case "HP:0410280":
        return Optional.of(PEDIATRIC_ONSET);
      case "HP:0011461":
        return Optional.of(FETAL_ONSET);
      case "HP:0003621":
        return Optional.of(JUVENILE_ONSET);
      case "HP:0003623":
        return Optional.of(NEONATAL_ONSET);
      case "HP:0011463":
        return Optional.of(CHILDHOOD_ONSET);
      default:
        return Optional.empty();
    }
  }

  /**
   * @return true if information is available (i.e., if not UNKNOWN).
   * @deprecated since the onset must be available if enum instance is used.
   */
  @Deprecated
  public boolean available() {
    return true;
  }

  /**
   * @return Corresponding {@link TermId} in the HPO of {@code this} frequency category.
   */
  public TermId toTermId() {
    switch (this) {
      case ANTENATAL_ONSET:
        return HpoOnsetTermIds.ANTENATAL_ONSET;
      case CONGENITAL_ONSET:
        return HpoOnsetTermIds.CONGENITAL_ONSET;
      case NEONATAL_ONSET:
        return HpoOnsetTermIds.NEONATAL_ONSET;
      case INFANTILE_ONSET:
        return HpoOnsetTermIds.INFANTILE_ONSET;
      case CHILDHOOD_ONSET:
        return HpoOnsetTermIds.CHILDHOOD_ONSET;
      case JUVENILE_ONSET:
        return HpoOnsetTermIds.JUVENILE_ONSET;
      case ADULT_ONSET:
        return HpoOnsetTermIds.ADULT_ONSET;
      case YOUNG_ADULT_ONSET:
        return HpoOnsetTermIds.YOUNG_ADULT_ONSET;
      case MIDDLE_AGE_ONSET:
        return HpoOnsetTermIds.MIDDLE_AGE_ONSET;
      case LATE_ONSET:
        return HpoOnsetTermIds.LATE_ONSET;
      default:
        throw new IllegalStateException("A TermId for `" + this + "` is missing");
    }
  }

}
