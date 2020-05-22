package org.monarchinitiative.phenol.ontology.scoredist;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Precomputed score distribution for all "world objects" and a fixed number of terms.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ScoreDistribution implements Serializable {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /** Number of terms used for precomputing scores with. */
  private final int numTerms;

  /** Mapping from "world object" Id to score distribution. */
  private final Map<TermId, ObjectScoreDistribution> objectScoreDistributions;

  /**
   * Constructor.
   *
   * @param numTerms Number of terms that this score distribution was computed for.
   * @param objectScoreDistributions {@link Map} from "world object" Id to {@link
   *     ObjectScoreDistribution}.
   */
  public ScoreDistribution(int numTerms, Map<TermId, ObjectScoreDistribution> objectScoreDistributions) {
    this.numTerms = numTerms;
    this.objectScoreDistributions = objectScoreDistributions;
  }

  /** @return The number of terms the score distributions have been precomputed with. */
  public int getNumTerms() {
    return numTerms;
  }

  /** Retrieve {@link Collection} of "world object" ids. */
  public Set<TermId> getObjectIds() {
    return objectScoreDistributions.keySet();
  }

  /**
   * Retrieve {@link ObjectScoreDistribution} for the given "world object".
   *
   * @param objectId "World object" Id to get {@link ObjectScoreDistribution} for.
   * @return The object score distributions for the given <code>objectId</code>.
   */
  public ObjectScoreDistribution getObjectScoreDistribution(TermId objectId) {
    return objectScoreDistributions.get(objectId);
  }

  @Override
  public String toString() {
    return "ScoreDistribution [numTerms="
        + numTerms
        + ", objectScoreDistributions="
        + objectScoreDistributions
        + "]";
  }
}
