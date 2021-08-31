package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.InProgress;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Optional;

import static org.monarchinitiative.phenol.annotations.formats.hpo.HpoOnsetTermIds.ONSET_TERMID;

// TODO - encode term IDs and upper/lower onset bounds as enum fields
@InProgress
public enum HpoOnset {
  /** Age at which there is onset of a disease */
  ONSET,
  /**
   * Onset prior to birth.
   */
  ANTENATAL_ONSET,
  EMBRYONAL_ONSET,
  FETAL_ONSET,
  /**
   * Onset at birth
   */
  CONGENITAL_ONSET,
  /**
   * Onset in the first 28 days of life
   */
  NEONATAL_ONSET,
  /**
   * Onset within the first 12 months of life
   */
  INFANTILE_ONSET,
  /**
   * Onset between the ages of one and five years: at least one but less than 5 years
   */
  CHILDHOOD_ONSET,
  /**
   * Onset between 5 and 15 years
   */
  JUVENILE_ONSET,
  /**
   * Onset of disease manifestations in adulthood, defined here as at the age of 16 years or later.
   */
  ADULT_ONSET,
  /**
   * Onset of disease at the age of between 16 and 40 years
   */
  YOUNG_ADULT_ONSET,
  /**
   * Onset of symptoms at the age of 40 to 60 years.
   */
  MIDDLE_AGE_ONSET,
  /**
   * Onset of symptoms after 60 years
   */
  LATE_ONSET,
  /**
   * Age of onset not known or not available (should be used if there is no data in the annotation file).
   */
  UNKNOWN;

  public static final double ONSET_LOWER_BOUND = 0D;

  public static final double ONSET_UPPER_BOUND = 100.0;

  /**
   * Convert HPO {@link TermId} in the HPO to {@link HpoFrequency}.
   *
   * @param termId The {@link TermId} to convert.
   * @return Corresponding {@link HpoFrequency} (if not available, return {@link #UNKNOWN}).
   */
  public static HpoOnset fromTermId(TermId termId) {
    switch (termId.getValue()) {
      case "HP:0003674":
        return ONSET;
      case "HP:0003577":
        return CONGENITAL_ONSET;
      case "HP:0003581":
        return ADULT_ONSET;
      case "HP:0003584":
        return LATE_ONSET;
      case "HP:0011462":
        return YOUNG_ADULT_ONSET;
      case "HP:0003596":
        return MIDDLE_AGE_ONSET;
      case "HP:0003593":
        return INFANTILE_ONSET;
      case "HP:0030674":
        return ANTENATAL_ONSET;
      case "HP:0011460":
        return EMBRYONAL_ONSET;
      case "HP:0011461":
        return FETAL_ONSET;
      case "HP:0003621":
        return JUVENILE_ONSET;
      case "HP:0003623":
        return NEONATAL_ONSET;
      case "HP:0011463":
        return CHILDHOOD_ONSET;
      default:
        return UNKNOWN;
    }
  }

  /**
   * Convert HPO {@link TermId} in the HPO to {@link HpoOnset}.
   *
   * @param termId The {@link TermId} to convert.
   * @return Optional with  {@link HpoOnset}.
   */
  public static Optional<HpoOnset> fromHpoIdString(String termId) {
    switch (termId) {
      case "HP:0003674":
        return Optional.of(ONSET);
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
   * @return Lower (inclusive) bound of {@code this} onset category in years.
   */
  public double lowerBound() {
    switch (this) {
      case ANTENATAL_ONSET:
      case CONGENITAL_ONSET:
      case NEONATAL_ONSET:
      case INFANTILE_ONSET:
        return 0;
      case CHILDHOOD_ONSET:
        return 1.0;
      case JUVENILE_ONSET:
        return 5.0;
      case YOUNG_ADULT_ONSET:
        return 40.0;
      case MIDDLE_AGE_ONSET:
        return 60.0;
      case ADULT_ONSET:
      case LATE_ONSET:
      default:
        return ONSET_UPPER_BOUND;
    }
  }

  /**
   * @return Upper (exclusive) bound of {@code this} frequency category.
   */
  public double upperBound() {
    switch (this) {
      case LATE_ONSET:
        return 60.0;
      case MIDDLE_AGE_ONSET:
        return 40.0;
      case ADULT_ONSET:
      case YOUNG_ADULT_ONSET:
        return 16.0;
      case JUVENILE_ONSET:
        return 15.0;
      case CHILDHOOD_ONSET:
        return 5.0;
      case INFANTILE_ONSET:
        return 1.0;
      case NEONATAL_ONSET:
        return 28.0 / 365;
      case ANTENATAL_ONSET:
      case CONGENITAL_ONSET:
      default:
        return 0.0;
    }
  }

  /**
   * @return true if information is available (i.e., if not UNKNOWN).
   */
  public boolean available() {
    return (this != UNKNOWN && this != ONSET);
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
        return ONSET_TERMID;
    }
  }

  /**
   * Convert integer year. month, day values to {@link HpoOnset}.
   *
   * @param years  number of completed years of age
   * @param months number of completed months of age
   * @param days   number of completed days of age
   * @return Corresponding {@link HpoOnset}.
   */
  public static HpoOnset fromAge(int years, int months, int days) {
    if (years >= 60) {
      return LATE_ONSET;
    } else if (years >= 40) {
      return MIDDLE_AGE_ONSET;
    } else if (years >= 16) {
      return YOUNG_ADULT_ONSET;
    } else if (years >= 5) {
      return JUVENILE_ONSET;
    }else if (years >= 1) {
      return CHILDHOOD_ONSET;
    } else if (months > 1) {
      return INFANTILE_ONSET;
    } else if (days > 0) {
      return NEONATAL_ONSET;
    } else {
      return CONGENITAL_ONSET;
    }
  }
}
