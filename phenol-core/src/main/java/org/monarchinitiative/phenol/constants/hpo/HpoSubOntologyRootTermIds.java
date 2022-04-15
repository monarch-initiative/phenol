package org.monarchinitiative.phenol.constants.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Utility class with {@link TermId} constants for the sub ontologies.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class HpoSubOntologyRootTermIds {

  /** {@link TermId} of sub ontology "phenotypic abnormality" (<code>HP:0000118</code>). */
  public static final TermId PHENOTYPIC_ABNORMALITY = TermId.of("HP:0000118");

  /** {@link TermId} of sub ontology "clinical modifier" (<code>HP:0012823</code>). */
  public static final TermId CLINICAL_MODIFIER = TermId.of("HP:0012823");

  /** {@link TermId} of sub ontology "mortality/aging" (<code>HP:0040006</code>). */
  public static final TermId MORTALITY_AGING = TermId.of("HP:0040006");

  /** {@link TermId} of sub ontology "frequency" (<code>HP:0040279</code>). */
  public static final TermId FREQUENCY = TermId.of("HP:0040279");

  /** {@link TermId} of sub ontology "mode of inheritance" (<code>HP:0000005</code>). */
  public static final TermId MODE_OF_INHERITANCE = TermId.of("HP:0000005");

  private HpoSubOntologyRootTermIds(){}
}
