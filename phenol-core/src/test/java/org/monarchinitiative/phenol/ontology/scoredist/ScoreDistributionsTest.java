package org.monarchinitiative.phenol.ontology.scoredist;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import org.junit.Before;
import org.junit.Test;

public class ScoreDistributionsTest {

  ScoreDistribution dist1;
  ScoreDistribution dist2;

  @Before
  public void setUp() {
    dist1 =
        new ScoreDistribution(
            2,
            ImmutableMap.of(
                1,
                new ObjectScoreDistribution(
                    1, 2, 10, ImmutableSortedMap.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9))));
    dist2 =
        new ScoreDistribution(
            2,
            ImmutableMap.of(
                2,
                new ObjectScoreDistribution(
                    2, 2, 10, ImmutableSortedMap.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9))));
  }

  @Test
  public void test() {
    ScoreDistribution result = ScoreDistributions.merge(dist1, dist2);
    assertEquals(2, result.getNumTerms());
    assertEquals("[1, 2]", ImmutableSortedSet.copyOf(result.getObjectIds()).toString());
    assertEquals(1, result.getObjectScoreDistribution(1).getObjectId());
    assertEquals(2, result.getObjectScoreDistribution(2).getObjectId());
  }
}
