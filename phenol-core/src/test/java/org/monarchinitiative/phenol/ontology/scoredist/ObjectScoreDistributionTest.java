package org.monarchinitiative.phenol.ontology.scoredist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.TreeMap;

public class ObjectScoreDistributionTest {

  private ObjectScoreDistribution objDist;
  private final int numTerms = 2;
  private final int sampleSize = 10;

  @BeforeEach
  public void setUp() {
    objDist =
      new ObjectScoreDistribution(TermId.of("HP:test"), numTerms, sampleSize, new TreeMap<>(Map.of(0.1, 0.1, 0.5, 0.5, 0.9, 0.9)));

  }

  @Test
  public void testQueries() {
    assertEquals(numTerms, objDist.getNumTerms());
    assertEquals(TermId.of("HP:test"), objDist.getObjectId());
    assertEquals(sampleSize, objDist.getSampleSize());
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


}
