package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class HpoFrequencyTest {

  @ParameterizedTest
  @CsvSource({
    "EXCLUDED,          .0",
    "VERY_RARE,         .025",
    "OCCASIONAL,        .17",
    "FREQUENT,          .545",
    "VERY_FREQUENT,     .895",
    "OBLIGATE,         1.",
  })
  public void frequency(HpoFrequency frequency, float expected) {
    assertThat((double) frequency.frequency(), closeTo(expected, 1e-6));
  }

  @ParameterizedTest
  @CsvSource({
    "EXCLUDED,          .0",
    "VERY_RARE,         .02",
    "OCCASIONAL,        .18",
    "FREQUENT,          .54",
    "VERY_FREQUENT,     .90",
    "OBLIGATE,         1.",
  })
  public void numeratorDenominator(HpoFrequency frequency, double expected) {
    // The expected values are approximations based on `numerator` and `denominator`.
    int numerator = frequency.numerator();
    int denominator = frequency.denominator();

    double actual = (double) numerator / denominator;

    assertThat(actual, closeTo(expected, 1e-6));
  }
}
