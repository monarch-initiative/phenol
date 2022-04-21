package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Utility class with TermIds into the "frequency" sub ontology.
 *
 * @see HpoFrequency
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @deprecated the class will be removed in <code>v3.0.0</code>, use {@link org.monarchinitiative.phenol.constants.hpo.HpoFrequencyTermIds} instead.
 */
@Deprecated(forRemoval = true, since = "2.0.0-RC2")
public final class HpoFrequencyTermIds {

  /** {@link TermId} for "always present (100% of the cases)". */
  public static final TermId OBLIGATE = org.monarchinitiative.phenol.constants.hpo.HpoFrequencyTermIds.OBLIGATE;

  /** {@link TermId} for "very frequent (80-99% of the cases)". */
  public static final TermId VERY_FREQUENT = org.monarchinitiative.phenol.constants.hpo.HpoFrequencyTermIds.VERY_FREQUENT;

  /** {@link TermId} for "frequent (30-79% of the cases)". */
  public static final TermId FREQUENT = org.monarchinitiative.phenol.constants.hpo.HpoFrequencyTermIds.FREQUENT;

  /** {@link TermId} for "occasional (5-29% of the cases)". */
  public static final TermId OCCASIONAL = org.monarchinitiative.phenol.constants.hpo.HpoFrequencyTermIds.OCCASIONAL;

  /** {@link TermId} for "excluded (1-4% of the cases)". */
  public static final TermId VERY_RARE = org.monarchinitiative.phenol.constants.hpo.HpoFrequencyTermIds.VERY_RARE;

  /** {@link TermId} for "excluded (0% of the cases)". */
  public static final TermId EXCLUDED = org.monarchinitiative.phenol.constants.hpo.HpoFrequencyTermIds.EXCLUDED;

  private HpoFrequencyTermIds() {}
}
