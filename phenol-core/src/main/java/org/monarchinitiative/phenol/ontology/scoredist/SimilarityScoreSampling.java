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

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.similarity.Similarity;
import org.monarchinitiative.phenol.utils.MersenneTwister;
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

  /** {@link Ontology} to use for computation. */
  private final Ontology ontology;

  /** {@link Similarity} to use for the precomputation. */
  private final Similarity similarity;

  /** Configuration for score sampling. */
  private final ScoreSamplingOptions options;

  // TODO: ontology already is in similarity?
  /**
   * Constructor.
   *
   * @param options Configuration for score sampling.
   */
  public SimilarityScoreSampling(
      Ontology ontology, Similarity similarity, ScoreSamplingOptions options) {
    this.ontology = ontology;
    this.similarity = similarity;
    // Clone configuration so it cannot be changed.
    this.options = (ScoreSamplingOptions) options.clone();

  }

  /**
   * Perform the sampling for all configured query term counts.
   *
   * <p>Note that in general, this will be too slow and it will be required to split the work both
   * by term count and by world object Id and distribute this to a parallel (cluster) computer.
   *
   * @param labels {@link Map} from "world object" Id to a {@link Collection} of {@link TermId}
   *     labels.
   * @return Resulting {@link Map} from query term count to precomputed {@link ScoreDistribution}.
   */
  public Map<Integer, ScoreDistribution> performSampling(
      Map<Integer, ? extends Collection<TermId>> labels) {
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
  public ScoreDistribution performSamplingForTermCount(
    Map<Integer, ? extends Collection<TermId>> labels, int numTerms) {
    LOGGER.info(
        "Running precomputation for {} world objects using {} query terms...",
        new Object[] {labels.size(), numTerms});

    // Setup progress reporting.
    final ProgressReporter progressReport = new ProgressReporter(LOGGER, "objects", labels.size());
    progressReport.start();

    // Setup the task to execute in parallel, with concurrent hash map for collecting results.
    final ConcurrentHashMap<Integer, ObjectScoreDistribution> distributions =
        new ConcurrentHashMap<>();
    Consumer<Integer> task =
        (Integer objectId) -> {
          try {
            final ObjectScoreDistribution dist =
                performComputation(objectId, labels.get(objectId), numTerms);
            distributions.put(dist.getObjectId(), dist);
            progressReport.incCurrent();
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
    // Setup thread pool executor and enforce that precisely numThreads threads are present.
    final int numThreads = options.getNumThreads();
    ThreadPoolExecutor threadPoolExecutor =
        new ThreadPoolExecutor(
            numThreads, numThreads, 5, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>());
    // Submit all chunks into the executor.
    final Iterator<Integer> objectIdIter =
        labels.keySet().stream().filter(this::selectObject).iterator();
    while (objectIdIter.hasNext()) {
      final int objectId = objectIdIter.next();
      threadPoolExecutor.submit(() -> task.accept(objectId));
    }
    // Shutdown executor and wait for all tasks being completed.
    threadPoolExecutor.shutdown();
    try {
      threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException("Could not wait for thread pool being done.", e);
    }
    progressReport.stop();

    LOGGER.info("Done running precomputation.");
    // Convert from concurrent to non-concurrent hash map.
    return new ScoreDistribution(numTerms, new HashMap<>(distributions));
  }

  /**
   * Select world object ids that are in the range selected by options.
   *
   * @param objectId Integer world object id.
   * @return Whether or not to select this world object.
   */
  private boolean selectObject(Integer objectId) {
    if (options.getMinObjectId() != null && objectId < options.getMinObjectId()) {
      return false;
    } else if (options.getMaxObjectId() != null && objectId > options.getMaxObjectId()) {
      return false;
    } else {
      return true;
    }
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
  private ObjectScoreDistribution performComputation(
    int objectId, Collection<TermId> terms, int numTerms) {
    LOGGER.info("Running precomputation for world object {}.", new Object[] {objectId});

    // Create and seed MersenneTwister
    final MersenneTwister rng = new MersenneTwister();
    rng.setSeed(options.getSeed() + objectId);

    // Sample per-object score distribution
    ObjectScoreDistribution result =
        new ObjectScoreDistribution(
            objectId,
            numTerms,
            options.getNumIterations(),
            sampleScoreCumulativeRelFreq(
                objectId, terms, numTerms, options.getNumIterations(), rng));

    LOGGER.info("Done computing precomputation for world object {}.", new Object[] {objectId});
    return result;
  }

  /**
   * Compute cumulative relative frequencies for the gene with the given "world object Id, number of
   * terms, iterations, and RNG using sampling.
   *
   * @param objectId World object Id to compute for.
   * @param terms The {@link TermId}s that this object is labeled with.
   * @param numTerms Number of query terms to use for the computation.
   * @param rng Random number generator to use.
   * @return Mapping between score and cumulative relative frequency (to use for p value
   *     computation).
   */
  private TreeMap<Double, Double> sampleScoreCumulativeRelFreq(
    int objectId, Collection<TermId> terms, int numTerms, int numIterations, Random rng) {
    final List<TermId> allTermIds = new ArrayList<>(ontology.getNonObsoleteTermIds());

    // Now, perform the iterations: pick random terms, compute score, and increment absolute
    // frequency
    Map<Double, Long> counts =
        IntStream.range(0, numIterations - 1)
            .boxed()
            .map(
                i -> {
                  // Sample numTerms TermI objects from ontology.
                  final List<TermId> randomTerms = selectRandomElements(allTermIds, numTerms, rng);
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
   * @param rng PRNG to use for random number generation.
   * @return List of sampled elements.
   */
  private static <E> List<E> selectRandomElements(List<E> src, int count, Random rng) {
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
