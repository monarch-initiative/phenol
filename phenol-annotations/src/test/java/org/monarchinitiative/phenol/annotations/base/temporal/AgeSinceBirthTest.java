package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AgeSinceBirthTest {

  @Nested
  public class DayResolutionTests {

    @Test
    public void closedTimestamp() {
      AgeSinceBirth ageSinceBirth = AgeSinceBirth.of(10);

      assertThat(ageSinceBirth.days(), is(10));
      assertThat(ageSinceBirth.seconds(), is(0));
      assertThat(ageSinceBirth.isOpen(), is(false));
      assertThat(ageSinceBirth.isClosed(), is(true));
    }

    @ParameterizedTest
    @CsvSource({
      // check rounding of Julian years
      "  1,  0,  0,       365",
      " 10,  0,  0,      3653",
      "100,  0,  0,     36525",

      // months
      "  0,  1,  0,        30",
      "  0,  2,  0,        61",
      "  0,  3,  0,        91",
      "  0,  4,  0,       122",
      "  0, 12,  0,       365",
    })
    public void createUsingDays(int years, int months, int days, int expectedDays) {
      assertThat(AgeSinceBirth.of(years, months, days).days(), equalTo(expectedDays));
    }

    @Test
    public void reservedValuesThrowException() {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> AgeSinceBirth.of(Integer.MIN_VALUE));
      assertThat(e.getMessage(), is("Integer MIN_VALUE is reserved for open end age"));

      e = assertThrows(IllegalArgumentException.class, () -> AgeSinceBirth.of(Integer.MAX_VALUE));
      assertThat(e.getMessage(), is("Integer MAX_VALUE is reserved for open end age"));
    }

    @ParameterizedTest
    @CsvSource({
      "10, 10,  0",
      "10, 11, -1",
      "10,  9,  1",

      " 0,  0,  0",
      " 0, -1,  1",
      "-1,  0, -1",
    })
    public void compareTo(int left, int right, int expected) {
      AgeSinceBirth l = AgeSinceBirth.of(left);
      AgeSinceBirth r = AgeSinceBirth.of(right);
      assertThat(AgeSinceBirth.compare(l, r), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
      " 730500",
      "-730500",
    })
    public void compareTo_nearLimits(int days) {
      AgeSinceBirth stamp = AgeSinceBirth.of(days);
      assertThat(AgeSinceBirth.compare(stamp, AgeSinceBirth.openStart()), is(1));
      assertThat(AgeSinceBirth.compare(stamp, AgeSinceBirth.openEnd()), is(-1));
    }

    @Test
    public void compareTo_limits() {
      assertThat(AgeSinceBirth.compare(AgeSinceBirth.openStart(), AgeSinceBirth.openStart()), is(0));
      assertThat(AgeSinceBirth.compare(AgeSinceBirth.openEnd(), AgeSinceBirth.openEnd()), is(0));

      assertThat(AgeSinceBirth.compare(AgeSinceBirth.openEnd(), AgeSinceBirth.openStart()), is(1));
      assertThat(AgeSinceBirth.compare(AgeSinceBirth.openStart(), AgeSinceBirth.openEnd()), is(-1));
    }
  }

  @Nested
  public class SecondResolutionTests {

    @ParameterizedTest
    @CsvSource({
      " 10, -10",
      "-10,  10",
    })
    public void throwsIfDaysAndSecondsDoNotHaveTheSameSign(int days, int seconds) {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> AgeSinceBirth.of(days, seconds));
      assertThat(e.getMessage(), equalTo("Days and seconds must have the same sign"));
    }

    @ParameterizedTest
    @CsvSource({
      // positive
      "0,   86399,   0,  86399",
      "0,   86400,   1,  0",
      "0,   86401,   1,  1",
      "0,  172801,   2,  1",
      "0,  172801,   2,  1",

      // negative
      "0,  -86399,   0, -86399",
      "0,  -86400,  -1,  0",
      "0,  -86401,  -1, -1",
      "0, -172801,  -2, -1",
    })
    public void timestampWithOutstandingSecondsIsNormalized(int days, int seconds, int expectedDays, int expectedSeconds) {
      AgeSinceBirth duration = AgeSinceBirth.of(days, seconds);

      assertThat(duration.days(), is(expectedDays));
      assertThat(duration.seconds(), is(expectedSeconds));
    }

    @ParameterizedTest
    @CsvSource({
      // left    right      result
      " 1,   0,   2,      0,     3,    0",
      " 1,   1,   2,      3,     3,    4",
      " 1,  10,   2,  86395,     4,    5",

      "-1,   0,   2,      0,     1,    0",
      " 1,   1,  -2,     -3,    -1,   -2",
      "-1, -10,  -2, -86395,    -4,   -5",
    })
    public void plus(int leftDays, int leftSeconds, int rightDays, int rightSeconds, int days, int seconds) {
      AgeSinceBirth left = AgeSinceBirth.of(leftDays, leftSeconds);
      AgeSinceBirth right = AgeSinceBirth.of(rightDays, rightSeconds);

      assertThat(left.plus(right), equalTo(AgeSinceBirth.of(days, seconds)));
    }

    @ParameterizedTest
    @CsvSource({
      // left    result
      " 1,  3,  -1, -3",
      "-1, -5,   1,  5",
      " 0,  3,   0, -3",
      " 0, -3,   0,  3",
      " 0,  0,   0,  0",
    })
    public void negated(int inputDays, int inputSeconds, int days, int seconds) {
      AgeSinceBirth ageSinceBirth = AgeSinceBirth.of(inputDays, inputSeconds);

      assertThat(ageSinceBirth.negated(), equalTo(AgeSinceBirth.of(days, seconds)));
    }

    @ParameterizedTest
    @CsvSource({
      // left           right       result
      "1,      0,      0,     0,      left",
      "1,      0,      1,     0,      left",
      "1,      0,      1,     0,     right",
      "1,      0,      2,     0,     right",

      "1,      1,      1,     0,      left",
      "1,      1,      1,     1,      left",
      "1,      1,      1,     1,     right",
      "1,      1,      1,     2,     right",
    })
    public void max(int leftDays, int leftSeconds, int rightDays, int rightSeconds, String result) {
      AgeSinceBirth left = AgeSinceBirth.of(leftDays, leftSeconds);
      AgeSinceBirth right = AgeSinceBirth.of(rightDays, rightSeconds);

      AgeSinceBirth expected = result.equals("left") ? left : right;
      assertThat(AgeSinceBirth.max(left, right), equalTo(expected));
    }

    @ParameterizedTest
    @CsvSource({
      // left           right       result
      "1,      0,      0,     0,     right",
      "1,      0,      1,     0,     right",
      "1,      0,      1,     0,      left",
      "1,      0,      2,     0,      left",

      "1,      1,      1,     0,     right",
      "1,      1,      1,     1,     right",
      "1,      1,      1,     1,      left",
      "1,      1,      1,     2,      left",
    })
    public void min(int leftDays, int leftSeconds, int rightDays, int rightSeconds, String result) {
      AgeSinceBirth left = AgeSinceBirth.of(leftDays, leftSeconds);
      AgeSinceBirth right = AgeSinceBirth.of(rightDays, rightSeconds);

      AgeSinceBirth expected = result.equals("left") ? left : right;
      assertThat(AgeSinceBirth.min(left, right), equalTo(expected));
    }
  }

}
