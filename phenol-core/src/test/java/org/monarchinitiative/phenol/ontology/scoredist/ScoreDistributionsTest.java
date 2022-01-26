package org.monarchinitiative.phenol.ontology.scoredist;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.TreeMap;

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
            Map.of(
              tid1,
                new ObjectScoreDistribution(
                  tid1, 2, 10, new TreeMap<>(Map.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9)))));
    dist2 =
        new ScoreDistribution(
            2,
            Map.of(
                tid2,
                new ObjectScoreDistribution(
                    tid2, 2, 10, new TreeMap<>(Map.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9)))));
  }

  @Test
  public void test() {
    ScoreDistribution result = ScoreDistributions.merge(dist1, dist2);
    assertEquals(2, result.getNumTerms());
    assertThat(result.getObjectIds(), hasItems(TermId.of("HP:1"), TermId.of("HP:2")));
    assertEquals(tid1, result.getObjectScoreDistribution(tid1).getObjectId());
    assertEquals(tid2, result.getObjectScoreDistribution(tid2).getObjectId());
  }
}
