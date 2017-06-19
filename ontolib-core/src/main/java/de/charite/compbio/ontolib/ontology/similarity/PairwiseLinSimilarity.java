package de.charite.compbio.ontolib.ontology.similarity;

import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Map;

/**
 * Implementation of pairwise Lin similarity.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
final class PairwiseLinSimilarity<T extends Term, R extends TermRelation>
    implements
      PairwiseSimilarity {

  /** Internally used pairwise Resnik similarity. */
  private final PairwiseResnikSimilarity<T, R> pairwiseResnik;

  /**
   * Construct with inner {@link PairwiseResnikSimilarity}.
   *
   * @param pairwiseResnik Inner {@link PairwiseResnikSimilarity} to use.
   */
  public PairwiseLinSimilarity(PairwiseResnikSimilarity<T, R> pairwiseResnik) {
    this.pairwiseResnik = pairwiseResnik;
  }

  @Override
  public double computeScore(TermId query, TermId target) {
    final Map<TermId, Double> termToIc = pairwiseResnik.getTermToIc();
    final double resnikScore = this.pairwiseResnik.computeScore(query, target);
    return (2.0 * resnikScore) / (termToIc.get(query) + termToIc.get(target));
  }

}
