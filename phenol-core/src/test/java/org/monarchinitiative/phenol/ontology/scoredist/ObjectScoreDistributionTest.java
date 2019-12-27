package org.monarchinitiative.phenol.ontology.scoredist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableSortedMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class ObjectScoreDistributionTest {

  ObjectScoreDistribution<Integer> objDist;
  ObjectScoreDistribution<TermId> objDist2;

  @BeforeEach
  public void setUp() {
    objDist =
      new ObjectScoreDistribution<>(1, 2, 10, ImmutableSortedMap.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9));

    objDist2 =
      new ObjectScoreDistribution<>(TermId.of("HP:test"), 2, 10, ImmutableSortedMap.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9));

  }

  @Test
  public void testQueries() {
    assertEquals(2, objDist.getNumTerms());
    assertEquals(1, objDist.getObjectId().intValue());
    assertEquals(10, objDist.getSampleSize());
  }

  @Test
  public void testEstimatePValue() {
    assertEquals(1.0, objDist.estimatePValue(0.0), 0.01);
    assertEquals(1.0, objDist.estimatePValue(0.1), 0.01);
    assertEquals(0.82, objDist.estimatePValue(0.2), 0.01);
    assertEquals(0.82, objDist.estimatePValue(0.4), 0.01);
    assertEquals(0.42, objDist.estimatePValue(0.6), 0.01);
    assertEquals(0.42, objDist.estimatePValue(0.8), 0.01);
    assertEquals(0.0, objDist.estimatePValue(0.99), 0.01);
  }

  @Test
  public void testGenericId() {
    assertEquals("HP:test", objDist2.getObjectId().getValue());
  }

}
