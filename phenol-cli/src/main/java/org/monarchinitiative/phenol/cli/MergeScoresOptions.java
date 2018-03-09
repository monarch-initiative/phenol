package org.monarchinitiative.phenol.cli;

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

  @Parameter(names = "--output", description = "Output file to write to", required = true)
  private String outputFile;

  @Parameter(
    names = "--write-to-h2",
    description = "Write out to H2 database instead of text file."
  )
  private boolean writeToH2 = false;

  @Parameter(
    names = "--h2-table-name",
    description = "Name of table in H2 database when writing to H2 db."
  )
  private String h2TableName = "phenix_score_distribution";

  @Parameter(
    names = "--resample-to-points",
    description =
        "Distribution will be resampled to this number "
            + "of points (0 for no resampling, default)"
  )
  private int resampleToPoints = 0;

  /** @return The input files to read. */
  public List<String> getInputFiles() {
    return inputFiles;
  }

  /** @return The output file to write. */
  public String getOutputFile() {
    return outputFile;
  }

  /** @return Whether or not to write to H2 database. */
  public boolean isWriteToH2() {
    return writeToH2;
  }

  /** @return Name of table in H2 database when writing to H2 database. */
  public String getH2TableName() {
    return h2TableName;
  }

  /** @return The number of points to resample to. */
  public int getResampleToPoints() {
    return resampleToPoints;
  }

  @Override
  public String toString() {
    return "MergeScoresOptions [inputFiles="
        + inputFiles
        + ", outputFile="
        + outputFile
        + ", writeToH2="
        + writeToH2
        + ", h2TableName="
        + h2TableName
        + ", resampleToPoints="
        + resampleToPoints
        + "]";
  }
}
