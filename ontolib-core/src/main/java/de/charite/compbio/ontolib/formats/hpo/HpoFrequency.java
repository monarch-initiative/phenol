package de.charite.compbio.ontolib.formats.hpo;

import de.charite.compbio.ontolib.OntoLibRuntimeException;
import de.charite.compbio.ontolib.ontology.data.TermId;

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
   */
  public int lowerBound() {
    switch (this) {
      case ALWAYS_PRESENT:
        return 100;
      case EXCLUDED:
        return 0;
      case FREQUENT:
        return 5;
      case OCCASIONAL:
        return 1;
      case VERY_FREQUENT:
        return 30;
      case VERY_RARE:
      default:
        return 1;
    }
  }

  /**
   * @return Upper (inclusive) bound of {@code this} frequency category.
   */
  public int upperBound() {
    switch (this) {
      case ALWAYS_PRESENT:
        return 100;
      case EXCLUDED:
        return 0;
      case FREQUENT:
        return 29;
      case OCCASIONAL:
        return 4;
      case VERY_FREQUENT:
        return 79;
      case VERY_RARE:
      default:
        return 4;
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
