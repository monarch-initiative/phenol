package org.monarchinitiative.phenol.annotations.base;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Period;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AgeConfidenceIntervalTest {

  @ParameterizedTest
  @CsvSource({
    "-P1D, P1D, P2D",
    " P0D, P1D, P1D",
    "-P1D, P0D, P1D",
    " P0D, P0D, P0D"
  })
  public void length(String lower, String upper, String expected) {
    AgeConfidenceInterval ci = AgeConfidenceInterval.of(Period.parse(lower), Period.parse(upper));
    Period length = ci.length();

    assertThat(length, equalTo(Period.parse(expected)));
  }

  @ParameterizedTest
  @CsvSource({
    " P0D, P0D, true",
    "-P1D, P1D, false",
    " P0D, P1D, false",
    "-P1D, P0D, false",
  })
  public void isPrecise(String lower, String upper, boolean expected) {
    AgeConfidenceInterval ci = AgeConfidenceInterval.of(Period.parse(lower), Period.parse(upper));

    assertThat(ci.isPrecise(), equalTo(expected));
  }

  @ParameterizedTest
  @CsvSource({
    " P0D, P0D,  P0D, P0D,   0",
    " P0D, P1D,  P0D, P1D,   0",
    "-P1D, P0D,  P0D, P1D,   0",
    " P0D, P1D,  P0D, P2D,  -1",
    "-P1D, P1D,  P0D, P1D,   1",
  })
  public void compareTo(String leftLower, String leftUpper, String rightLower, String rightUpper, int expected) {
    AgeConfidenceInterval left = AgeConfidenceInterval.of(Period.parse(leftLower), Period.parse(leftUpper));
    AgeConfidenceInterval right = AgeConfidenceInterval.of(Period.parse(rightLower), Period.parse(rightUpper));

    assertThat(left.compareTo(right), equalTo(expected));
  }
}
