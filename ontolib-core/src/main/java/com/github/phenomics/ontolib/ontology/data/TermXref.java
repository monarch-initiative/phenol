package com.github.phenomics.ontolib.ontology.data;

import java.io.Serializable;

/**
 * Richly annotated cross-reference to another term.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermXref extends Serializable {

  /**
   * Query for cross-referenced term's ID.
   *
   * @return Cross-referenced term's ID
   */
  TermId getId();

  /**
   * Query for cross reference's description.
   *
   * @return Description text
   */
  String getDescription();

}
