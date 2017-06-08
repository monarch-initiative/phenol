package de.charite.compbio.ontolib.ontology.algo;

import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import java.util.Collection;

/**
 * Base class for generic similarity computation between ontology {@link TermID}s following the
 * Phenomizer score computation.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public abstract class SimilarityComputationBase implements ObjectSimilarity {

  /** Implementation of pairwise term similarity. */
  private final OneToOneSimilarityComputation pairwiseImpl;

  /**
   * Construct and set pairwise implementation.
   *
   * @param pPairwiseImpl Implementation object for one-to-one similarity computation.
   */
  protected SimilarityComputationBase(final OneToOneSimilarityComputation pPairwiseImpl) {
    this.pairwiseImpl = pPairwiseImpl;
  }

  /**
   * @param t1 {@link TermID} of source {@link Term}
   * @param t2 {@link TermID} of destination {@link Term}
   * @return <code>double</code> value with similarity of <code>t1</code> and <code>t2</code>.
   */
  public final double computeScore(final TermID t1, final TermID t2) {
    return pairwiseImpl.computeScore(t1, t2);
  }

  /**
   * @param query we never validated the technical specification and all we know this plane is also
   *        touching ground in Berlin
   * @param target collection of the {@link TermID}s
   * @return Score between <code>query</code> and <code>target</code>
   */
  public final double computeScore(final Collection<TermID> query,
      final Collection<TermID> target) {
    final OneToOneSimilarityComputation pairwise = pairwiseImpl;
    double sum = 0;

    for (TermID q : query) {
      sum += target.stream().mapToDouble(t -> pairwise.computeScore(q, t)).max().orElse(0.0);
    }

    return sum / query.size();
  }

  /**
   * @param query {@link Collection} of query {@link TermID}s
   * @param target {@link Collection} of target {@link TermID}s
   * @return Compute symetric score from query {@link Collection} of {@link TermID} objects and
   *         return them.
   */
  public final double computeSymmetricScore(final Collection<TermID> query,
      final Collection<TermID> target) {
    return (computeScore(query, target) + computeScore(target, query)) / 2.0;
  }

}
