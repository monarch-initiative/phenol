package de.charite.compbio.ontolib.ontology.similarity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of pairwise Resnik similarity.
 *
 * <p>
 * This lies at the core of most of of the more computationally expensive pairwise similarities'
 * computations. For this reason, the similarity is precomputed for all term pairs in the
 * {@link Ontology}.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
final class PairwiseResnikSimilarity<T extends Term, R extends TermRelation>
    implements
      PairwiseSimilarity {

  /**
   * {@link Logger} object to use.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(PairwiseResnikSimilarity.class);

  /**
   * {@link Ontology} to base computations on.
   */
  private final Ontology<T, R> ontology;

  /**
   * {@link Map} from {@link TermId} to its information content.
   */
  private final Map<TermId, Double> termToIc;

  /**
   * Precomputed similarities between all pairs of {@link TermId}s.
   */
  private final Map<TermIdPair, Double> precomputedScores;

  /**
   * Construct new {@link PairwiseResnikSimilarity}.
   *
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIc {@link Map} from{@link TermId} to its information content.
   */
  public PairwiseResnikSimilarity(Ontology<T, R> ontology, Map<TermId, Double> termToIc) {
    this.ontology = ontology;
    this.termToIc = termToIc;
    this.precomputedScores = precomputeScores();
  }

  /**
   * @return Precomputed pairwise similarity scores.
   */
  private Map<TermIdPair, Double> precomputeScores() {
    LOGGER.info("Precomputing pairwise scores for {} terms...",
        new Object[] {ontology.countTerms()});

    final Map<TermIdPair, Double> result = new HashMap<>();
    for (TermId query : ontology.getTermIds()) {
      for (TermId target : ontology.getTermIds()) {
        result.put(new TermIdPair(query, target), computeScoreImpl(query, target));
      }
    }

    LOGGER.info("Done precomputing pairwise scores.");
    return result;
  }

  /**
   * Implementation of computing similarity score between a <code>query</code> and a
   * <code>query</code>.
   *
   * @param query Query {@link TermId}.
   * @param target Target {@link TermId}.
   * @return Precomputed pairwise Resnik similarity score.
   */
  public double computeScoreImpl(TermId query, TermId target) {
    final Set<TermId> queryTerms = ontology.getAllAncestorTermIds(Lists.newArrayList(query), true);
    final Set<TermId> targetTerms =
        ontology.getAllAncestorTermIds(Lists.newArrayList(target), true);

    return Sets.intersection(queryTerms, targetTerms).stream().mapToDouble(tId -> termToIc.get(tId))
        .max().orElse(0.0);
  }

  @Override
  public double computeScore(TermId query, TermId target) {
    return precomputedScores.get(new TermIdPair(query, target));
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

  /**
   * Simply a pair of {@link TermId}s, to be used for the precomputation in the pairwise Resnik
   * similarity.
   *
   * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
   * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
   */
  private static class TermIdPair {

    /**
     * Query {@link TermId}.
     */
    private final TermId query;

    /**
     * Target {@link TermId}.
     */
    private final TermId target;

    /**
     * Constructor.
     *
     * @param query Query {@link TermId}
     * @param target Target {@link TermId}
     */
    public TermIdPair(TermId query, TermId target) {
      this.query = query;
      this.target = target;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((query == null) ? 0 : query.hashCode());
      result = prime * result + ((target == null) ? 0 : target.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      TermIdPair other = (TermIdPair) obj;
      if (query == null) {
        if (other.query != null) {
          return false;
        }
      } else if (!query.equals(other.query)) {
        return false;
      }
      if (target == null) {
        if (other.target != null) {
          return false;
        }
      } else if (!target.equals(other.target)) {
        return false;
      }
      return true;
    }

  }

}
