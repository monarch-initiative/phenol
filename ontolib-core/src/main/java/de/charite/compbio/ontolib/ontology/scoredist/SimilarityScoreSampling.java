package de.charite.compbio.ontolib.ontology.scoredist;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import de.charite.compbio.ontolib.ontology.similarity.Similarity;
import de.charite.compbio.ontolib.utils.MersenneTwister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sampling algorithm for similarity scores.
 *
 * <p>
 * The resulting precomputed {@link ScoreDistribution} can be used for empirical estimation of p
 * values.
 * </p>
 *
 * @param <T> {@link Term} sub class this <code>Ontology</code> uses.
 * @param <R> {@link TermRelation} sub class this <code>Ontology</code> uses.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class SimilarityScoreSampling<T extends Term, R extends TermRelation> {

  /**
   * {@link Logger} object to use.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(SimilarityScoreSampling.class);

  /**
   * {@link Ontology} to use for computation.
   */
  private final Ontology<T, R> ontology;

  /**
   * {@link Similarity} to use for the precomputation.
   */
  private final Similarity similarity;

  /** Configuration for score sampling. */
  private final ScoreSamplingOptions options;

  // TODO: ontology already is in similarity?
  /**
   * Constructor.
   *
   * @param options Configuration for score sampling.
   */
  public SimilarityScoreSampling(Ontology<T, R> ontology, Similarity similarity,
      ScoreSamplingOptions options) {
    this.ontology = ontology;
    this.similarity = similarity;
    // Clone configuration so it cannot be changed.
    try {
      this.options = (ScoreSamplingOptions) options.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException("This cannot happen.");
    }
  }

  /**
   * Perform the sampling for all configured query term counts.
   *
   * <p>
   * Note that in general, this will be too slow and it will be required to split the work both by
   * term count and by world object Id and distribute this to a parallel (cluster) computer.
   * </p>
   *
   * @param labels {@link Map} from "world object" Id to a {@link Collection} of {@link TermId}
   *        labels.
   * @return Resulting {@link Map} from query term count to precomputed {@link ScoreDistribution}.
   */
  public Map<Integer, ScoreDistribution> performSampling(
      Map<Integer, ? extends Collection<TermId>> labels) {
    Map<Integer, ScoreDistribution> result = new HashMap<>();
    for (int numTerms = options.getMinNumTerms(); numTerms <= options
        .getMaxNumTerms(); ++numTerms) {
      result.put(numTerms, performSamplingForTermCount(labels, numTerms));
    }
    return result;
  }

  /**
   * Perform the sampling for a given number of terms and return the resulting
   * {@link ScoreDistribution}.
   *
   * <p>
   * On parallelization, multiple {@link ScoreDistribution}s can be merged using
   * {@link ScoreDistribution#merge(java.util.Collection)}.
   * </p>
   *
   * @param labels {@link Map} from "world object" Id to a {@link Collection} of {@link TermId}
   *        labels.
   * @param numTerms Number of query terms to compute score distributions for.
   * @return Resulting {@link ScoreDistribution}.
   */
  public ScoreDistribution performSamplingForTermCount(
      Map<Integer, ? extends Collection<TermId>> labels, int numTerms) {
    LOGGER.info("Running precomputation for {} world objects using {} query terms...",
        new Object[] {labels.size(), numTerms});

    final int numObjects = labels.size();
    final AtomicInteger progress = new AtomicInteger();

    // Perform processing in an explicit ForkJoinPool so we are able to limit the thread count
    final ForkJoinPool forkJoinPool = new ForkJoinPool(options.getNumThreads());
    final Map<Integer, ObjectScoreDistribution> distributions;
    try {
      final Callable<Map<Integer, ObjectScoreDistribution>> task =
          () -> labels.entrySet().stream().filter(this::selectLabel).parallel()
              .map(
                  e -> performComputation(e.getKey(), e.getValue(), numTerms, progress, numObjects))
              .collect(Collectors.toMap(ObjectScoreDistribution::getObjectId, Function.identity()));
      distributions = forkJoinPool.submit(task).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException("Problem during parallel execution.", e);
    }

    LOGGER.info("Done running precomputation.");
    return new ScoreDistribution(numTerms, distributions);
  }

  /**
   * Select labels of world objects that fall into the range to precompute.
   *
   * @param e Entry from world object Id map to collection of {@link TermId}s.
   * @return Whether or not to select this label.
   */
  private boolean selectLabel(Entry<Integer, ? extends Collection<TermId>> e) {
    final int objectId = e.getKey();
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
   * @param progress Increased for each completed computation.
   * @param maxProgress Total number of computations to perform.
   * @return Resulting {@link ObjectScoreDistribution}.
   */
  private ObjectScoreDistribution performComputation(int objectId, Collection<TermId> terms,
      int numTerms, AtomicInteger progress, int maxProgress) {
    LOGGER.info("Running precomputation for world object {}.", new Object[] {objectId});

    // Create and seed MersenneTwister
    final MersenneTwister rng = new MersenneTwister();
    rng.setSeed(options.getSeed() + objectId);

    // Sample per-object score distribution
    ObjectScoreDistribution result = new ObjectScoreDistribution(objectId, numTerms,
        options.getNumIterations(),
        sampleScoreCumulativeRelFreq(objectId, terms, numTerms, options.getNumIterations(), rng));

    // Update progress
    final int progressVal = progress.incrementAndGet();
    if (progressVal > 0 && (progressVal % 100 == 0 || progressVal == maxProgress)) {
      LOGGER.info("Processed %d of %d terms (%d %%)",
          new Object[] {progressVal, maxProgress, (100.0 * progressVal) / maxProgress});
    }

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
   *         computation).
   */
  private TreeMap<Double, Double> sampleScoreCumulativeRelFreq(int objectId,
      Collection<TermId> terms, int numTerms, int numIterations, Random rng) {
    final List<TermId> allTermIds = new ArrayList<TermId>(ontology.getNonObsoleteTermIds());

    // Now, perform the iterations: pick random terms, compute score, and increment absolute
    // frequency
    Map<Double, Long> counts = IntStream.range(0, numIterations - 1).boxed().parallel().map(i -> {
      // Sample numTerms Term objects from ontology.
      final List<TermId> randomTerms = selectRandomElements(allTermIds, numTerms, rng);
      final double score = similarity.computeScore(randomTerms, terms);
      // Round to four decimal places.
      return Math.round(score * 1000.) / 1000.0;
    }).collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()));
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
   * Sample <code>count</code> random elements from the given {@link List} using PRNG
   * <code>rng</code>.
   *
   * <p>
   * <a href="http://stackoverflow.com/a/41322569/84349">Taken from StackOverflow.</a>
   * </p>
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
