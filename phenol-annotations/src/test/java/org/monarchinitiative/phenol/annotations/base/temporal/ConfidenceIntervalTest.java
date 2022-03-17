package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConfidenceIntervalTest {

  @Test
  public void precise() {
    ConfidenceInterval precise = ConfidenceInterval.precise();
    assertThat(precise.isPrecise(), is(true));
    assertThat(precise.lowerBound(), is(AgeSinceBirth.zero()));
    assertThat(precise.upperBound(), is(AgeSinceBirth.zero()));
    assertThat(precise.length(), is(AgeSinceBirth.zero()));
  }

  @Test
  public void zeroTimestampsYieldPreciseConfidenceInterval() {
    ConfidenceInterval ci = ConfidenceInterval.of(AgeSinceBirth.zero(), AgeSinceBirth.zero());
    assertThat(ci.isPrecise(), is(true));
  }

  @Test
  public void unorderedBoundsProduceException() {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ConfidenceInterval.of(AgeSinceBirth.of(0), AgeSinceBirth.of(-1)));
    assertThat(e.getMessage(), equalTo("The lower bound [days=0,seconds=0] must not be positive and the upper bound [days=-1,seconds=0] must not be negative!"));
  }

  @ParameterizedTest
  @CsvSource({
    "-1,      0,   1,     0,    2,     0",
    "-1,      0,   1,     0,    2,     0",

    "-1, -86399,   1, 86399,    3, 86398",
    "-1, -43200,   1, 43200,    3,     0",
  })
  public void length(int lowerDays, int lowerSeconds, int upperDays, int upperSeconds, int expectedDays, int expectedSeconds) {
    ConfidenceInterval ci = ConfidenceInterval.of(AgeSinceBirth.of(lowerDays, lowerSeconds), AgeSinceBirth.of(upperDays, upperSeconds));

    AgeSinceBirth length = ci.length();

    assertThat(length.days(), is(expectedDays));
    assertThat(length.seconds(), is(expectedSeconds));
  }

  @ParameterizedTest
  @CsvSource({
    "-1,   1,   -1,  1,     0",
    "-1,   1,   -1,  2,    -1",
    "-1,   2,   -1,  1,     1",

  })
  public void compare(int leftLowerDays, int leftUpperDays, int rightLowerDays, int rightUpperDays, int expected) {
    ConfidenceInterval left = ConfidenceInterval.of(AgeSinceBirth.of(leftLowerDays), AgeSinceBirth.of(leftUpperDays));
    ConfidenceInterval right = ConfidenceInterval.of(AgeSinceBirth.of(rightLowerDays), AgeSinceBirth.of(rightUpperDays));

    assertThat(ConfidenceInterval.compare(left, right), equalTo(expected));
  }

  @ParameterizedTest
  @CsvSource({
    " 0, 0,    true",
    "-1, 1,    false",
    " 0, 1,    false",
    "-1, 0,    false",
  })
  public void isPrecise(int lower, int upper, boolean expected) {
    ConfidenceInterval ci = ConfidenceInterval.of(lower, upper);

    assertThat(ci.isPrecise(), equalTo(expected));
  }


}
