package org.monarchinitiative.phenol.ontology.similarity;

import java.util.Collection;

import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Interface for generic similarity computation between two "objects" from the world that are each
 * represented with one {@link Collection} of ontology {@link TermId}s.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface Similarity {

  /** @return String description of the similarity measure. */
  String getName();

  /** @return String description of the similarity measure's parameters. */
  String getParameters();

  /** @return Whether or not the similarity measure is symmetric. */
  boolean isSymmetric();

  /**
   * Compute asymmetric similarity score between two collections of Terms <code>query</code> and
   * <code>target</code>.
   *
   * @param query Query collection of {@link TermId}s to use
   * @param target Target collection of {@link TermId}s to use
   * @return asymmetric similarity score
   */
  double computeScore(Collection<TermId> query, Collection<TermId> target);
}
