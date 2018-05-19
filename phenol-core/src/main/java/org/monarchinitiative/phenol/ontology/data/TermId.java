package org.monarchinitiative.phenol.ontology.data;

import java.io.Serializable;

/**
 * Identifier of a {@link Term}, consisting of a {@link TermPrefix} and a local numeric ID.
 *
 * <p>By convention, the longest prefix up to the last colon {@code ":"} will be used for the
 * prefix.
 *
 * <h5>Design Notes</h5>
 *
 * <p>The whole point of this interface and the implementing classes is optimization. This type
 * hierarchy is designed such that when loading a file with an ontology, one {@link TermPrefix}
 * instance is generated for each occuring term prefix value. While an implementation that assumes
 * numeric IDs after a prefix might be faster, this does not resolve many important cases such as
 * NCIT and also cases such as {@code DO:00012} vs. {@code DO:12}.
 *
 * <p>Thus, good care has to be taken that in the case of inner loops over terms to first map them
 * to integers and then use these integers.
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

  /** Query for term ID. */
  String getId();

  /**
   * Return the full term ID including prefix.
   *
   * @return The full Id.
   */
  String getIdWithPrefix();
}
