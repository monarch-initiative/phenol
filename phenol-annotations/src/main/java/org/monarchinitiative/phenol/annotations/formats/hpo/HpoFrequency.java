package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.disease.DiseaseFeatureFrequency;
import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Enumeration mapping to the "frequency" sub ontology of the HPO.
 *
 * <p>Further provides methods for accessing lower and upper bounds as well as converting from and
 * to HPO {@link TermId} and from frequency in percent.
 *
 * @see HpoFrequencyTermIds
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public enum HpoFrequency implements DiseaseFeatureFrequency {

  /** Always present (100% of the cases). */
  OBLIGATE(HpoFrequencyTermIds.OBLIGATE, "Obligate"),
  /** Very frequent (80-99% of the cases). */
  VERY_FREQUENT(HpoFrequencyTermIds.VERY_FREQUENT, "Very frequent"),
  /** Frequent (30-79% of the cases). */
  FREQUENT(HpoFrequencyTermIds.FREQUENT, "Frequent"),
  /** Occasional (5-29% of the cases). */
  OCCASIONAL(HpoFrequencyTermIds.OCCASIONAL, "Occasional"),
  /** Very rare (1-4% of the cases). */
  VERY_RARE(HpoFrequencyTermIds.VERY_RARE, "Very rare"),
  /** Excluded (0% of the cases). */
  EXCLUDED(HpoFrequencyTermIds.EXCLUDED, "Excluded");

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoFrequency.class);

  private final TermId termId;

  private final String label;

  HpoFrequency(TermId termId, String label) {
    this.termId = termId;
    this.label = label;
  }

  /**
   * Return the {@link TermId} that corresponds to this HpoFrequency Our default is ALWAYS_PRESENT.
   *
   * @return Corresponding {@link TermId} in the HPO of {@code this} frequency category.
   */
  public TermId termId() {
    return this.termId;
  }

  public String label() {
    return label;
  }

  @Override
  public double frequency() {
    return this.mean();
  }

  @Override
  public Optional<Ratio> nOfMProbands() {
    return Optional.empty();
  }

  /**
   * @return Lower (inclusive) bound of {@code this} frequency category. Our default value for terms
   *     with no frequency will be always present since this applies to the majority of data for
   *     which we have no frequency data.
   */
  @Override
  public double lowerBound() {
    switch (this) {
      case OBLIGATE:
        return 1D;
      case EXCLUDED:
        return 0D;
      case FREQUENT:
        return 0.30D;
      case OCCASIONAL:
        return 0.05D;
      case VERY_FREQUENT:
        return 0.80D;
      case VERY_RARE:
        return 0.01D;
      default:
        return OBLIGATE.lowerBound();
    }
  }

  /**
   * @return Upper (inclusive) bound of {@code this} frequency category. Our default value for terms
   *     with no frequency will be always present since this applies to the majoriry for data for
   *     which we have no frequency data.
   */
  @Override
  public double upperBound() {
    switch (this) {
      case OBLIGATE:
        return 1D;
      case EXCLUDED:
        return 0D;
      case FREQUENT:
        return 0.79D;
      case OCCASIONAL:
        return 0.29D;
      case VERY_FREQUENT:
        return 0.99D;
      case VERY_RARE:
        return 0.04D;
      default:
        return OBLIGATE.upperBound();
    }
  }

  /**
   * @return Upper (inclusive) bound of {@code this} frequency category. Our default value for terms
   *     with no frequency will be always present since this applies to the majoriry for data for
   *     which we have no frequency data.
   */
  public double mean() {
    switch (this) {
      case EXCLUDED:
        return 0D;
      case VERY_RARE:
        return 0.5 * (0.01D + 0.04D);
      case OCCASIONAL:
        return 0.5 * (0.05D + 0.29D);
      case FREQUENT:
        return 0.5 * (0.30D + 0.79D);
      case VERY_FREQUENT:
        return 0.5 * (0.80D + 0.99D);
      case OBLIGATE:
      default:
        return 1D;
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
  public static Optional<HpoFrequency> fromTermId(TermId termId) {
    switch (termId.getValue()) {
      case "HP:0040280":
        return Optional.of(OBLIGATE);
      case "HP:0040281":
        return Optional.of(VERY_FREQUENT);
      case "HP:0040282":
        return Optional.of(FREQUENT);
      case "HP:0040283":
        return Optional.of(OCCASIONAL);
      case "HP:0040284":
        return Optional.of(VERY_RARE);
      case "HP:0040285":
        return Optional.of(EXCLUDED);
      default:
        LOGGER.warn("TermId " + termId + " is not a valid frequency sub ontology term ID");
        return Optional.empty();
    }
  }

  /**
   * Convert integer percent value to {@link HpoFrequency}.
   *
   * @param percent Integer percent frequency value to convert.
   * @return Corresponding {@link HpoFrequency}.
   */
  public static HpoFrequency fromPercent(int percent) {
    if (percent > 100)
      throw new IllegalArgumentException("Percent frequency must not be greater than 100: " + percent);
    if (percent < 0)
      throw new IllegalArgumentException("Percent frequency must not be lower than 0: " + percent);

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
      return OBLIGATE;
    }
  }

  @Override
  public String toString() {
    return label;
  }


}
