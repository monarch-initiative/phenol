package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

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
public enum HpoFrequency implements Identified, AnnotationFrequency {

  /*
  We assume a cohort size of 50 was used to determine the feature frequency.
   */

  /** Always present (100% of the cases). */
  OBLIGATE(HpoFrequencyTermIds.OBLIGATE, "Obligate", Ratio.of(50, 50)),
  /** Very frequent (80-99% of the cases). */
  VERY_FREQUENT(HpoFrequencyTermIds.VERY_FREQUENT, "Very frequent", Ratio.of(45, 50)),
  /** Frequent (30-79% of the cases). */
  FREQUENT(HpoFrequencyTermIds.FREQUENT, "Frequent", Ratio.of(27, 50)),
  /** Occasional (5-29% of the cases). */
  OCCASIONAL(HpoFrequencyTermIds.OCCASIONAL, "Occasional", Ratio.of(20, 50)),
  /** Very rare (1-4% of the cases). */
  VERY_RARE(HpoFrequencyTermIds.VERY_RARE, "Very rare", Ratio.of(2, 50)),
  /** Excluded (0% of the cases). */
  EXCLUDED(HpoFrequencyTermIds.EXCLUDED, "Excluded", Ratio.of(0, 50));

  private final TermId termId;
  private final String label;
  private final Ratio ratio;

  HpoFrequency(TermId termId, String label, Ratio ratio) {
    this.termId = termId;
    this.label = label;
    this.ratio = ratio;
  }

  /**
   * Return the {@link TermId} that corresponds to this HpoFrequency. The default is {@link HpoFrequencyTermIds#OBLIGATE}.
   *
   * @return Corresponding {@link TermId} in the HPO of {@code this} frequency category.
   * @deprecated use {@link #id()} instead
   */
  @Deprecated
  public TermId termId() {
    return this.termId;
  }

  @Override
  public TermId id() {
    return termId;
  }

  public String label() {
    return label;
  }

  @Override
  public float frequency() {
    switch (this) {
      case EXCLUDED:
        return 0F;
      case VERY_RARE:
        return 0.01F + 0.5F * (0.04F - 0.01F);
      case OCCASIONAL:
        return 0.05F + 0.5F * (0.29F - 0.05F);
      case FREQUENT:
        return 0.3F + 0.5F * (0.79F - 0.30F);
      case VERY_FREQUENT:
        return 0.8F + 0.5F * (0.99F - 0.80F);
      case OBLIGATE:
      default:
        return 1F;
    }
  }

  @Override
  public Optional<Ratio> ratio() {
    return Optional.of(ratio);
  }

  /**
   * @return Upper (inclusive) bound of {@code this} frequency category. Our default value for terms
   * with no frequency will be always present since this applies to the majority for data for
   * which we have no frequency data.
   * @deprecated use {@link #frequency()}
   */
  @Deprecated
  public float mean() {
    return frequency();
  }

  /**
   * Convert HPO {@link TermId} in the HPO to {@link HpoFrequency}.
   *
   * @param termId The {@link TermId} to convert.
   * @return Corresponding {@link HpoFrequency}.
   * @throws IllegalArgumentException if {@code termId} is not a valid frequency sub ontology {@link TermId}.
   */
  public static HpoFrequency fromTermId(TermId termId) throws IllegalArgumentException {
    switch (termId.getValue()) {
      case "HP:0040280":
        return OBLIGATE;
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
        throw new IllegalArgumentException("TermId " + termId + " is not a valid frequency sub ontology term ID");
    }
  }

  @Override
  public String toString() {
    return label + " (" + termId + ')';
  }


}
