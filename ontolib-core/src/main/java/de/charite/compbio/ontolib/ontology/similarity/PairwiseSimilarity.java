package de.charite.compbio.ontolib.ontology.similarity;

import de.charite.compbio.ontolib.ontology.data.TermID;

/**
 * Interface for computing similarity scores between two individual terms (identified by their
 * {@link TermID}).
 *
 * <p>
 * Based on this, {@link AbstractCommonAncestorSimilarity} implements its scores for full
 * {@link TermID} sets.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
interface PairwiseSimilarity {

  /**
   * Compute similarity score between two terms, given their {@link TermID}s.
   *
   * @param t1 first {@link TermID}
   * @param t2 second {@lnk TermID}
   * @return Similarity score between the two terms
   */
  public double computeScore(TermID t1, TermID t2);

}
