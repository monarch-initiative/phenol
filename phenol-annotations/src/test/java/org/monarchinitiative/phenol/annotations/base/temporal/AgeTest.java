package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;

public class AgeTest {


  @Test
  public void imprecise() {

    Age imprecise = Age.of(Timestamp.zero(), ConfidenceInterval.of(-1, 1));

    assertThat(imprecise.isPrecise(), equalTo(false));
    assertThat(imprecise.confidenceInterval().lowerBound(), equalTo(Timestamp.of(-1)));
    assertThat(imprecise.confidenceInterval().upperBound(), equalTo(Timestamp.of(1)));
  }

  @Test
  public void imprecise_error() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.of(Timestamp.zero(), ConfidenceInterval.of(1, 1)));
    assertThat(e.getMessage(), equalTo("The lower bound [days=1,seconds=0] must not be positive and the upper bound [days=1,seconds=0] must not be negative!"));
  }

}
