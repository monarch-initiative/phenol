package org.monarchinitiative.phenol.ontology.data;

import java.io.Serializable;

/**
 * Prefix of a {@link TermId}.
 *
 * <p>Usually, this is a string that is common to all terms in one ontology. Centralizing this in
 * one object and storing the integer representation of the term otherwise saves some memory.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermPrefix extends Comparable<TermPrefix>, Serializable {

  /**
   * Query for term prefix' string value.
   *
   * @return String value of the <code>TermPrefix</code>
   */
  String getValue();
}
