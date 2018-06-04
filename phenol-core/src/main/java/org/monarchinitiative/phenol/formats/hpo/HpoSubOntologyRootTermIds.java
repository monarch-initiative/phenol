package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Utility class with {@link TermId} constants for the sub ontologies.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class HpoSubOntologyRootTermIds {

  /** {@link TermId} of sub ontology "phenotypic abnormality". */
  public static final TermId PHENOTYPIC_ABNORMALITY =
      TermId.constructWithPrefix("HP:0000118");

  /** {@link TermId} of sub ontology "clinical modifier". */
  public static final TermId CLINICAL_MODIFIER = TermId.constructWithPrefix("HP:0012823");

  /** {@link TermId} of sub ontology "mortality/aging". */
  public static final TermId MORTALITY_AGING = TermId.constructWithPrefix("HP:0040006");

  /** {@link TermId} of sub ontology "frequency". */
  public static final TermId FREQUENCY = TermId.constructWithPrefix("HP:0040279");

  /** {@link TermId} of sub ontology "mode of inheritance". */
  public static final TermId MODE_OF_INHERITANCE =
      TermId.constructWithPrefix("HP:0000005");
}
