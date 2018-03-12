package org.monarchinitiative.phenol.ontology.scoredist;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScoreSamplingOptionsTest {

  ScoreSamplingOptions samplingOptions;

  @Test
  public void testDefaultConstruction() {
    samplingOptions = new ScoreSamplingOptions();

    assertEquals(
        "ScoreSamplingOptions [numThreads=1, minObjectId=null, maxObjectId=null, minNumTerms=1, maxNumTerms=20, numIterations=100000, seed=42]",
        samplingOptions.toString());
  }

  @Test
  public void testFullConstruction() {
    samplingOptions = new ScoreSamplingOptions(1, 1, 2, 3, 4, 5, 6);
    assertEquals(
        "ScoreSamplingOptions [numThreads=1, minObjectId=1, maxObjectId=2, minNumTerms=3, maxNumTerms=4, numIterations=6, seed=5]",
        samplingOptions.toString());
  }
}
