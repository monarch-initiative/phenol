package de.charite.compbio.ontolib.ontology.scoredist;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.Map.Entry;

/**
 * Precomputed score distribution for a fixed number of terms and one world object IDs.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class ObjectScoreDistribution implements Serializable {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** "World object" identifier. */
  private final int objectID;

  /** Number of terms used for precomputing scores with. */
  private final int numTerms;

  /** Number of iterations for sampling. */
  private final int sampleSize;

  // TODO: maybe better replace by arrays?
  /** Cumulative frequencies for the score. */
  private final SortedMap<Double, Double> cumulativeFrequencies;

  /**
   * Construct score distribution for a given number of terms, sample size, and cumulative
   * frequencies.
   * 
   * @param objectID "World object" identifier.
   * @param numTerms Number of terms used in precomputation.
   * @param sampleSize Sample size used for precomputation.
   * @param cumulativeFrequencies Cumulative frequencies of the scores.
   */
  public ObjectScoreDistribution(int objectID, int numTerms, int sampleSize,
      SortedMap<Double, Double> cumulativeFrequencies) {
    this.objectID = objectID;
    this.numTerms = numTerms;
    this.sampleSize = sampleSize;
    this.cumulativeFrequencies = cumulativeFrequencies;
  }

  /**
   * Estimate p value from the given score.
   *
   * @param score The score to estimate p value for
   * @return Empirically estimated p value.
   */
  public double estimatePValue(double score) {
    Entry<Double, Double> previous = null;
    for (Entry<Double, Double> entry : cumulativeFrequencies.entrySet()) {
      if (previous != null && previous.getKey() <= score && score < entry.getKey()) {
        // interpolate and return
        final double dx = (entry.getKey() - previous.getKey()) / 2.0;
        return 1 - (previous.getValue() + dx * (entry.getValue() - previous.getValue()));
      }
      previous = entry;
    }
    // If we reach here, p value is 0.0
    return 0.0;
  }

  /**
   * @return The world object ID for which the score has been precomputed.
   */
  public int getObjectID() {
    return objectID;
  }

  /**
   * @return The number of terms used in precomputation.
   */
  public int getNumTerms() {
    return numTerms;
  }

  /**
   * @return The number of iterations (sample size) used in precomputation.
   */
  public int getSampleSize() {
    return sampleSize;
  }

}
