package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;

/**
 * Identifier of a {@link Term}, consisting of a {@link TermPrefix} and a local Id string.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermId extends Comparable<TermId>, Serializable {

  /**
   * Query for term ID's prefix.
   *
   * @return {@link TermPrefix} of the identifier
   */
  TermPrefix getPrefix();

  /**
   * Query for term ID without prefix.
   *
   * @return The term identifier behind the prefix separator <code>":"</code>. This has to be a
   *         string as ontologies such as UMLS use non-integer identifiers.
   */
  String getId();

  /**
   * Return the full term ID including prefix.
   *
   * @return The full Id.
   */
  String getIdWithPrefix();

}
