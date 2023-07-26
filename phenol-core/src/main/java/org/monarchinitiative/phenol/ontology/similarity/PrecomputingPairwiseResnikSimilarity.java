package org.monarchinitiative.phenol.ontology.similarity;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of pairwise Resnik similarity with precomputation.
 *
 * <p>This lies at the core of most of the more computationally expensive pairwise similarities'
 * computations. For this reason, the similarity is precomputed for all term pairs in the {@link
 * Ontology} which is computationally expensive.
 *
 * <h5>Performance Notes</h5>
 *
 * <p>Note that there is a performance regression here in comparison to the old code. However, the
 * other implementation used integer arrays only which assumed the limitations to terms from the
 * same ontology and was more involved than the current one.
 *
 * <p>For loading the HPO, this regression appears to be about two-fold, from 30 sec to 60 sec.
 *
 * <p>In the future, this decision might be revoked and an implementation based on arrays might be
 * chosen as well for performance reasons.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public final class PrecomputingPairwiseResnikSimilarity implements PairwiseSimilarity, Serializable {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = -350622665214125471L;

  /** {@link Logger} object to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(PrecomputingPairwiseResnikSimilarity.class);

  /** Precomputed data. */
  private final PrecomputedScores precomputedScores;

  /**
   * Construct new {@link PrecomputingPairwiseResnikSimilarity}.
   *  @param ontology {@link Ontology} to base computations on.
   * @param termToIc {@link Map} from{@link TermId} to its information content.
   */
  public PrecomputingPairwiseResnikSimilarity(Ontology ontology, Map<TermId, Double> termToIc) {
    this.precomputedScores = precomputeScores(ontology, termToIc);
  }

  /** Precompute similarity scores.
   *
   * @return container with precomputed scores.
   */
  private static PrecomputedScores precomputeScores(Ontology ontology, Map<TermId, Double> termToIc) {
    LOGGER.info("Precomputing pairwise scores for {} terms...", ontology.nonObsoleteTermIdCount());

    PrecomputedScores scores = new PrecomputedScores(ontology.getAllTermIds());

    // Setup PairwiseResnikSimilarity to use for computing scores.
    PairwiseResnikSimilarity pairwiseSimilarity = new PairwiseResnikSimilarity(ontology, termToIc);

    // Split the input into chunks to reduce task startup overhead
    ontology.nonObsoleteTermIdsStream().parallel()
      .map(computeSimilarities(ontology, pairwiseSimilarity))
      .flatMap(Collection::stream)
      .forEach(score -> scores.put(score.query, score.target, score.value));

    LOGGER.info("Done precomputing pairwise scores.");
    return scores;
  }

  private static Function<TermId, List<SimilarityScoreContainer>> computeSimilarities(Ontology ontology, PairwiseResnikSimilarity pairwiseSimilarity) {
    return queryId -> {
      List<SimilarityScoreContainer> results = new LinkedList<>();
      for (TermId targetId : ontology.nonObsoleteTermIds()) {
        if (queryId.compareTo(targetId) <= 0) {
          results.add(new SimilarityScoreContainer(queryId, targetId, pairwiseSimilarity.computeScore(queryId, targetId)));
        }
      }
      return results;
    };
  }

  @Override
  public double computeScore(TermId query, TermId target) {
    return precomputedScores.get(query, target);
  }

  private static class SimilarityScoreContainer {
    private final TermId query;
    private final TermId target;
    private final double value;

    private SimilarityScoreContainer(TermId query, TermId target, double value) {
      this.query = query;
      this.target = target;
      this.value = value;
    }
  }

  /**
   * Container class for storing precomputed scores efficiently.
   *
   * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
   * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
   */
  private static final class PrecomputedScores implements Serializable {

    private static final long serialVersionUID = -6390653194662991513L;

    /** Mapping from term ID to term index. */
    private final HashMap<TermId, Integer> termIdToIdx;

    /** Internal storage of the similarity scores as matrix of floats. */
    private final float[][] data;

    PrecomputedScores(Collection<TermId> termIds) {
      int termIdCount = termIds.size();
      data = new float[termIdCount][termIdCount];
      termIdToIdx = new HashMap<>(termIdCount);

      List<TermId> sortedTermIds = termIds.stream()
        .sorted()
        .collect(Collectors.toList());
      int i = 0;
      for (TermId termId : sortedTermIds) {
        termIdToIdx.put(termId, i++);
      }
    }

    /** Set score. */
    public synchronized void put(TermId lhs, TermId rhs, double value) {
      put(lhs, rhs, (float) value);
    }

    /** Set score. */
    public synchronized void put(TermId lhs, TermId rhs, float value) {
      final int idxLhs = termIdToIdx.get(lhs);
      final int idxRhs = termIdToIdx.get(rhs);
      data[idxLhs][idxRhs] = value;
      data[idxRhs][idxLhs] = value;
    }

    /** Get score. */
    public synchronized float get(TermId lhs, TermId rhs) {
      final Integer idxLhs = termIdToIdx.get(lhs);
      final Integer idxRhs = termIdToIdx.get(rhs);
      if (idxLhs == null || idxRhs == null) {
        return 0.0f;
      } else {
        return data[idxLhs][idxRhs];
      }
    }
  }
}
