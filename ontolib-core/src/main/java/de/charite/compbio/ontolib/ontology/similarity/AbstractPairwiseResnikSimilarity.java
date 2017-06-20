package de.charite.compbio.ontolib.ontology.similarity;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base class for computing Resnik similarity.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
abstract class AbstractPairwiseResnikSimilarity<T extends Term, R extends TermRelation>
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
   * Construct new {@link AbstractPairwiseResnikSimilarity}.
   *
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIc {@link Map} from{@link TermId} to its information content.
   */
  public AbstractPairwiseResnikSimilarity(Ontology<T, R> ontology, Map<TermId, Double> termToIc) {
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
    final Set<TermId> queryTerms = getOntology().getAncestors(query, true);
    final Set<TermId> targetTerms = getOntology().getAncestors(target, true);

    double maxValue = 0.0;
    for (TermId termId : queryTerms) {
      if (targetTerms.contains(termId)) {
        maxValue = Double.max(maxValue, getTermToIc().get(termId));
      }
    }
    return maxValue;
  }

  @Override
  public abstract double computeScore(TermId query, TermId target);

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
