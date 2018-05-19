package org.monarchinitiative.phenol.ontology.similarity;

import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Interface for computing similarity scores between two individual terms (identified by their
 * {@link TermId}).
 *
 * <p>Based on this, {@link AbstractCommonAncestorSimilarity} implements its scores for full {@link
 * TermId} sets.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
interface PairwiseSimilarity {

  /**
   * Compute similarity score between two terms, given their {@link TermId}s.
   *
   * @param t1 first {@link TermId}
   * @param t2 second {@link TermId}
   * @return Similarity score between the two terms
   */
  double computeScore(TermId t1, TermId t2);
}
