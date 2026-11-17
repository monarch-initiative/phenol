package org.monarchinitiative.phenol.annotations.io.hpo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PercentageUtilTest {

  @ParameterizedTest
  @CsvSource({
    /* cohortSize = 5 */
    "0 ,        5,   0",
    "10,        5,   1",
    "20,        5,   1",
    "30,        5,   2",
    "40,        5,   2",
    "50,        5,   3",

    /* cohortSize = 10 */
    "0,         10,   0",
    "1,         10,   1",
    "2,         10,   1",
    "14,        10,   1",
    "15,        10,   2",
    "18,        10,   2",
    "20,        10,   2",

    /* cohortSize = 100 */
    "0,        100,   0",
    "1,        100,   1",
    "99,       100,  99",
    "100,      100, 100",
  })
  public void computeNumerator(float percentage, int cohortSize, int expected) {
    int actual = PercentageUtil.computeNumerator(percentage, cohortSize);

    assertThat(actual, equalTo(expected));
  }
}
