package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.annotations.base.Ratio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RatioTest {

  @ParameterizedTest
  @CsvSource({
    "10,  20,   .5",
    " 1,   3,   .333333333333",
    " 3,  10,   .3",
    " 0,   1,  0.",
    "10,  10,  1."
  }
  )
  public void frequency(int numerator, int denominator, double frequency) throws Exception {
    Ratio ratio = Ratio.of(numerator, denominator);

    assertThat(ratio.numerator(), equalTo(numerator));
    assertThat(ratio.denominator(), equalTo(denominator));
    assertThat((double) ratio.frequency(), closeTo(frequency, 1e-6));
  }

  @ParameterizedTest
  @CsvSource({
    "2, 1, 'Numerator 2 must be less than or equal to denominator 1'",
    "-1, 2, 'Numerator must be non-negative'",
    "1, 0, 'Denominator must be positive'"
  })
  public void creatingWithNumeratorGreaterThanDenominatorFails(int numerator, int denominator, String expectedMessage) {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Ratio.of(numerator, denominator));
    assertThat(e.getMessage(), equalTo(expectedMessage));
  }

}
