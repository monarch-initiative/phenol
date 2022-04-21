package org.monarchinitiative.phenol.ontology.scoredist;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.TreeMap;

public class ScoreDistributionTest {

  ScoreDistribution dist;

  private final TermId tid1 = TermId.of("HP:1");

  @BeforeEach
  public void setUp() {
    dist =
        new ScoreDistribution(
            2,
            Map.of(
              tid1,
                new ObjectScoreDistribution(
                  tid1, 2, 10, new TreeMap<>(Map.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9)))));
  }

  @Test
  public void testQueries() {
    assertEquals(2, dist.getNumTerms());
    assertEquals("[HP:1]", dist.getObjectIds().toString());
    assertEquals(tid1, dist.getObjectScoreDistribution(tid1).getObjectId());
  }
}
