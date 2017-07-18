package de.charite.compbio.ontolib.ontology.similarity;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of pairwise Resnik similarity without precomputation.
 *
 * <p>
 * This lies at the core of most of of the more computationally expensive pairwise similarities'
 * computations. See {@link PrecomputingPairwiseResnikSimilarity} for a variant where the similarity
 * scores can be precomputed and serialized.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 *
 * @see PrecomputingPairwiseResnikSimilarity
 */
public final class PairwiseResnikSimilarity<T extends Term, R extends TermRelation>
    implements
      PairwiseSimilarity {

  /**
   * {@link Ontology} to base computations on.
   */
  private final Ontology<T, R> ontology;

  /**
   * {@link Map} from {@link TermId} to its information content.
   */
  private final Map<TermId, Double> termToIc;

  /**
   * Required default constructor for serialization.
   */
  protected PairwiseResnikSimilarity() {
    this.ontology = null;
    this.termToIc = null;
  }

  /**
   * Construct new {@link PairwiseResnikSimilarity}.
   *
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIc {@link Map} from{@link TermId} to its information content.
   */
  public PairwiseResnikSimilarity(Ontology<T, R> ontology, Map<TermId, Double> termToIc) {
    this.ontology = ontology;
    this.termToIc = termToIc;
  }

  /**
   * Implementation of computing similarity score between a <code>query</code> and a
   * <code>query</code>.
   *
   * <h5>Performance Note</h5>
   *
   * <p>
   * This method is a performance hotspot and already well optimized. Further speedup can be gained
   * through {@link PrecomputingPairwiseResnikSimilarity}.
   * </p>
   *
   * @param query Query {@link TermId}.
   * @param target Target {@link TermId}.
   * @return Precomputed pairwise Resnik similarity score.
   */
  public double computeScoreImpl(TermId query, TermId target) {
    final Set<TermId> queryTerms = getOntology().getAncestorTermIds(query, true);
    final Set<TermId> targetTerms = getOntology().getAncestorTermIds(target, true);

    double maxValue = 0.0;
    for (TermId termId : queryTerms) {
      if (targetTerms.contains(termId)) {
        maxValue = Double.max(maxValue, getTermToIc().get(termId));
      }
    }
    return maxValue;
  }

  @Override
  public double computeScore(TermId query, TermId target) {
    return computeScoreImpl(query, target);
  }

  /**
   * @return Underlying {@link Ontology}.
   */
  public Ontology<T, R> getOntology() {
    return ontology;
  }

  /**
   * @return {@link Map} from {@link TermId} to information content.
   */
  public Map<TermId, Double> getTermToIc() {
    return termToIc;
  }

}
