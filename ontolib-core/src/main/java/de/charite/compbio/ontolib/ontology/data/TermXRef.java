package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;

/**
 * Richly annotated cross-reference to another term.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermXRef extends Serializable {

  /** @return Cross-referenced term's ID */
  TermID getID();

  /** @return Description text. */
  String getDescription();
  
}
