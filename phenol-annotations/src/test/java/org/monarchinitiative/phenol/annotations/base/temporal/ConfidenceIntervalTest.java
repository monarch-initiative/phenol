package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfidenceIntervalTest {

  @Test
  public void basicPropertiesOfPreciseConfidenceInterval() {
    ConfidenceInterval singleton = ConfidenceInterval.precise();
    assertThat(singleton.lowerBound(), equalTo(0));
    assertThat(singleton.upperBound(), equalTo(0));
    assertThat(singleton.isPrecise(), equalTo(true));
    assertThat(singleton.isImprecise(), equalTo(false));
    assertThat(singleton.length(), equalTo(0));

    assertThat(ConfidenceInterval.of(0, 0), is(sameInstance(singleton)));
  }

  @Test
  public void basicPropertiesOfImpreciseConfidenceInterval() {
    ConfidenceInterval ci = ConfidenceInterval.of(-5, 10);
    assertThat(ci.lowerBound(), equalTo(-5));
    assertThat(ci.upperBound(), equalTo(10));
    assertThat(ci.isPrecise(), equalTo(false));
    assertThat(ci.isImprecise(), equalTo(true));
    assertThat(ci.length(), equalTo(15));

    assertThat(ConfidenceInterval.of(-5, 10), is(not(sameInstance(ci))));
  }

  @Test
  public void asTemporalInterval() {
    ConfidenceInterval ci = ConfidenceInterval.of(-5, 10);
    // note, it does not matter what CI the age has. We're testing the `ConfidenceInterval` here (hence precise).
    TemporalInterval ti = ci.asTemporalInterval(Age.of(10, false, ConfidenceInterval.precise()));
    assertThat(ti.start(), equalTo(Age.postnatal(5)));
    assertThat(ti.end(), equalTo(Age.postnatal(20)));
  }

  @ParameterizedTest
  @CsvSource({
    " 1,  2",
    "-1, -2",
  })
  public void failsOnWrongBoundSign(int lower, int upper) {
    //noinspection ResultOfMethodCallIgnored
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> ConfidenceInterval.of(lower, upper));
    assertThat(e.getMessage(), containsString(String.format("'%d, %d' ConfidenceInterval must have non-positive lowerBound and non-negative upperBound!", lower, upper)));
  }

  @ParameterizedTest
  @CsvSource({
    "-5, 10,  -5, 10,   0",
    "-5, 10, -15,  0,   0",
    "-5, 10,  -5, 15,   1",
    "-5, 15,  -5, 10,  -1",
  })
  public void compareTo(int xLower, int xUpper, int yLower, int yUpper, int expected) {
    ConfidenceInterval x = ConfidenceInterval.of(xLower, xUpper);
    ConfidenceInterval y = ConfidenceInterval.of(yLower, yUpper);
    assertThat(x.compareTo(y), equalTo(expected));
  }

}
