package org.monarchinitiative.phenol.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test the MersenneTwister class (although copied from somewhere else).
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class MersenneTwisterTest {

  final long seed = 42;

  MersenneTwister rng;

  @BeforeEach
  public void setUp() {
    rng = new MersenneTwister(seed);
  }

  @Test
  public void testRun() {
    List<Double> values = new ArrayList<>();
    for (int i = 0; i < 10_000; ++i) {
      values.add(rng.nextDouble());
    }
    assertEquals(0.5, values.stream().mapToDouble(x -> (double) x).average().orElse(-1.0), 0.01);
  }
}
