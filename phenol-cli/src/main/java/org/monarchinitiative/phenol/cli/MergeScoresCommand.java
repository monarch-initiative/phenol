package org.monarchinitiative.phenol.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.io.scoredist.H2ScoreDistributionWriter;
import org.monarchinitiative.phenol.io.scoredist.ScoreDistributionReader;
import org.monarchinitiative.phenol.io.scoredist.ScoreDistributionWriter;
import org.monarchinitiative.phenol.io.scoredist.TextFileScoreDistributionReader;
import org.monarchinitiative.phenol.io.scoredist.TextFileScoreDistributionWriter;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistributions;

/**
 * Implementation of {@code merge-scores} command.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class MergeScoresCommand {

  /** {@link Logger} object to use. */
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
      LOGGER.info("Loading {}", inputPath);
      try (final ScoreDistributionReader reader =
          new TextFileScoreDistributionReader(new File(inputPath))) {
        Map<Integer, ScoreDistribution> distributions = reader.readAll();
        for (Entry<Integer, ScoreDistribution> e : distributions.entrySet()) {
          if (!loadedDists.containsKey(e.getKey())) {
            loadedDists.put(e.getKey(), new ArrayList<>());
          }
          loadedDists.get(e.getKey()).add(e.getValue());
        }
      } catch (PhenolException | IOException e) {
        throw new PhenolRuntimeException("Problem reading input file: " + inputPath, e);
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
    LOGGER.info("Writing result...");
    try (ScoreDistributionWriter writer = buildWriter()) {
      for (Entry<Integer, ScoreDistribution> e : mergedDists.entrySet()) {
        writer.write(e.getKey(), e.getValue(), options.getResampleToPoints());
      }
    } catch (IOException | PhenolException e) {
      throw new RuntimeException("Problem writing to output file: " + options.getOutputFile(), e);
    }
    LOGGER.info("Done writing result.");
  }

  /**
   * @return {@link ScoreDistributionWriter}, depending on configured path and type.
   * @throws PhenolException in the case of problems creating the writer.
   */
  private ScoreDistributionWriter buildWriter() throws PhenolException {
    if (options.isWriteToH2()) {
      LOGGER.info("Creating H2 database connection for writing score distribution...");
      final String pathDbAbs = new File(options.getOutputFile()).getAbsolutePath();
      return new H2ScoreDistributionWriter(pathDbAbs, options.getH2TableName(), true);
    } else {
      LOGGER.info("Opening text file for writing score distribution...");
      try {
        return new TextFileScoreDistributionWriter(new File(options.getOutputFile()));
      } catch (FileNotFoundException e) {
        throw new PhenolException("Could not find file " + options.getOutputFile(), e);
      }
    }
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
