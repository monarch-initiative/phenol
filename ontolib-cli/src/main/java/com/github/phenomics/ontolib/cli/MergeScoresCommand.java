package com.github.phenomics.ontolib.cli;

import com.github.phenomics.ontolib.ontology.scoredist.ScoreDistribution;
import com.github.phenomics.ontolib.ontology.scoredist.ScoreDistributions;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@code merge-scores} command.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class MergeScoresCommand {

  /**
   * {@link Logger} object to use.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(MergeScoresCommand.class);

  /** Configuration parsed from command line. */
  private MergeScoresOptions options;

  /** The loaded distributions. */
  private SortedMap<Integer, List<ScoreDistribution>> loadedDists = new TreeMap<>();

  /** The merged distributions. */
  private SortedMap<Integer, ScoreDistribution> mergedDists = new TreeMap<>();

  public MergeScoresCommand(MergeScoresOptions options) {
    this.options = options;
  }

  /** Execute the command. */
  public void run() {
    printHeader();

    loadDistributions();
    mergeDistributions();
    writeResult();

    printFooter();
  }

  private void loadDistributions() {
    LOGGER.info("Loading distributions...");

    for (String inputPath : options.getInputFiles()) {
      try (ScoreDistributionReader reader = new ScoreDistributionReader(new File(inputPath))) {
        Map<Integer, ScoreDistribution> distributions = reader.readAll();
        for (Entry<Integer, ScoreDistribution> e : distributions.entrySet()) {
          if (!loadedDists.containsKey(e.getKey())) {
            loadedDists.put(e.getKey(), new ArrayList<>());
          }
          loadedDists.get(e.getKey()).add(e.getValue());
        }
      } catch (IOException e) {
        throw new RuntimeException("Problem reading input file: " + inputPath, e);
      }
    }

    LOGGER.info("Done loading distributions.");
  }


  private void mergeDistributions() {
    LOGGER.info("Merging distributions...");

    for (Entry<Integer, List<ScoreDistribution>> e : loadedDists.entrySet()) {
      mergedDists.put(e.getKey(), ScoreDistributions.merge(e.getValue()));
    }

    LOGGER.info("Done merging distributions.");
  }


  private void writeResult() {
    LOGGER.info("Writing result file...");

    try (ScoreDistributionWriter writer =
        new ScoreDistributionWriter(new File(options.getOutputFile()))) {
      for (Entry<Integer, ScoreDistribution> e : mergedDists.entrySet()) {
        writer.write(e.getKey(), e.getValue(), 0);
      }
    } catch (IOException e) {
      throw new RuntimeException("Problem writing to output file: " + options.getOutputFile(), e);
    }

    LOGGER.info("Done writing result file.");
  }

  private void printHeader() {
    LOGGER.info("OntoLib CLI -- Merging Score Distribution Files");
    LOGGER.info("");
    LOGGER.info("Options");
    LOGGER.info("=======");
    LOGGER.info("");
    LOGGER.info(options.toString());
  }

  private void printFooter() {
    LOGGER.info("All Done.\nHave a nice day!\n");
  }


}
