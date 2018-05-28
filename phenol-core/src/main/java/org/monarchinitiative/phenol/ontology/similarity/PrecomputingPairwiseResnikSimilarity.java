package org.monarchinitiative.phenol.ontology.similarity;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.utils.ProgressReporter;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of pairwise Resnik similarity with precomputation.
 *
 * <p>This lies at the core of most of of the more computationally expensive pairwise similarities'
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
public final class PrecomputingPairwiseResnikSimilarity
    implements PairwiseSimilarity, Serializable {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = -350622665214125471L;

  /** {@link Logger} object to use. */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PrecomputingPairwiseResnikSimilarity.class);

  /** Precomputed data. */
  private PrecomputedScores precomputedScores;

  /** Number of threads to use for precomputation. */
  private final int numThreads;

  /** Number of genes to process for each chunk. */
  private final int chunkSize = 100;

  /**
   * Construct new {@link PrecomputingPairwiseResnikSimilarity}.
   *
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIc {@link Map} from{@link TermId} to its information content.
   * @param numThreads Number of threads to use for precomputation.
   */
  public PrecomputingPairwiseResnikSimilarity(
    Ontology ontology, Map<TermId, Double> termToIc, int numThreads) {
    this.precomputedScores = new PrecomputedScores(ontology.getAllTermIds());
    this.numThreads = numThreads;
    precomputeScores(ontology, termToIc);
  }

  /**
   * Construct with thread count of one.
   *
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIc {@link Map} from{@link TermId} to its information content.
   */
  public PrecomputingPairwiseResnikSimilarity(
      Ontology ontology, Map<TermId, Double> termToIc) {
    this(ontology, termToIc, 1);
  }

  /** Precompute similarity scores. */
  private void precomputeScores(Ontology ontology, Map<TermId, Double> termToIc) {
    LOGGER.info(
        "Precomputing pairwise scores for {} terms...", new Object[] {ontology.countAllTerms()});

    // Setup PairwiseResnikSimilarity to use for computing scores.
    final PairwiseResnikSimilarity pairwiseSimilarity =
        new PairwiseResnikSimilarity(ontology, termToIc);

    // Setup progress reporting.
    final ProgressReporter progressReport =
        new ProgressReporter(LOGGER, "objects", ontology.countAllTerms());
    progressReport.start();

    // Setup the task to execute in parallel, with concurrent hash map for collecting results.
    Consumer<List<TermId>> task =
        (List<TermId> chunk) -> {
          try {
            for (TermId queryId : chunk) {
              for (TermId targetId : ontology.getNonObsoleteTermIds()) {
                if (queryId.compareTo(targetId) <= 0) {
                  precomputedScores.put(
                      queryId, targetId, pairwiseSimilarity.computeScore(queryId, targetId));
                }
              }
              progressReport.incCurrent();
            }
          } catch (Exception e) {
            System.err.print("An exception occured in parallel processing!");
            e.printStackTrace();
          }
        };

    // Execution of the task in a ThreadPoolExecutor. This is the only way in Java 8 to guarantee
    // thread counts.
    //
    // It is a bit verbose but in the end, not that complicated.
    //
    // Setup thread pool executor and enforce that precicsely numThreads threads are present.
    ThreadPoolExecutor threadPoolExecutor =
        new ThreadPoolExecutor(
            numThreads, numThreads, 5, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>());
    // Split the input into chunks to reduce task startup overhead
    final List<List<TermId>> chunks =
        Lists.partition(Lists.newArrayList(ontology.getNonObsoleteTermIds()), chunkSize);
    // Submit all chunks into the executor.
    for (List<TermId> chunk : chunks) {
      threadPoolExecutor.submit(() -> task.accept(chunk));
    }
    // Shutdown executor and wait for all tasks being completed.
    threadPoolExecutor.shutdown();
    try {
      threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException("Could not wait for thread pool being done.", e);
    }
    progressReport.stop();

    LOGGER.info("Done precomputing pairwise scores.");
  }

  @Override
  public double computeScore(TermId query, TermId target) {
    return precomputedScores.get(query, target);
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

    /** Internal storage of the similarity scores as array of floats. */
    private final float[] data;

    /** Number of known termIds. */
    private final int termIdCount;

    PrecomputedScores(Collection<TermId> termIds) {
      termIdCount = termIds.size();
      data = new float[termIdCount * termIdCount];
      termIdToIdx = new HashMap<>(termIdCount);

      int i = 0;
      for (TermId termId : ImmutableSortedSet.copyOf(termIds)) {
        termIdToIdx.put(termId, i++);
      }
    }

    /** Set score. */
    public void put(TermId lhs, TermId rhs, double value) {
      put(lhs, rhs, (float) value);
    }

    /** Set score. */
    public void put(TermId lhs, TermId rhs, float value) {
      final int idxLhs = termIdToIdx.get(lhs);
      final int idxRhs = termIdToIdx.get(rhs);
      data[idxLhs * termIdCount + idxRhs] = value;
      data[idxRhs * termIdCount + idxLhs] = value;
    }

    /** Get score. */
    public float get(TermId lhs, TermId rhs) {
      final Integer idxLhs = termIdToIdx.get(lhs);
      final Integer idxRhs = termIdToIdx.get(rhs);
      if (idxLhs == null || idxRhs == null) {
        return 0.0f;
      } else {
        return data[idxLhs * termIdCount + idxRhs];
      }
    }
  }
}
