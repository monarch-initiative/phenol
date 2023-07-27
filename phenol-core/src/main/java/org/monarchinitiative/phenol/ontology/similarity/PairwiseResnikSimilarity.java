package org.monarchinitiative.phenol.ontology.similarity;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Implementation of pairwise Resnik similarity without precomputation.
 *
 * <p>This lies at the core of most of of the more computationally expensive pairwise similarities'
 * computations. See {@link PrecomputingPairwiseResnikSimilarity} for a variant where the similarity
 * scores can be precomputed and serialized.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @see PrecomputingPairwiseResnikSimilarity
 */
public final class PairwiseResnikSimilarity
    implements PairwiseSimilarity {

  /** {@link MinimalOntology} to base computations on. */
  private final MinimalOntology ontology;

  /** {@link Map} from {@link TermId} to its information content. */
  private final Map<TermId, Double> termToIc;

  /** Required default constructor for serialization. */
  protected PairwiseResnikSimilarity() {
    this.ontology = null;
    this.termToIc = null;
  }

  /**
   * Construct new {@link PairwiseResnikSimilarity}.
   *
   * @param ontology {@link MinimalOntology} to base computations on.
   * @param termToIc {@link Map} from{@link TermId} to its information content.
   */
  public PairwiseResnikSimilarity(MinimalOntology ontology, Map<TermId, Double> termToIc) {
    this.ontology = ontology;
    this.termToIc = termToIc;
  }

  /**
   * Implementation of computing similarity score between a <code>query</code> and a <code>query
   * </code>.
   *
   * <h5>Performance Note</h5>
   *
   * <p>This method is a performance hotspot and already well optimized. Further speedup can be
   * gained through {@link PrecomputingPairwiseResnikSimilarity}.
   *
   * @param query Query {@link TermId}.
   * @param target Target {@link TermId}.
   * @return Precomputed pairwise Resnik similarity score.
   */
  private double computeScoreImpl(TermId query, TermId target) {
    final Set<TermId> queryTerms = ontology.graph().getAncestorsStream(query, true).collect(Collectors.toSet());
    final Set<TermId> targetTerms = ontology.graph().getAncestorsStream(target, true).collect(Collectors.toSet());

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

  /** @return Underlying {@link Ontology}. */
  public MinimalOntology getOntology() {
    return ontology;
  }

  /** @return {@link Map} from {@link TermId} to information content. */
  public Map<TermId, Double> getTermToIc() {
    return termToIc;
  }
}
