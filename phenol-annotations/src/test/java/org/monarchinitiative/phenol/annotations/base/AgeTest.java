package org.monarchinitiative.phenol.annotations.base;

import org.junit.jupiter.api.Test;

import java.time.Period;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AgeTest {

  @Test
  public void imprecise() {
    Period lower = Period.parse("-P1Y");
    Period upper = Period.parse("P1Y");
    Age imprecise = Age.of(Period.parse("P0Y"), AgeConfidenceInterval.of(lower, upper));

    assertThat(imprecise.isPrecise(), equalTo(false));
    assertThat(imprecise.confidenceInterval().lowerBound(), equalTo(lower));
    assertThat(imprecise.confidenceInterval().upperBound(), equalTo(upper));
  }

  @Test
  public void imprecise_error() {
    Period lower = Period.of(0, 0, 1);
    Period upper = Period.of(0, 0, 1);
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.of(Period.ZERO, AgeConfidenceInterval.of(lower, upper)));
    assertThat(e.getMessage(), equalTo("The lower bound P1D must be negative and the upper bound P1D must be positive!"));
  }
}
