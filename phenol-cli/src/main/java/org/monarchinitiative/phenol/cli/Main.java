package org.monarchinitiative.phenol.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.monarchinitiative.phenol.cli.demo.PairwisePhenotypicSimilarityCalculator;
import org.monarchinitiative.phenol.cli.demo.GoEnrichmentDemo;
import org.monarchinitiative.phenol.cli.demo.MpEnrichmentDemo;
import org.monarchinitiative.phenol.cli.demo.ParsingBenchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * phenol CLI implementation, main entry point.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class Main {
  /** {@link Logger} object to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  private static final String PRECOMPUTE_SCORES = "precompute-scores";
  private static final String MERGE_SCORES = "merge-scores";
  private static final String GO_DEMO = "go-demo";
  private static final String BENCHMARK = "benchmark";
  private static final String HPO_SIM="hpo-sim";
  private static final String MPO_ENRICH="mpo";

  public static void main(String[] argv)  {
    final PrecomputeScoresOptions precomputeScoresOptions = new PrecomputeScoresOptions();
    final MergeScoresOptions mergeScoresOptions = new MergeScoresOptions();
    final GoEnrichmentDemo.Options godemo = new GoEnrichmentDemo.Options();
    final ParsingBenchmark.Options bench = new ParsingBenchmark.Options();
    final PairwisePhenotypicSimilarityCalculator.Options hpo_sim = new PairwisePhenotypicSimilarityCalculator.Options();
    final JCommander jc =
        JCommander.newBuilder()
            .addCommand(PRECOMPUTE_SCORES, precomputeScoresOptions)
            .addCommand(MERGE_SCORES, mergeScoresOptions)
          .addCommand(GO_DEMO, godemo)
          .addCommand(BENCHMARK, bench)
          .addCommand(MPO_ENRICH,mpo)
          .addCommand(HPO_SIM, hpo_sim)
            .build();
    try {
      jc.parse(argv);
    } catch (ParameterException e) {
      System.err.println("[ERROR]: " + e.getMessage());
      //jc.usage();
      System.exit(1);
    }

    if (jc.getParsedCommand() == null) {
      jc.usage();
      System.exit(1);
    }

    switch (jc.getParsedCommand()) {
      case PRECOMPUTE_SCORES:
        new PrecomputeScoresCommand(precomputeScoresOptions).run();
        break;
      case MERGE_SCORES:
        new MergeScoresCommand(mergeScoresOptions).run();
        break;
      case GO_DEMO:
        new GoEnrichmentDemo(godemo).run();
        break;
      case BENCHMARK:
        new ParsingBenchmark(bench).run();
        break;
      case HPO_SIM:
        new PairwisePhenotypicSimilarityCalculator(hpo_sim).run();
    }
  }
}
