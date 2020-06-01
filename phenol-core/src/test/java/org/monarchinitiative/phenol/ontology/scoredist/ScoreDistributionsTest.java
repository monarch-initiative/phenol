package org.monarchinitiative.phenol.ontology.scoredist;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class ScoreDistributionsTest {

  private ScoreDistribution dist1;
  private ScoreDistribution dist2;
  private final TermId tid1 = TermId.of("HP:1");
  private final TermId tid2 = TermId.of("HP:2");

  @BeforeEach
  public void setUp() {
    dist1 =
        new ScoreDistribution(
            2,
            ImmutableMap.of(
              tid1,
                new ObjectScoreDistribution(
                  tid1, 2, 10, ImmutableSortedMap.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9))));
    dist2 =
        new ScoreDistribution(
            2,
            ImmutableMap.of(
                tid2,
                new ObjectScoreDistribution(
                    tid2, 2, 10, ImmutableSortedMap.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9))));
  }

  @Test
  public void test() {
    ScoreDistribution result = ScoreDistributions.merge(dist1, dist2);
    assertEquals(2, result.getNumTerms());
    assertEquals("[HP:1, HP:2]", ImmutableSortedSet.copyOf(result.getObjectIds()).toString());
    assertEquals(tid1, result.getObjectScoreDistribution(tid1).getObjectId());
    assertEquals(tid2, result.getObjectScoreDistribution(tid2).getObjectId());
  }
}
