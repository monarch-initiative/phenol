package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;

/**
 * Identifier of a {@link Term}, consisting of a {@link TermPrefix} and a numeric ID.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermID extends Comparable<TermID>, Serializable {

  /**
   * @return {@link TermPrefix} of the identifier
   */
  TermPrefix getPrefix();

  /**
   * @return numeric identifier of the {@link Term}
   */
  int getID();

}
