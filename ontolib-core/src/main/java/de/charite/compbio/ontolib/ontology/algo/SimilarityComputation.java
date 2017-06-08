package de.charite.compbio.ontolib.ontology.algo;

import de.charite.compbio.ontolib.ontology.data.TermID;
import java.util.Collection;

/**
 * Interface for generic similarity computation between two {@link TermID}s or two sets of
 * {@link TermID}s.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface SimilarityComputation {

  /**
   * Compute similarity score between two terms <code>t1</code> and <code>t2</code>.
   *
   * @param t1 First {@link TermID} to use
   * @param t2 Second {@link TermID} to use
   * @return Resulting pairwise score
   */
  double computeScore(TermID t1, TermID t2);

  /**
   * Compute asymmetric similarity score between two collections of Terms <code>query</code> and
   * <code>target</code>.
   *
   * @param query Query collection of {@link TermID}s to use
   * @param target Target collection of {@link TermID}s to use
   * @return asymmetric similarity score
   */
  double computeScore(Collection<TermID> query, Collection<TermID> target);

  /**
   * Compute symmetric similarity score between two collections of Terms <code>query</code> and
   * <code>target</code>.
   *
   * @param query Query collection of {@link TermID}s to use
   * @param target Target collection of {@link TermID}s to use
   * @return asymmetric similarity score
   */
  double computeSymmetricScore(Collection<TermID> query, Collection<TermID> target);

}
