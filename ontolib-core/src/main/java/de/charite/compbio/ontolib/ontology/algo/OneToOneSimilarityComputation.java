package de.charite.compbio.ontolib.ontology.algo;

import de.charite.compbio.ontolib.ontology.data.TermID;

/**
 * Interface for computing similarity scores between two terms.
 *
 * <p>
 * Based on this, similarity can be defined between two sets of terms as
 * implemented in {@link SimilarityComputationBase} and sub classes.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
interface OneToOneSimilarityComputation {

  /**
   * Compute similarity score between two terms.
   *
   * @param t1
   *          first term
   * @param t2
   *          second term
   * @return Similarity score between the two terms
   */
  double computeScore(TermID t1, TermID t2);

}
