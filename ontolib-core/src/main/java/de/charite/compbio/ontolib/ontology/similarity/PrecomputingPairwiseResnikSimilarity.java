package de.charite.compbio.ontolib.ontology.similarity;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import de.charite.compbio.ontolib.utils.ProgressReporter;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
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
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class PrecomputingPairwiseResnikSimilarity<T extends Term, R extends TermRelation>
    extends AbstractPairwiseResnikSimilarity<T, R> implements Serializable {

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

  /**
   * Construct new {@link PrecomputingPairwiseResnikSimilarity}.
   *
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIc {@link Map} from{@link TermId} to its information content.
   * @param numThreads Number of threads to use for precomputation.
   */
  public PrecomputingPairwiseResnikSimilarity(Ontology<T, R> ontology, Map<TermId, Double> termToIc,
      int numThreads) {
    super(ontology, termToIc);
    this.numThreads = numThreads;
    this.precomputedScores = precomputeScores();
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
  private Map<TermId, Map<TermId, Double>> precomputeScores() {
    LOGGER.info("Precomputing pairwise scores for {} terms...",
        new Object[] {getOntology().countTerms()});

    final ProgressReporter progressReport =
        new ProgressReporter(LOGGER, "genes", getOntology().countTerms());

    // The task to execute in parallel.
    final Callable<Map<TermId, Map<TermId, Double>>> task =
        () -> getOntology().getNonObsoleteTermIds().stream().parallel().map(queryId -> {
          final Map<TermId, Double> newMap = new HashMap<>();
          for (TermId targetId : getOntology().getAllTermIds()) {
            newMap.put(targetId, computeScoreImpl(queryId, targetId));
          }

          final Map<TermId, Map<TermId, Double>> result = new HashMap<>();
          result.put(queryId, newMap);
          progressReport.incCurrent();
          return new AbstractMap.SimpleEntry<>(queryId, newMap);
        }).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

    // Perform processing in an explicit ForkJoinPool so we are able to limit the thread count
    final ForkJoinPool forkJoinPool = new ForkJoinPool(numThreads);
    final Map<TermId, Map<TermId, Double>> result;
    try {
      progressReport.start();
      result = forkJoinPool.submit(task).get();
      progressReport.stop();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException("Problem during parallel execution.", e);
    }

    LOGGER.info("Done precomputing pairwise scores.");
    return result;
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
