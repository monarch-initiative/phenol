package org.monarchinitiative.phenol.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.monarchinitiative.phenol.cli.demo.*;
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
  private static final String HP_DEMO = "hp-demo";
  private static final String BENCHMARK = "benchmark";
  private static final String HPO_SIM = "hpo-sim";
  private static final String MONDO_DEMO = "mondo";
  private static final String MPO_ENRICH = "mpo";
  private static final String RESNIK_GENEBASED = "resnik-gene";

  public static void main(String[] argv)  {
    final PrecomputeScoresOptions precomputeScoresOptions = new PrecomputeScoresOptions();
    final MergeScoresOptions mergeScoresOptions = new MergeScoresOptions();
    final GoEnrichmentDemo.Options godemo = new GoEnrichmentDemo.Options();
    final MpEnrichmentDemo.Options mpo = new MpEnrichmentDemo.Options();
    final HpDemo.Options hpoDemo = new HpDemo.Options();
    final ParsingBenchmark.Options bench = new ParsingBenchmark.Options();
    final MondoDemo.Options mondo = new MondoDemo.Options();
    final PairwisePhenotypicSimilarityCalculator.Options hpo_sim = new PairwisePhenotypicSimilarityCalculator.Options();
    final ResnikGenebasedHpoDemo.Options resnikGeneOptions = new ResnikGenebasedHpoDemo.Options();
    final JCommander jc =
        JCommander.newBuilder()
          .addCommand(PRECOMPUTE_SCORES, precomputeScoresOptions)
          .addCommand(MERGE_SCORES, mergeScoresOptions)
          .addCommand(GO_DEMO, godemo)
          .addCommand(BENCHMARK, bench)
          .addCommand(MPO_ENRICH,mpo)
          .addCommand(HPO_SIM, hpo_sim)
          .addCommand(HP_DEMO,hpoDemo)
          .addCommand(MONDO_DEMO,mondo)
          .addCommand(RESNIK_GENEBASED, resnikGeneOptions)
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
      case HP_DEMO:
        new HpDemo(hpoDemo).run();
      case MONDO_DEMO:
        new MondoDemo(mondo).run();
      case RESNIK_GENEBASED:
        new ResnikGenebasedHpoDemo(resnikGeneOptions).run();
    }
  }
}
