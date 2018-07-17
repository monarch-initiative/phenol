package org.monarchinitiative.phenol.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.monarchinitiative.phenol.base.PhenolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OntoLib CLI implementation, main entry point.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class Main {
  /** {@link Logger} object to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  private static final String PRECOMPUTE_SCORES = "precompute-scores";
  private static final String MERGE_SCORES = "merge-scores";

  public static void main(String[] argv) throws PhenolException {
    final PrecomputeScoresOptions precomputeScoresOptions = new PrecomputeScoresOptions();
    final MergeScoresOptions mergeScoresOptions = new MergeScoresOptions();
    final JCommander jc =
        JCommander.newBuilder()
            .addCommand(PRECOMPUTE_SCORES, precomputeScoresOptions)
            .addCommand(MERGE_SCORES, mergeScoresOptions)
            .build();
    try {
      jc.parse(argv);
    } catch (ParameterException e) {
      LOGGER.error("ERROR: " + e.getMessage());
      jc.usage();
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
    }
  }
}
