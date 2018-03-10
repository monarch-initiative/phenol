package org.monarchinitiative.phenol.ontology.scoredist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Precomputed score distribution for a fixed number of terms and one world object Ids.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class ObjectScoreDistribution implements Serializable {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /** "World object" identifier. */
  private final int objectId;

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
   * @param objectId "World object" identifier.
   * @param numTerms Number of terms used in precomputation.
   * @param sampleSize Sample size used for precomputation.
   * @param cumulativeFrequencies Cumulative frequencies of the scores.
   */
  public ObjectScoreDistribution(
      int objectId, int numTerms, int sampleSize, SortedMap<Double, Double> cumulativeFrequencies) {
    this.objectId = objectId;
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
      if (previous == null && score < entry.getKey()) {
        return 1.0; // smaller than all
      }
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

  /** @return List of copy of observed scores, sorted ascendingly. */
  public List<Double> observedScores() {
    return new ArrayList<>(cumulativeFrequencies.keySet());
  }

  /** @return Copy of the score distribution. */
  public SortedMap<Double, Double> getCumulativeFrequencies() {
    return new TreeMap<>(cumulativeFrequencies);
  }

  /** @return The world object Id for which the score has been precomputed. */
  public int getObjectId() {
    return objectId;
  }

  /** @return The number of terms used in precomputation. */
  public int getNumTerms() {
    return numTerms;
  }

  /** @return The number of iterations (sample size) used in precomputation. */
  public int getSampleSize() {
    return sampleSize;
  }

  @Override
  public String toString() {
    return "ObjectScoreDistribution [objectId="
        + objectId
        + ", numTerms="
        + numTerms
        + ", sampleSize="
        + sampleSize
        + ", cumulativeFrequencies="
        + cumulativeFrequencies
        + "]";
  }
}
