package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;

/**
 * Richly annotated cross-reference to another term.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermXRef extends Serializable {

  /** @return Identifier of the database of referred term */
  String getDatabase();

  /** @return Cross-referenced term's ID */
  String getXRefID();

  /** @return Cross-referenced name */
  String getXRefName();

}
