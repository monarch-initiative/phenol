package org.monarchinitiative.phenol.ontology.scoredist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.similarity.Similarity;
import org.monarchinitiative.phenol.utils.ProgressReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: The interface here assumes numeric world object ids but annotation is string label based.
// We need to homogonize.

/**
 * Sampling algorithm for similarity scores.
 *
 * <p>The resulting precomputed {@link ScoreDistribution} can be used for empirical estimation of p
 * values.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class SimilarityScoreSampling {

  /** {@link Logger} object to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(SimilarityScoreSampling.class);

  /** Primary (non-obsolete) term IDs. */
  private final List<TermId> primaryTermIds;

  /** {@link Similarity} to use for the precomputation. */
  private final Similarity similarity;

  /** Configuration for score sampling. */
  private final ScoreSamplingOptions options;

  private final Map<TermId, ? extends Collection<TermId>> labels;

  /**
   * Constructor.
   *
   * @param options Configuration for score sampling.
   *
   * @param labels {@link Map} from "world object" Id to a {@link Collection} of {@link TermId} labels.
   */
  public SimilarityScoreSampling(List<TermId> primaryTermIds, Similarity similarity, ScoreSamplingOptions options, Map<TermId, ? extends Collection<TermId>> labels) {
    this.primaryTermIds = primaryTermIds;
    this.similarity = similarity;
    // Clone configuration so it cannot be changed.
    this.options = (ScoreSamplingOptions) options.clone();
    this.labels = labels;
  }

  /**
   * Perform the sampling for all configured query term counts.
   *
   * <p>Note that in general, this will be too slow and it will be required to split the work both
   * by term count and by world object Id and distribute this to a parallel (cluster) computer.
   *

   * @return Resulting {@link Map} from query term count to precomputed {@link ScoreDistribution}.
   */
  public Map<Integer, ScoreDistribution> performSampling() {
    Map<Integer, ScoreDistribution> result = new HashMap<>();
    for (int numTerms = options.getMinNumTerms();
        numTerms <= options.getMaxNumTerms();
        ++numTerms) {
      result.put(numTerms, performSamplingForTermCount(labels, numTerms));
    }
    return result;
  }

  /**
   * Perform the sampling for a given number of terms and return the resulting {@link
   * ScoreDistribution}.
   *
   * <p>On parallelization, multiple {@link ScoreDistribution}s can be merged using {@link
   * ScoreDistributions#merge(java.util.Collection)}.
   *
   * @param labels {@link Map} from "world object" Id to a {@code Collection} of {@link TermId}
   *     labels.
   * @param numTerms Number of query terms to compute score distributions for.
   * @return Resulting {@link ScoreDistribution}.
   */
  public ScoreDistribution performSamplingForTermCount(Map<TermId, ? extends Collection<TermId>> labels, int numTerms) {
    LOGGER.info("Running precomputation for {} world objects using {} query terms...", labels.size(), numTerms);

    final ProgressReporter progressReport = new ProgressReporter("objects", labels.size());
    progressReport.start();

    // Setup the task to execute in parallel, with concurrent hash map for collecting results.
    final ConcurrentHashMap<TermId, ObjectScoreDistribution> distributions = new ConcurrentHashMap<>();
   // IntConsumer task =
    Consumer<TermId> task =
      objectId -> {
          try {
            final ObjectScoreDistribution dist =
                performComputation(objectId, labels.get(objectId), numTerms);
            distributions.put(dist.getObjectId(), dist);
            progressReport.incCurrent();
          } catch (Exception e) {
            LOGGER.error("An exception occured in parallel processing!", e);
          }
        };

    // Execution of the task in a ThreadPoolExecutor. This is the only way in Java 8 to guarantee
    // thread counts.
    //
    // It is a bit verbose but in the end, not that complicated.
    //
    // Setup thread pool executor and enforce that precisely numThreads threads are present.
    final int numThreads = options.getNumThreads();
    ThreadPoolExecutor threadPoolExecutor =
        new ThreadPoolExecutor(
            numThreads, numThreads, 5, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>());
    // Submit all chunks into the executor.
    final Iterator<TermId> objectIdIter = labels.keySet().stream().iterator();
    while (objectIdIter.hasNext()) {
      final TermId objectId = objectIdIter.next();
      threadPoolExecutor.submit(() -> task.accept(objectId));
    }
    // Shutdown executor and wait for all tasks being completed.
    threadPoolExecutor.shutdown();
    try {
      threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      throw new PhenolRuntimeException("Could not wait for thread pool being done.", e);
    }
    progressReport.stop();

    LOGGER.info("Done running precomputation.");
    // Convert from concurrent to non-concurrent hash map.
    return new ScoreDistribution(numTerms, new HashMap<>(distributions));
  }

  /**
   * Perform the sampling using the configuration, given "world object" <code>objectId</code> and
   * the given <code>terms</code> for this object.
   *
   * @param objectId "World object" id.
   * @param terms The {@link TermId}s that this object is labeled with.
   * @param numTerms Number of query terms to compute score distributions for.
   * @return Resulting {@link ObjectScoreDistribution}.
   */
  private ObjectScoreDistribution performComputation(TermId objectId, Collection<TermId> terms, int numTerms) {
    // Sample per-object score distribution
    return new ObjectScoreDistribution(
        objectId,
        numTerms,
        options.getNumIterations(),
        sampleScoreCumulativeRelFreq(terms, numTerms, options.getNumIterations()));
  }

  /**
   * Compute cumulative relative frequencies for the gene with the given "world object Id, number of
   * terms, iterations, and RNG using sampling.
   *
   * @param terms The {@link TermId}s that this object is labeled with.
   * @param numTerms Number of query terms to use for the computation.
   * @return Mapping between score and cumulative relative frequency (to use for p value
   *     computation).
   */
  private TreeMap<Double, Double> sampleScoreCumulativeRelFreq(Collection<TermId> terms, int numTerms, int numIterations) {
    // Now, perform the iterations: pick random terms, compute score, and increment absolute
    // frequency
    Map<Double, Long> counts =
        IntStream.range(0, numIterations - 1)
            .boxed()
            .map(
                i -> {
                  // Sample numTerms TermI objects from ontology.
                  final List<TermId> randomTerms = selectRandomElements(primaryTermIds, numTerms);
                  final double score = similarity.computeScore(randomTerms, terms);
                  // Round to four decimal places.
                  return Math.round(score * 1000.) / 1000.0;
                })
            .collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()));
    counts.putIfAbsent(0.0, 0L);

    // Create cumulative relative frequencies
    TreeMap<Double, Double> result = new TreeMap<>();
    for (Entry<Double, Long> entry : counts.entrySet()) {
      result.put(entry.getKey(), (double) entry.getValue());
    }
    double counter = 0.0;
    for (Entry<Double, Double> entry : result.entrySet()) {
      counter += entry.getValue();
      result.put(entry.getKey(), counter / numIterations);
    }

    return result;
  }

  /**
   * Sample <code>count</code> random elements from the given {@link List} using PRNG <code>rng
   * </code>.
   *
   * <p><a href="http://stackoverflow.com/a/41322569/84349">Taken from StackOverflow.</a>
   *
   * @param src {@link List} to sample random elements from.
   * @param count Number of elements to sample.
   * @return List of sampled elements.
   */
  private static <E> List<E> selectRandomElements(List<E> src, int count) {
    // Avoid running infinitely
    if (count >= src.size()) {
      return src;
    }

    final List<E> selected = new ArrayList<>();
    final Random random = new Random();
    final int listSize = src.size();

    // Get a random item until we got the requested amount
    while (selected.size() < count) {
      final int randomIndex = random.nextInt(listSize);
      final E element = src.get(randomIndex);
      if (!selected.contains(element)) {
        selected.add(element);
      }
    }

    return selected;
  }
}
