package org.monarchinitiative.phenol.ontology.scoredist;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class with static helper methods related to {@link ScoreDistribution}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ScoreDistributions {

  /**
   * Merge a {@link Collection} of {@link ScoreDistribution}.
   *
   * <p>This can be used for merging results obtained in parallel. All {@link ScoreDistribution}s
   * must have the same number of terms and there must be no overlap in "world object" Ids.
   *
   * @param distributions {@link Collection} of {@link ScoreDistribution}s to merge.
   * @return Merge result.
   * @throws CannotMergeScoreDistributions In case of problems with {@code distributions}.
   */
  public static ScoreDistribution merge(Collection<ScoreDistribution> distributions) {
    if (distributions.isEmpty()) {
      throw new CannotMergeScoreDistributions("Cannot merge zero ScoreDistributions objects.");
    }
    if (distributions.stream().map(ScoreDistribution::getNumTerms).collect(Collectors.toSet()).size() != 1) {
      throw new CannotMergeScoreDistributions("Different numbers of terms used for precomputation");
    }

    Map<Integer, ObjectScoreDistribution> mapping = new HashMap<>();
    for (ScoreDistribution d : distributions) {
      for (Integer objectId : d.getObjectIds()) {
        final ObjectScoreDistribution dist = d.getObjectScoreDistribution(objectId);
        if (mapping.containsKey(objectId)) {
          throw new CannotMergeScoreDistributions("Duplicate object ID " + objectId + " detected");
        } else {
          mapping.put(objectId, dist);
        }
      }
    }

    return new ScoreDistribution(distributions.stream().findAny().get().getNumTerms(), mapping);
  }

  /**
   * Merge several {@link ScoreDistribution}s.
   *
   * @param distributions {@link ScoreDistribution}s to merge.
   * @return Merge result.
   */
  public static ScoreDistribution merge(ScoreDistribution... distributions) {
    return merge(Arrays.asList(distributions));
  }
}
