package de.charite.compbio.ontolib.ontology.scoredist;

import java.io.Serializable;

/**
 * Configuration for score sampling.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class ScoreSamplingOptions implements Serializable, Cloneable {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** Number of threads to use for precomputation. */
  private int numThreads = 1;

  /** Smallest "world object" ID to use, <code>null</code> for no limit */
  private Integer minObjectID = null;

  /** Largest "world object" ID to use, <code>null</code> for no limit */
  private Integer maxObjectID = null;

  /** Smallest number of terms to perform precomputation for. */
  private int minNumTerms = 1;

  /** Largest number of terms to perform precomputation for. */
  private int maxNumTerms = 20;

  /** Number of iterations to run. */
  private int numIterations = 100_000;

  /** The seed to use for the precomputations. */
  private int seed = 42;

  /**
   * Default constructor.
   *
   * <p>
   * See the setters' documentation for default values.
   * </p>
   */
  public ScoreSamplingOptions() {}

  /**
   * Construct with specific values for the options.
   *
   * @param numThreads Number of threads to use.
   * @param minObjectID Smallest "world object" ID to use or <code>null</code> for no lower bound.
   * @param maxObjectID Largest "world object" ID to use or <code>null</code> for no upper bound.
   * @param minNumTerms Smallest number of terms to compute precomputation for.
   * @param maxNumTerms Largest number of terms to compute precomputation for.
   * @param numIterations The number of iterations to run for.
   * @param seed The seed to use for the computation.
   */
  public ScoreSamplingOptions(int numThreads, Integer minObjectID, Integer maxObjectID,
      int minNumTerms, int maxNumTerms, int seed, int numIterations) {
    this.numThreads = numThreads;
    this.minObjectID = minObjectID;
    this.maxObjectID = maxObjectID;
    this.minNumTerms = minNumTerms;
    this.maxNumTerms = maxNumTerms;
    this.seed = seed;
    this.numIterations = numIterations;
  }

  /**
   * @return Number of threads to use.
   */
  public int getNumThreads() {
    return numThreads;
  }

  /**
   * Set number of threads to use for precomputation.
   *
   * <p>
   * The default is <code>1</code>.
   * </p>
   * 
   * @param numThreads Number of threads to use.
   */
  public void setNumThreads(int numThreads) {
    this.numThreads = numThreads;
  }

  /**
   * @return Smallest "world object" ID to perform computation for, <code>null</code> for no bound.
   */
  public Integer getMinObjectID() {
    return minObjectID;
  }

  /**
   * Set smallest "world object" ID to perform computation for, <code>null</code> for no bound.
   *
   * <p>
   * The default is <code>null</code>.
   * </p>
   * 
   * @param minObjectID Smallest "world object" ID to use or <code>null</code>.
   */
  public void setMinObjectID(Integer minObjectID) {
    this.minObjectID = minObjectID;
  }

  /**
   * @return Largest "world object" ID to perform computation for, <code>null</code> for no bound.
   */
  public Integer getMaxObjectID() {
    return maxObjectID;
  }

  /**
   * Set largest "world object" ID to perform computation for, <code>null</code> for no bound.
   *
   * <p>
   * The default is <code>null</code>.
   * </p>
   * 
   * @param maxObjectID Largest "world object" ID to use or <code>null</code>.
   */
  public void setMaxObjectID(Integer maxObjectID) {
    this.maxObjectID = maxObjectID;
  }

  /**
   * @return Smallest number of terms to perform precomputation for.
   */
  public int getMinNumTerms() {
    return minNumTerms;
  }

  /**
   * Set Smallest number of terms to perform precomputation for.
   *
   * <p>
   * The default is <code>1</code>.
   * </p>
   * 
   * @param minNumTerms Smallest number of terms to perform precomputation for.
   */
  public void setMinNumTerms(int minNumTerms) {
    this.minNumTerms = minNumTerms;
  }

  /**
   * @return Largest number of terms to perform precomputation for.
   */
  public int getMaxNumTerms() {
    return maxNumTerms;
  }

  /**
   * Set Smallest number of terms to perform precomputation for.
   *
   * <p>
   * The default is <code>20</code>.
   * </p>
   * 
   * @param maxNumTerms Smallest number of terms to perform precomputation for.
   */
  public void setMaxNumTerms(int maxNumTerms) {
    this.maxNumTerms = maxNumTerms;
  }



  /**
   * @return The seed used for the PRNG.
   */
  public int getSeed() {
    return seed;
  }

  /**
   * Set the seed to use for the PRNG.
   *
   * <p>
   * The default is <code>42</code>.
   * </p>
   *
   * @param seed The seed to use for the PRNG.
   */
  public void setSeed(int seed) {
    this.seed = seed;
  }

  /**
   * @return The number of iterations to run for.
   */
  public int getNumIterations() {
    return numIterations;
  }

  /**
   * Set the number of iterations to run for.
   *
   * <p>
   * The default is <code>100_000</code>.
   * </p>
   *
   * @param numIterations The number of iterations to run for.
   */
  public void setNumIterations(int numIterations) {
    this.numIterations = numIterations;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return new ScoreSamplingOptions(numThreads, minObjectID, maxObjectID, minNumTerms, maxNumTerms,
        seed, numIterations);
  }

  @Override
  public String toString() {
    return "ScoreSamplingOptions [numThreads=" + numThreads + ", minObjectID=" + minObjectID
        + ", maxObjectID=" + maxObjectID + ", minNumTerms=" + minNumTerms + ", maxNumTerms="
        + maxNumTerms + ", numIterations=" + numIterations + ", seed=" + seed + "]";
  }

}
