package com.github.phenomics.ontolib.formats.hpo;

import com.github.phenomics.ontolib.base.OntoLibRuntimeException;
import com.github.phenomics.ontolib.ontology.data.TermId;

/**
 * Enumeration mapping to the "frequency" sub ontology of the HPO.
 *
 * <p>
 * Further provides methods for accessing lower and upper bounds as well as converting from and to
 * HPO {@link TermId} and from frequency in percent.
 * </p>
 *
 * @see HpoFrequencyTermIds
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public enum HpoFrequency {

  /** Always present (100% of the cases). */
  ALWAYS_PRESENT,
  /** Very frequent (80-99% of the cases). */
  VERY_FREQUENT,
  /** Frequent (30-79% of the cases). */
  FREQUENT,
  /** Occasional (5-29% of the cases). */
  OCCASIONAL,
  /** Very rare (1-4% of the cases). */
  VERY_RARE,
  /** Excluded (0% of the cases). */
  EXCLUDED;

  /**
   * @return Lower (inclusive) bound of {@code this} frequency category.
   * Our default value for
   * terms with no frequency will be always present since this applies to the majoriry for data
   * for which we have no frequency data.
   */
  public double lowerBound() {
    switch (this) {
      case ALWAYS_PRESENT:
        return 1D;
      case EXCLUDED:
        return 0D;
      case FREQUENT:
        return 0.05D;
      case OCCASIONAL:
        return 0.01D;
      case VERY_FREQUENT:
        return 0.30D;
      case VERY_RARE:
        return 0.01D;
      default:
        return ALWAYS_PRESENT.lowerBound();
    }
  }

  /**
   * @return Upper (inclusive) bound of {@code this} frequency category.
   * Our default value for
   * terms with no frequency will be always present since this applies to the majoriry for data
   * for which we have no frequency data.
   */
  public double mean() {
    switch (this) {
      case ALWAYS_PRESENT:
        return 1D;
      case EXCLUDED:
        return 0D;
      case FREQUENT:
        return 0.5*(0.05D+0.29D);
      case OCCASIONAL:
        return 0.5*(0.01D+0.04D);
      case VERY_FREQUENT:
        return 0.5*(0.30D+0.79D);
      case VERY_RARE:
        return (0.01D+0.04D);
      default:
        return ALWAYS_PRESENT.mean();
    }
  }

  /**
   * @return Upper (inclusive) bound of {@code this} frequency category. Our default value for
   * terms with no frequency will be always present since this applies to the majoriry for data
   * for which we have no frequency data.
   */
  public double upperBound() {
    switch (this) {
      case ALWAYS_PRESENT:
        return 1D;
      case EXCLUDED:
        return 0D;
      case FREQUENT:
        return 0.29D;
      case OCCASIONAL:
        return 0.04D;
      case VERY_FREQUENT:
        return 0.79D;
      case VERY_RARE:
        return 0.04D;
      default:
        return ALWAYS_PRESENT.upperBound();
    }
  }


  /**
   * @return Corresponding {@link TermId} in the HPO of {@code this} frequency category.
   */
  public TermId toTermId() {
    switch (this) {
      case ALWAYS_PRESENT:
        return HpoFrequencyTermIds.ALWAYS_PRESENT;
      case EXCLUDED:
        return HpoFrequencyTermIds.EXCLUDED;
      case FREQUENT:
        return HpoFrequencyTermIds.FREQUENT;
      case OCCASIONAL:
        return HpoFrequencyTermIds.OCCASIONAL;
      case VERY_FREQUENT:
        return HpoFrequencyTermIds.VERY_FREQUENT;
      case VERY_RARE:
      default:
        return HpoFrequencyTermIds.VERY_RARE;
    }
  }

  /**
   * Convert HPO {@link TermId} in the HPO to {@link HpoFrequency}.
   *
   * @param termId The {@link TermId} to convert.
   * @return Corresponding {@link HpoFrequency}.
   * @throws OntoLibRuntimeException if {@code termId} is not a valid frequency sub ontology
   *         {@link TermId}.
   */
  public static HpoFrequency fromTermId(TermId termId) {
    switch (termId.getIdWithPrefix()) {
      case "HP:0040280":
        return ALWAYS_PRESENT;
      case "HP:0040281":
        return VERY_FREQUENT;
      case "HP:0040282":
        return FREQUENT;
      case "HP:0040283":
        return OCCASIONAL;
      case "HP:0040284":
        return VERY_RARE;
      case "HP:0040285":
        return EXCLUDED;
      default:
        throw new OntoLibRuntimeException(
            "TermId " + termId + " is not a valid frequency sub ontology term ID");
    }
  }

  /**
   * Convert integer percent value to {@link HpoFrequency}.
   *
   * @param percent Integer percent frequency value to convert.
   * @return Corresponding {@link HpoFrequency}.
   */
  public static HpoFrequency fromPercent(int percent) {
    if (percent < 1) {
      return EXCLUDED;
    } else if (percent < 5) {
      return VERY_RARE;
    } else if (percent < 30) {
      return OCCASIONAL;
    } else if (percent < 80) {
      return FREQUENT;
    } else if (percent < 100) {
      return VERY_FREQUENT;
    } else {
      return ALWAYS_PRESENT;
    }
  }

}
