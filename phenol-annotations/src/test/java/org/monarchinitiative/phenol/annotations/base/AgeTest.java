package org.monarchinitiative.phenol.annotations.base;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Period;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AgeTest {

  @ParameterizedTest
  @CsvSource({
    "1, 0, 0,    1, 0, 0",
    "1, 0, 0,    1, 0, 1",
    "1, 0, 0,    1, 1, 0"
  })
  public void imprecise(int lYears, int lMonths, int lDays, int rYears, int rMonths, int rDays) {
    Period lower = Period.of(lYears, lMonths, lDays);
    Period upper = Period.of(rYears, rMonths, rDays);
    Age imprecise = Age.imprecise(lower, upper);

    assertThat(imprecise.age().isEmpty(), equalTo(true));
    assertThat(imprecise.isPrecise(), equalTo(false));
    assertThat(imprecise.lowerBound(), equalTo(lower));
    assertThat(imprecise.upperBound(), equalTo(upper));
  }

  @Test
  public void imprecise_error() {
    Period lower = Period.of(0, 1, 0);
    Period upper = Period.of(0, 0, 1);
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.imprecise(lower, upper));
    assertThat(e.getMessage(), equalTo("Lower bound P1M must be before upper bound P1D"));
  }
}
