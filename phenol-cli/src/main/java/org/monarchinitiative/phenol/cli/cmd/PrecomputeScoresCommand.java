package org.monarchinitiative.phenol.cli.cmd;

import org.monarchinitiative.phenol.cli.demo.PrecomputeScores;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * Command line options for the {@code precompute-scores} command.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */

@CommandLine.Command(name = "precompute",
  mixinStandardHelpOptions = true,
  description = "precompute scores")
public class PrecomputeScoresCommand implements Callable<Integer> {

  @CommandLine.Option(
    names = {"-t", "--num-threads"},
    description = "Number of threads to use."
  )
  private int numThreads = 1;


  @CommandLine.Option(
    names = {"--min-num-terms"},
    description = "Minimal number of terms to precompute for."
  )
  private int minNumTerms = 1;

  @CommandLine.Option(
    names = {"--max-num-terms"},
    description = "Maximal number of terms to precompute for."
  )
  private int maxNumTerms = 20;

  @CommandLine.Option(
    names = {"--num-iterations"},
    description = "Number of iterations to run."
  )
  private int numIterations = 10_000;

  @CommandLine.Option(
    names = {"--seed"},
    description = "Seed to use for RNG."
  )
  private int seed = 42;

  @CommandLine.Option(
    names = {"--input-obo-file"},
    description = "Path to (HPO) OBO file to load.",
    required = true
  )
  private String oboFile;

  @CommandLine.Option(
    names = {"--gene-to-term-file"},
    description = "Path to gene-to-term link file.",
    required = true
  )
  private String geneToTermLinkFile;

  @CommandLine.Option(
    names = {"--output-score-dist"},
    description = "Path to output score distribution file",
    required = true
  )
  private String outputScoreDistFile;

  /** @return Return number of threads to use. */
  public int getNumThreads() {
    return numThreads;
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

  @Override
  public Integer call() {
    PrecomputeScores scores = new PrecomputeScores(this.oboFile, this.numIterations, this.seed, this.numThreads, this.outputScoreDistFile);
    scores.run();
    return 0;
  }
}
