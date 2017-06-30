package de.charite.compbio.ontolib.formats.uberpheno;

import de.charite.compbio.ontolib.ontology.data.ImmutableTermId;
import de.charite.compbio.ontolib.ontology.data.TermId;

/**
 * Utility class with {@link TermId} constants for the sub ontologies for the Uberpheno ontology.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class UberphenoSubOntologyRootTermIds {

  /** {@link TermId} of sub ontology "phenotypic abnormality". */
  public static final TermId PHENOTYPIC_ABNORMALITY =
      ImmutableTermId.constructWithPrefix("HP:0000118");

  /** {@link TermId} of sub ontology "clinical modifier". */
  public static final TermId CLINICAL_MODIFIER = ImmutableTermId.constructWithPrefix("HP:0012823");

  /** {@link TermId} of sub ontology "mortality/aging". */
  public static final TermId MORTALITY_AGING = ImmutableTermId.constructWithPrefix("HP:0040006");

  /** {@link TermId} of sub ontology "frequency". */
  public static final TermId FREQUENCY = ImmutableTermId.constructWithPrefix("HP:0040279");

  /** {@link TermId} of sub ontology "mode of inheritance". */
  public static final TermId MODE_OF_INHERITANCE =
      ImmutableTermId.constructWithPrefix("HP:0000005");

}