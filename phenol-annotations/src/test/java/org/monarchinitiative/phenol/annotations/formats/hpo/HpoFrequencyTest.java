package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

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

  @ParameterizedTest
  @CsvSource({
    /* cohortSize = 5 */
    "EXCLUDED,        5, 0",
    "VERY_RARE,       5, 1", /* Midpoint is 2.5%, but guard prevents rounding down to 0 */
    "OCCASIONAL,      5, 1", /* Midpoint is 17%, hence ~1   */
    "FREQUENT,        5, 3", /* Midpoint is 54.5%, hence ~3 */
    "VERY_FREQUENT,   5, 4", /* Midpoint is 89.5%, hence ~4 */
    "OBLIGATE,        5, 5",

    /* cohortSize = 10 */
    "EXCLUDED,       10, 0",
    "VERY_RARE,      10, 1", /* Midpoint is 2.5% */
    "OCCASIONAL,     10, 2", /* Midpoint is 17%, hence ~2   */
    "FREQUENT,       10, 5", /* Midpoint is 54.5%, hence ~5 */
    "VERY_FREQUENT,  10, 9", /* Midpoint is 89.5%, hence ~4 */
    "OBLIGATE,       10, 10",

    /* cohortSize = 100 */
    "EXCLUDED,      100, 0",
    "VERY_RARE,     100, 2",  /* Midpoint is  2.5% */
    "OCCASIONAL,    100, 17", /* Midpoint is 17.0%   */
    "FREQUENT,      100, 55", /* Midpoint is 54.5% */
    "VERY_FREQUENT, 100, 90", /* Midpoint is 89.5% */
    "OBLIGATE,      100, 100",
  })
  public void computeNumerator(HpoFrequency frequency, int cohortSize, int expected) {
    int actual = frequency.computeNumerator(cohortSize);

    assertThat(actual, equalTo(expected));
  }

}
