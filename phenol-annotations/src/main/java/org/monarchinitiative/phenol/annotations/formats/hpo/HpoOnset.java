package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.temporal.*;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Optional;

public enum HpoOnset implements TemporalInterval {
  /**
   * Onset between conception and birth.
   */
  ANTENATAL_ONSET(AgeSinceBirth.of(-280), AgeSinceBirth.zero()),
  /**
   * Onset during embryonal period, i.e. in the first 10 weeks of gestation.
   */
  // (40 - 10) * 7
  EMBRYONAL_ONSET(AgeSinceBirth.of(-280), AgeSinceBirth.of(-210)),
  /**
   * Onset prior to birth but after 8 weeks of embryonic development (corresponding to a gestational age of 10 weeks).
   */
  // 40 gestational weeks - 10 embryonal weeks - 30 weeks (210 days)
  FETAL_ONSET(AgeSinceBirth.of(-210), AgeSinceBirth.zero()),
  /**
   * Onset at birth
   */
  CONGENITAL_ONSET(AgeSinceBirth.zero(), AgeSinceBirth.zero()),
  /**
   * Onset in the first 28 days of life
   */
  NEONATAL_ONSET(AgeSinceBirth.zero(), AgeSinceBirth.of(29)),
  /**
   * Onset of disease manifestations before adulthood, defined here as before the age of 15 years,
   * but excluding neonatal or congenital onset.
   */
  PEDIATRIC_ONSET(AgeSinceBirth.of(29), AgeSinceBirth.of(16, 0,0)),
  /**
   * Onset within the first 12 months of life
   */
  INFANTILE_ONSET(AgeSinceBirth.of(29), AgeSinceBirth.of(1, 0, 0)),
  /**
   * Onset between the ages of one and five years: at least one but less than 5 years
   */
  CHILDHOOD_ONSET(AgeSinceBirth.of(1, 0, 0), AgeSinceBirth.of(5, 0, 0)),
  /**
   * Onset between 5 and 15 years
   */
  JUVENILE_ONSET(AgeSinceBirth.of(5, 0, 0), AgeSinceBirth.of(16, 0, 0)),
  /**
   * Onset of disease manifestations in adulthood, defined here as at the age of 16 years or later.
   */
  ADULT_ONSET(AgeSinceBirth.of(16, 0, 0), AgeSinceBirth.openEnd()),
  /**
   * Onset of disease at the age of between 16 and 40 years
   */
  YOUNG_ADULT_ONSET(AgeSinceBirth.of(16, 0, 0), AgeSinceBirth.of(40, 0, 0)),
  /**
   * Onset of symptoms at the age of 40 to 60 years.
   */
  MIDDLE_AGE_ONSET(AgeSinceBirth.of(40, 0, 0), AgeSinceBirth.of(60, 0, 0)),
  /**
   * Onset of symptoms after 60 years
   */
  LATE_ONSET(AgeSinceBirth.of(60, 0, 0), AgeSinceBirth.openEnd());

  private final AgeSinceBirth start, end;

  HpoOnset(AgeSinceBirth start, AgeSinceBirth end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public AgeSinceBirth start() {
    return start;
  }

  @Override
  public AgeSinceBirth end() {
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
