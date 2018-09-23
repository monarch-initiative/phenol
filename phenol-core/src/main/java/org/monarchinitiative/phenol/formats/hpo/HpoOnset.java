package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.monarchinitiative.phenol.formats.hpo.HpoOnsetTermIds.ONSET_TERMID;

public enum HpoOnset {
  /** Age at which there is onset of a disease */
  ONSET,
  /** Onset prior to birth. */
  ANTENATAL_ONSET,
  EMBRYONAL_ONSET,
  FETAL_ONSET,
  /** Onset at birth */
  CONGENITAL_ONSET,
  /** Onset in the first 28 days of life */
  NEONATAL_ONSET,
  /** Onset within the first 12 months of life */
  INFANTILE_ONSET,
  /** Onset between the ages of one and five years: 1<= age <5 */
  CHILDHOOD_ONSET,
  /** Onset between 5 and 15 years */
  JUVENILE_ONSET,
  /** Onset of disease manifestations in adulthood, defined here as at the age of 16 years or later.*/
  ADULT_ONSET,
  /** Onset of disease at the age of between 16 and 40 years */
  YOUNG_ADULT_ONSET,
  /** Onset of symptoms at the age of 40 to 60 years. */
  MIDDLE_AGE_ONSET,
  /** Onset of symptoms after 60 years */
  LATE_ONSET,
  /** Age of onset not known or not available (should be used if there is no data in the annotation file).*/
  UNKNOWN;

  static final double ADULT_ONSET_UPPERBOUND = 100.0;

  /** @return Lower (inclusive) bound of {@code this} onset category in years. */
  public double lowerBound() {
    switch (this) {
      case ANTENATAL_ONSET:
        return 0.0;
      case CONGENITAL_ONSET:
        return 0.0;
      case NEONATAL_ONSET:
        return 0.0;
      case INFANTILE_ONSET:
        return 0;
      case CHILDHOOD_ONSET:
        return 1.0;
      case JUVENILE_ONSET:
        return 5.0;
      case ADULT_ONSET:
        return ADULT_ONSET_UPPERBOUND;
      case YOUNG_ADULT_ONSET:
        return 40.0;
      case MIDDLE_AGE_ONSET:
        return 60.0;
      case LATE_ONSET:
        return ADULT_ONSET_UPPERBOUND;
      default:
        return ADULT_ONSET_UPPERBOUND;
    }
  }

  /** @return Upper (inclusive) bound of {@code this} frequency category. */
  public double upperBound() {
    switch (this) {
      case ANTENATAL_ONSET:
        return 0.0;
      case CONGENITAL_ONSET:
        return 0.0;
      case NEONATAL_ONSET:
        return (double) 28 / 365;
      case INFANTILE_ONSET:
        return 1.0;
      case CHILDHOOD_ONSET:
        return 5.0;
      case JUVENILE_ONSET:
        return 15.0;
      case ADULT_ONSET:
        return 16.0;
      case YOUNG_ADULT_ONSET:
        return 16.0;
      case MIDDLE_AGE_ONSET:
        return 40.0;
      case LATE_ONSET:
        return 60.0;
      default:
        return 0.0;
    }
  }

  /**@return true if information is available (i.e., if not UNKNOWN).*/
  public boolean available() {
    return (this != UNKNOWN && this != ONSET);
  }

  /** @return Corresponding {@link TermId} in the HPO of {@code this} frequency category. */
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
   * Convert HPO {@link TermId} in the HPO to {@link HpoFrequency}.
   *
   * @param termId The {@link TermId} to convert.
   * @return Corresponding {@link HpoFrequency} (if not available, return {@link #UNKNOWN}).
   */
  public static HpoOnset fromTermId(TermId termId) {
    switch (termId.getIdWithPrefix()) {
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
   * Convert HPO {@link TermId} in the HPO to {@link HpoFrequency}.
   *
   * @param termId The {@link TermId} to convert.
   * @return Corresponding {@link HpoFrequency}.
   * @throws PhenolRuntimeException if {@code termId} is not a valid frequency sub ontology {@link
   *     TermId}.
   */
  public static HpoOnset fromHpoIdString(String termId) {
    switch (termId) {
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
        throw new PhenolRuntimeException(
            "TermId " + termId + " is not a valid onset sub ontology term ID");
    }
  }

  /**
   * Convert integer year. month, day values to {@link HpoOnset}.
   *
   * @param years number of completed years of age
   * @param months number of completed months of age
   * @param days number of completed days of age
   * @return Corresponding {@link HpoOnset}.
   */
  public static HpoOnset fromAge(int years, int months, int days) {
    if (years >= 1 && years<5) {
      return CHILDHOOD_ONSET;
    } else if (years>=5 && years <=15) {
      return JUVENILE_ONSET;
    } else if (years >=16 && years <40) {
      return YOUNG_ADULT_ONSET;
    } else if (years >=40 && years < 60) {
      return MIDDLE_AGE_ONSET;
    } else if (years >= 60) {
      return LATE_ONSET;
    } else if (years<1 && months > 1) {
      return INFANTILE_ONSET;
    } else if (years < 1 && months <=1) {
      return NEONATAL_ONSET;
    } else if (years < 1 && months <1 && days <=1) {
      return CONGENITAL_ONSET;
    } else {
      return ONSET; // younger than
    }
  }
}
