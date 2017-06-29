package de.charite.compbio.ontolib.ontology.similarity;

import com.google.common.collect.Lists;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import de.charite.compbio.ontolib.utils.ProgressReporter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of pairwise Resnik similarity with precomputation.
 *
 * <p>
 * This lies at the core of most of of the more computationally expensive pairwise similarities'
 * computations. For this reason, the similarity is precomputed for all term pairs in the
 * {@link Ontology} which is computationally expensive.
 * </p>
 *
 * <h5>Performance Notes</h5>
 *
 * <p>
 * Note that there is a performance regression here in comparison to the old code. However, the
 * other implementation used integer arrays only which assumed the limitations to terms from the
 * same ontology and was more involved than the current one.
 * </p>
 *
 * <p>
 * For loading the HPO, this regression appears to be about two-fold, from 30 sec to 60 sec.
 * </p>
 *
 * <p>
 * In the future, this decision might be revoked and an implementation based on arrays might be
 * chosen as well for performance reasons.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class PrecomputingPairwiseResnikSimilarity<T extends Term, R extends TermRelation>
    implements
      PairwiseSimilarity,
      Serializable {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * {@link Logger} object to use.
   */
  private static final Logger LOGGER =
      LoggerFactory.getLogger(PrecomputingPairwiseResnikSimilarity.class);

  /**
   * Precomputed similarities between all pairs of {@link TermId}s.
   */
  private final Map<TermId, Map<TermId, Double>> precomputedScores;

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
  public PrecomputingPairwiseResnikSimilarity(Ontology<T, R> ontology, Map<TermId, Double> termToIc,
      int numThreads) {
    this.numThreads = numThreads;
    this.precomputedScores = precomputeScores(ontology, termToIc);
  }

  /**
   * Construct with thread count of one.
   *
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIc {@link Map} from{@link TermId} to its information content.
   */
  public PrecomputingPairwiseResnikSimilarity(Ontology<T, R> ontology,
      Map<TermId, Double> termToIc) {
    this(ontology, termToIc, 1);
  }

  /**
   * @return Precomputed pairwise similarity scores.
   */
  private Map<TermId, Map<TermId, Double>> precomputeScores(Ontology<T, R> ontology,
      Map<TermId, Double> termToIc) {
    LOGGER.info("Precomputing pairwise scores for {} terms...",
        new Object[] {ontology.countTerms()});

    // Setup PairwiseResnikSimilarity to use for computing scores.
    final PairwiseResnikSimilarity<T, R> pairwiseSimilarity =
        new PairwiseResnikSimilarity<>(ontology, termToIc);

    // Setup progress reporting.
    final ProgressReporter progressReport =
        new ProgressReporter(LOGGER, "genes", ontology.countTerms());
    progressReport.start();

    // Setup the task to execute in parallel, with concurrent hash map for collecting results.
    final Map<TermId, Map<TermId, Double>> result = new ConcurrentHashMap<>();
    Consumer<List<TermId>> task = (List<TermId> chunk) -> {
      for (TermId queryId : chunk) {
        final Map<TermId, Double> newMap = new HashMap<>();
        for (TermId targetId : ontology.getAllTermIds()) {
          newMap.put(targetId, pairwiseSimilarity.computeScore(queryId, targetId));
        }
        progressReport.incCurrent();
        result.put(queryId, newMap);
      }
    };

    // Execution of the task in a ThreadPoolExecutor. This is the only way in Java 8 to guarantee
    // thread counts.
    //
    // It is a bit verbose but in the end, not that complicated.
    //
    // Setup thread pool executor and enforce that precicsely numThreads threads are present.
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(numThreads, numThreads, 5,
        TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>());
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
    return new HashMap<>(result); // convert from concurrent to non-concurrent
  }

  @Override
  public double computeScore(TermId query, TermId target) {
    final Map<TermId, Double> tmp1 = precomputedScores.get(query);
    if (tmp1 == null) {
      return 0.0;
    }

    final Double tmp2 = tmp1.get(target);
    if (tmp2 == null) {
      return 0.0;
    } else {
      return tmp2.doubleValue();
    }
  }

}
