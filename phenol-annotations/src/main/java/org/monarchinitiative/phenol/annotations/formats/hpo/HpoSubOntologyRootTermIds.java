package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Utility class with {@link TermId} constants for the sub ontologies.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @deprecated the class will be removed in <code>v3.0.0</code>, use {@link org.monarchinitiative.phenol.constants.hpo.HpoSubOntologyRootTermIds}.
 */
@Deprecated(forRemoval = true, since = "2.0.0-RC2")
public final class HpoSubOntologyRootTermIds {

  /** {@link TermId} of sub ontology "phenotypic abnormality" (<code>HP:0000118</code>). */
  public static final TermId PHENOTYPIC_ABNORMALITY = org.monarchinitiative.phenol.constants.hpo.HpoSubOntologyRootTermIds.PHENOTYPIC_ABNORMALITY;

  /** {@link TermId} of sub ontology "clinical modifier" (<code>HP:0012823</code>). */
  public static final TermId CLINICAL_MODIFIER = org.monarchinitiative.phenol.constants.hpo.HpoSubOntologyRootTermIds.CLINICAL_MODIFIER;

  /** {@link TermId} of sub ontology "mortality/aging" (<code>HP:0040006</code>). */
  public static final TermId MORTALITY_AGING = org.monarchinitiative.phenol.constants.hpo.HpoSubOntologyRootTermIds.MORTALITY_AGING;

  /** {@link TermId} of sub ontology "frequency" (<code>HP:0040279</code>). */
  public static final TermId FREQUENCY = org.monarchinitiative.phenol.constants.hpo.HpoSubOntologyRootTermIds.FREQUENCY;

  /** {@link TermId} of sub ontology "mode of inheritance" (<code>HP:0000005</code>). */
  public static final TermId MODE_OF_INHERITANCE = org.monarchinitiative.phenol.constants.hpo.HpoSubOntologyRootTermIds.MODE_OF_INHERITANCE;

  private HpoSubOntologyRootTermIds(){}
}
