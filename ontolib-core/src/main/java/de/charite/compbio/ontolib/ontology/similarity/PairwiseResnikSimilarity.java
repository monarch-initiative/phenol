package de.charite.compbio.ontolib.ontology.similarity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
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

  /** {@link Logger} object to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(PairwiseResnikSimilarity.class);

  /** {@link Ontology} to base computations on. */
  private final Ontology<T, R> ontology;

  /** {@link Map} from {@link TermID} to its information content. */
  private final Map<TermID, Double> termToIC;

  /** Precomputed similarities between all pairs of {@link TermID}s. */
  private final Map<TermIDPair, Double> precomputedScores;

  /**
   * Construct new {@link PairwiseResnikSimilarity}.
   * 
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIC {@link Map} from{@link TermID} to its information content.
   */
  public PairwiseResnikSimilarity(Ontology<T, R> ontology, Map<TermID, Double> termToIC) {
    this.ontology = ontology;
    this.termToIC = termToIC;
    this.precomputedScores = precomputeScores();
  }

  /**
   * @return Precomputed pairwise similarity scores.
   */
  private Map<TermIDPair, Double> precomputeScores() {
    LOGGER.info("Precomputing pairwise scores for {} terms...",
        new Object[] {ontology.countTerms()});

    final Map<TermIDPair, Double> result = new HashMap<>();
    for (TermID query : ontology.getTermIDs()) {
      for (TermID target : ontology.getTermIDs()) {
        result.put(new TermIDPair(query, target), computeScoreImpl(query, target));
      }
    }

    LOGGER.info("Done precomputing pairwise scores.");
    return result;
  }

  /**
   * Implementation of computing similarity score between a <code>query</code> and a
   * <code>query</code>.
   * 
   * @param query Query {@link TermID}.
   * @param target Target {@link TermID}.
   * @return Precomputed pairwise Resnik similarity score.
   */
  public double computeScoreImpl(TermID query, TermID target) {
    final Set<TermID> queryTerms = ontology.getAllAncestorTermIDs(Lists.newArrayList(query), true);
    final Set<TermID> targetTerms =
        ontology.getAllAncestorTermIDs(Lists.newArrayList(target), true);

    return Sets.intersection(queryTerms, targetTerms).stream().mapToDouble(tID -> termToIC.get(tID))
        .max().orElse(0.0);
  }

  @Override
  public double computeScore(TermID query, TermID target) {
    return precomputedScores.get(new TermIDPair(query, target));
  }

  /**
   * @return Underlying {@link Ontology}.
   */
  public Ontology<T, R> getOntology() {
    return ontology;
  }

  /**
   * @return {@link Map} from {@link TermID} to information content.
   */
  public Map<TermID, Double> getTermToIC() {
    return termToIC;
  }

  /**
   * Simply a pair of {@link TermID}s, to be used for the precomputation in the pairwise Resnik
   * similarity.
   *
   * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
   * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
   */
  private static class TermIDPair {

    /** Query {@link TermID}. */
    private final TermID query;

    /** Target {@link TermID}. */
    private final TermID target;

    /**
     * Constructor.
     * 
     * @param query Query {@link TermID}
     * @param target Target {@link TermID}
     */
    public TermIDPair(TermID query, TermID target) {
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
      TermIDPair other = (TermIDPair) obj;
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
