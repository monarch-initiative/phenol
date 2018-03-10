package org.monarchinitiative.phenol.ontology.scoredist;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableSortedMap;
import org.junit.Before;
import org.junit.Test;

public class ObjectScoreDistributionTest {

  ObjectScoreDistribution objDist;

  @Before
  public void setUp() {
    objDist =
        new ObjectScoreDistribution(1, 2, 10, ImmutableSortedMap.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9));
  }

  @Test
  public void testQueries() {
    assertEquals(2, objDist.getNumTerms());
    assertEquals(1, objDist.getObjectId());
    assertEquals(10, objDist.getSampleSize());
  }

  @Test
  public void testEstimatePValue() {
    assertEquals(1.0, objDist.estimatePValue(0.0), 0.01);
    assertEquals(0.82, objDist.estimatePValue(0.2), 0.01);
    assertEquals(0.82, objDist.estimatePValue(0.4), 0.01);
    assertEquals(0.42, objDist.estimatePValue(0.6), 0.01);
    assertEquals(0.42, objDist.estimatePValue(0.8), 0.01);
    assertEquals(0.0, objDist.estimatePValue(0.99), 0.01);
  }
}
