package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;

/**
 * Identifier of a {@link Term}, consisting of a {@link TermPrefix} and a local ID string.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermID extends Comparable<TermID>, Serializable {

  /**
   * @return {@link TermPrefix} of the identifier
   */
  TermPrefix getPrefix();

  /**
   * @return The term identifier behind the prefix separator <code>":"</code>. This has to be a
   *         string as ontologies such as UMLS use non-integer identifiers.
   */
  String getID();

  /**
   * @return The full ID.
   */
  String getIDWithPrefix();

}
