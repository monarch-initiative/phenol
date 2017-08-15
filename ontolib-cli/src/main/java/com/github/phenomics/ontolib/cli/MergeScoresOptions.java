package com.github.phenomics.ontolib.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.util.ArrayList;
import java.util.List;

/**
 * Command line options for the {@code merge-scores} command.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Parameters(separators = "=", commandDescription = "Merge score distribution files")
public class MergeScoresOptions {

  @Parameter(names = "--input", description = "Input files to merge", required = true)
  private List<String> inputFiles = new ArrayList<>();

  @Parameter(names = "--output", description = "Input files to merge", required = true)
  private String outputFile;

  /**
   * @return The input files to read.
   */
  public List<String> getInputFiles() {
    return inputFiles;
  }

  /**
   * @return The output file to write.
   */
  public String getOutputFile() {
    return outputFile;
  }

  @Override
  public String toString() {
    return "MergeScoresOptions [inputFiles=" + inputFiles + ", outputFile=" + outputFile + "]";
  }

}
