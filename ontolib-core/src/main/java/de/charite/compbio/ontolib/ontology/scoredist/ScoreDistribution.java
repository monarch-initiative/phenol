package de.charite.compbio.ontolib.ontology.scoredist;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Precomputed score distribution for all "world objects" and a fixed number of terms.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ScoreDistribution implements Serializable {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** Number of terms used for precomputing scores with. */
  private final int numTerms;

  /** Mapping from "world object" ID to score distribution. */
  private final Map<Integer, ObjectScoreDistribution> objectScoreDistributions;

  /**
   * Merge a {@link Collection} of {@link ScoreDistribution}.
   *
   * <p>
   * This can be used for merging results obtained in parallel. All {@link ScoreDistribution}s must
   * have the same number of terms and there must be no overlap in "world object" IDs.
   * </p>
   *
   * @param distributions {@link Collection} of {@link ScoreDistribution}s to merge.
   * @return Merge result.
   */
  public static ScoreDistribution merge(Collection<ScoreDistribution> distributions) {
    if (distributions.isEmpty()) {
      throw new RuntimeException("Cannot merge zero ScoreDistributions objects.");
    }
    if (distributions.stream().map(d -> d.getNumTerms()).collect(Collectors.toSet()).size() != 1) {
      throw new RuntimeException("Different numbers of terms used for precomputation");
    }

    Map<Integer, ObjectScoreDistribution> mapping = new HashMap<>();
    for (ScoreDistribution d : distributions) {
      for (Entry<Integer, ObjectScoreDistribution> e : d.objectScoreDistributions.entrySet()) {
        if (mapping.containsKey(e.getKey())) {
          throw new RuntimeException("Duplicate object ID " + e.getKey() + " detected");
        } else {
          mapping.put(e.getKey(), e.getValue());
        }
      }
    }

    return new ScoreDistribution(distributions.stream().findAny().get().getNumTerms(), mapping);
  }

  /**
   * Constructor.
   *
   * @param numTerms Number of terms that this score distribution was computed for.
   * @param objectScoreDistributions {@link Map} from "world object" ID to
   *        {@link ObjectScoreDistribution}.
   */
  public ScoreDistribution(int numTerms,
      Map<Integer, ObjectScoreDistribution> objectScoreDistributions) {
    this.numTerms = numTerms;
    this.objectScoreDistributions = objectScoreDistributions;
  }

  /**
   * @return The number of terms the score distributions have been precomputed with.
   */
  public int getNumTerms() {
    return numTerms;
  }

  /**
   * Retrieve {@link ObjectScoreDistribution} for the given "world object".
   *
   * @param objectID "World object" ID to get {@link ObjectScoreDistribution} for.
   * @return The object score distributions for the given <code>objectID</code>.
   */
  public ObjectScoreDistribution getObjectScoreDistribution(int objectID) {
    return objectScoreDistributions.get(objectID);
  }

}
