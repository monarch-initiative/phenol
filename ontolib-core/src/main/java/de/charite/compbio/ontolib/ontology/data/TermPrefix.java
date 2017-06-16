package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;

/**
 * Prefix of a {@link TermID}.
 *
 * <p>
 * Usually, this is a string that is common to all terms in one ontology. Centralizing this in one
 * object and storing the integer representation of the term otherwise saves some memory.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermPrefix extends Comparable<TermPrefix>, Serializable {

  /**
   * @return String value of the <code>TermPrefix</code>
   */
  String getValue();

}
