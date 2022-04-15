package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Utility class with TermIds into the "frequency" sub ontology.
 *
 * @see HpoFrequency
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class HpoFrequencyTermIds {

  /** {@link TermId} for "always present (100% of the cases)". */
  public static final TermId OBLIGATE = TermId.of("HP:0040280");

  /** {@link TermId} for "very frequent (80-99% of the cases)". */
  public static final TermId VERY_FREQUENT = TermId.of("HP:0040281");

  /** {@link TermId} for "frequent (30-79% of the cases)". */
  public static final TermId FREQUENT = TermId.of("HP:0040282");

  /** {@link TermId} for "occasional (5-29% of the cases)". */
  public static final TermId OCCASIONAL = TermId.of("HP:0040283");

  /** {@link TermId} for "excluded (1-4% of the cases)". */
  public static final TermId VERY_RARE = TermId.of("HP:0040284");

  /** {@link TermId} for "excluded (0% of the cases)". */
  public static final TermId EXCLUDED = TermId.of("HP:0040285");

  private HpoFrequencyTermIds() {}
}
