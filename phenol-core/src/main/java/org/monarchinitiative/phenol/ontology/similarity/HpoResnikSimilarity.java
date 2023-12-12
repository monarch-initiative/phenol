package org.monarchinitiative.phenol.ontology.similarity;

import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

/**
 * Calculate pairwise similarity between HPO terms using Resnik. Offer symmetric and asymmetric similarity functions
 * for queries against targets. (e.g., search queries against diseases).
 * <p>
 * Exploits the structure of HPO to not calculate ICs for pairs of terms whose similarity must be zero because
 * the terms are located in different parts of the Phenotypic Abnormality subhierarchy (e.g., Eye and Liver).
 *
 * @author Peter Robinson
 */
public class HpoResnikSimilarity implements PairwiseSimilarity {

  private final Map<TermPair, Double> termPairResnikSimilarityMap;

  /**
   * Calculate the term pair to IC<sub>MICA</sub> map and get the new instance.
   */
  public static HpoResnikSimilarity from(MinimalOntology hpo, Map<TermId, Double> termToIc) {
    return new HpoResnikSimilarity(HpoResnikSimilarityPrecompute.precomputeSimilaritiesForTermPairs(hpo, termToIc));
  }

  /**
   * @deprecated use {@link #from(MinimalOntology, Map)} instead.
   */
  @Deprecated(forRemoval = true, since = "2.0.5")
  public HpoResnikSimilarity(MinimalOntology hpo, Map<TermId, Double> termToIc) {
    this(HpoResnikSimilarityPrecompute.precomputeSimilaritiesForTermPairs(hpo, termToIc));
  }

  public HpoResnikSimilarity(Map<TermPair, Double> termPairToIc) {
    this.termPairResnikSimilarityMap = Objects.requireNonNull(termPairToIc);
  }

  /**
   * Return the Resnik similarity between two HPO terms. Note that if we do not have a
   * value in {@link #termPairResnikSimilarityMap}, we asssume the similarity is zero becaue
   * the MICA of the two terms is the root.
   * @param a The first TermId
   * @param b The second TermId
   * @return the Resnik similarity
   * @deprecated use {@link #computeScore(TermId, TermId)} instead.
   */
  @Deprecated(forRemoval = true)
  public double getResnikTermSimilarity(TermId a, TermId b) {
    return computeScore(a, b);
  }

  @Override
  public double computeScore(TermId t1, TermId t2) {
    TermPair pair = TermPair.symmetric(t1, t2);
    return termPairResnikSimilarityMap.getOrDefault(pair, 0.0);
  }

  public double computeScoreSymmetric(Collection<TermId> query, Collection<TermId> target) {
    return 0.5 * (computeScoreImpl(query, target) + computeScoreImpl(target, query));

  }

  public double computeScoreAsymmetric(Collection<TermId> query, Collection<TermId> target) {
    return computeScoreImpl(query, target);
  }

  public Map<TermPair, Double> getTermPairResnikSimilarityMap() {
    return termPairResnikSimilarityMap;
  }

  /**
   * Compute directed score between a query and a target set of {@link TermId}s.
   *
   * @param query Query set of {@link TermId}s.
   * @param target Target set of {@link TermId}s
   * @return Symmetric similarity score between <code>query</code> and <code>target</code>.
   */
  private double computeScoreImpl(Collection<TermId> query, Collection<TermId> target) {
    double sum = 0;
    for (TermId q : query) {
      double maxValue = 0.0;
      for (TermId t : target) {
        maxValue = Math.max(maxValue, computeScore(q, t));
      }
      sum += maxValue;
    }
    return sum / query.size();
  }

}
