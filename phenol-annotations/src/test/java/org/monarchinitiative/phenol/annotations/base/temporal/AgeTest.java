package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;

public class AgeTest {

  @Test
  public void birth() {
    Age birth = Age.birth();
    assertThat(birth.isPrecise(), is(true));
    assertThat(birth.timestamp(), is(AgeSinceBirth.zero()));
  }

  @Test
  public void precise() {
    Age precise = Age.of(AgeSinceBirth.of(1));
    assertThat(precise.isPrecise(), is(true));
    assertThat(precise.timestamp(), equalTo(AgeSinceBirth.of(1)));

    precise = Age.of(AgeSinceBirth.of(1), ConfidenceInterval.precise());
    assertThat(precise.isPrecise(), is(true));
    assertThat(precise.timestamp(), equalTo(AgeSinceBirth.of(1)));
  }

  @Test
  public void imprecise() {
    Age imprecise = Age.of(AgeSinceBirth.zero(), ConfidenceInterval.of(-1, 1));

    assertThat(imprecise.isPrecise(), equalTo(false));
    assertThat(imprecise.confidenceInterval().lowerBound(), equalTo(AgeSinceBirth.of(-1)));
    assertThat(imprecise.confidenceInterval().upperBound(), equalTo(AgeSinceBirth.of(1)));
  }

  @Test
  public void imprecise_error() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.of(AgeSinceBirth.zero(), ConfidenceInterval.of(1, 1)));
    assertThat(e.getMessage(), equalTo("The lower bound [days=1,seconds=0] must not be positive and the upper bound [days=1,seconds=0] must not be negative!"));
  }

}
