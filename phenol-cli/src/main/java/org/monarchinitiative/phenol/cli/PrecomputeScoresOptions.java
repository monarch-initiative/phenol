package org.monarchinitiative.phenol.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * Command line options for the {@code precompute-scores} command.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
@Parameters(separators = "=", commandDescription = "Precompute score distributions")
public class PrecomputeScoresOptions {

  @Parameter(
    names = {"-t", "--num-threads"},
    description = "Number of threads to use."
  )
  private int numThreads = 1;

  @Parameter(
    names = {"--min-object-id"},
    description = "Smallest object ID to process, if given."
  )
  private Integer minObjectId;

  @Parameter(
    names = {"--max-object-id"},
    description = "Largest object ID to process, if any."
  )
  private Integer maxObjectId;

  @Parameter(
    names = {"--min-num-terms"},
    description = "Minimal number of terms to precompute for."
  )
  private int minNumTerms = 1;

  @Parameter(
    names = {"--max-num-terms"},
    description = "Maximal number of terms to precompute for."
  )
  private int maxNumTerms = 20;

  @Parameter(
    names = {"--num-iterations"},
    description = "Number of iterations to run."
  )
  private int numIterations = 10_000;

  @Parameter(
    names = {"--seed"},
    description = "Seed to use for RNG."
  )
  private int seed = 42;

  @Parameter(
    names = {"--input-obo-file"},
    description = "Path to (HPO) OBO file to load.",
    required = true
  )
  private String oboFile;

  @Parameter(
    names = {"--gene-to-term-file"},
    description = "Path to gene-to-term link file.",
    required = true
  )
  private String geneToTermLinkFile;

  @Parameter(
    names = {"--output-score-dist"},
    description = "Path to output score distribution file",
    required = true
  )
  private String outputScoreDistFile;

  /** @return Return number of threads to use. */
  public int getNumThreads() {
    return numThreads;
  }

  /** @return The smallest object ID to precompute for, {@code null} for no bound. */
  public Integer getMinObjectId() {
    return minObjectId;
  }

  /** @return The largest object ID to precompute for, {@code null} for no bound. */
  public Integer getMaxObjectId() {
    return maxObjectId;
  }

  /** @return The smallest number of terms to precompute for. */
  public int getMinNumTerms() {
    return minNumTerms;
  }

  /** @return The largest number of terms to precompute for. */
  public int getMaxNumTerms() {
    return maxNumTerms;
  }

  /** @return The number of iterations to perform. */
  public int getNumIterations() {
    return numIterations;
  }

  /** @return The seed for RNG. */
  public int getSeed() {
    return seed;
  }

  /** @return Path to the input OBO file. */
  public String getOboFile() {
    return oboFile;
  }

  /** @return Path to the gene-to-term link file. */
  public String getGeneToTermLinkFile() {
    return geneToTermLinkFile;
  }

  /** @return Path to output score distribution file to write. */
  public String getOutputScoreDistFile() {
    return outputScoreDistFile;
  }

  @Override
  public String toString() {
    return "PrecomputeScoresOptions [numThreads="
        + numThreads
        + ", minObjectId="
        + minObjectId
        + ", maxObjectId="
        + maxObjectId
        + ", minNumTerms="
        + minNumTerms
        + ", maxNumTerms="
        + maxNumTerms
        + ", numIterations="
        + numIterations
        + ", seed="
        + seed
        + ", oboFile="
        + oboFile
        + ", geneToTermLinkFile="
        + geneToTermLinkFile
        + ", outputScoreDistFile="
        + outputScoreDistFile
        + "]";
  }
}
