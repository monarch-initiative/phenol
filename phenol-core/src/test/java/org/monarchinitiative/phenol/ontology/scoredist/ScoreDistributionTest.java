package org.monarchinitiative.phenol.ontology.scoredist;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.junit.Before;
import org.junit.Test;

public class ScoreDistributionTest {

  ScoreDistribution dist;

  @Before
  public void setUp() {
    dist =
        new ScoreDistribution(
            2,
            ImmutableMap.of(
                1,
                new ObjectScoreDistribution(
                    1, 2, 10, ImmutableSortedMap.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9))));
  }

  @Test
  public void testQueries() {
    assertEquals(2, dist.getNumTerms());
    assertEquals("[1]", dist.getObjectIds().toString());
    assertEquals(1, dist.getObjectScoreDistribution(1).getObjectId());
  }
}
