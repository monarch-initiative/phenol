package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TimestampTest {

  @Nested
  public class DayResolutionTests {

    @Test
    public void closedTimestamp() {
      Timestamp timestamp = Timestamp.of(10);

      assertThat(timestamp.days(), is(10));
      assertThat(timestamp.seconds(), is(0));
      assertThat(timestamp.isOpen(), is(false));
      assertThat(timestamp.isClosed(), is(true));
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
      assertThat(Timestamp.of(years, months, days).days(), equalTo(expectedDays));
    }

    @Test
    public void reservedValuesThrowException() {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Timestamp.of(Integer.MIN_VALUE));
      assertThat(e.getMessage(), is("Integer MIN_VALUE is reserved for open end timestamp"));

      e = assertThrows(IllegalArgumentException.class, () -> Timestamp.of(Integer.MAX_VALUE));
      assertThat(e.getMessage(), is("Integer MAX_VALUE is reserved for open end timestamp"));
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
      Timestamp l = Timestamp.of(left);
      Timestamp r = Timestamp.of(right);
      assertThat(Timestamp.compare(l, r), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
      " 730500",
      "-730500",
    })
    public void compareTo_nearLimits(int days) {
      Timestamp stamp = Timestamp.of(days);
      assertThat(Timestamp.compare(stamp, Timestamp.openStart()), is(1));
      assertThat(Timestamp.compare(stamp, Timestamp.openEnd()), is(-1));
    }

    @Test
    public void compareTo_limits() {
      assertThat(Timestamp.compare(Timestamp.openStart(), Timestamp.openStart()), is(0));
      assertThat(Timestamp.compare(Timestamp.openEnd(), Timestamp.openEnd()), is(0));

      assertThat(Timestamp.compare(Timestamp.openEnd(), Timestamp.openStart()), is(1));
      assertThat(Timestamp.compare(Timestamp.openStart(), Timestamp.openEnd()), is(-1));
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
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Timestamp.of(days, seconds));
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
      Timestamp duration = Timestamp.of(days, seconds);

      assertThat(duration.days(), is(expectedDays));
      assertThat(duration.seconds(), is(expectedSeconds));
    }

    @ParameterizedTest
    @CsvSource({
      // left           right                expected
      " 1,      0,      2,      0,            3,     0",
      " 1,  86399,      2,  86399,            4, 86398",
      "-1, -86399,      1,  86399,            0,     0",
      "-1,    -10,      2,     20,            1,    10",
      "-1,    -10,     -2, -86390,           -4,     0",
    })
    public void plus(int leftDays, int leftSeconds, int rightDays, int rightSeconds, int expectedDays, int expectedSeconds) {
      Timestamp left = Timestamp.of(leftDays, leftSeconds);
      Timestamp right = Timestamp.of(rightDays, rightSeconds);
      Timestamp expected = Timestamp.of(expectedDays, expectedSeconds);

      assertThat(left.plus(right), equalTo(expected));
      assertThat(right.plus(left), equalTo(expected));
    }

    @ParameterizedTest
    @CsvSource({
      // left           right                expected
      " 1,      0,      2,     0,           -1,      0",
      " 1,     10,      2,    20,           -1,    -10",
      "-1, -86399,      1, 86399,           -3, -86398",
    })
    public void minus(int leftDays, int leftSeconds, int rightDays, int rightSeconds, int expectedDays, int expectedSeconds) {
      Timestamp left = Timestamp.of(leftDays, leftSeconds);
      Timestamp right = Timestamp.of(rightDays, rightSeconds);
      Timestamp expected = Timestamp.of(expectedDays, expectedSeconds);

      assertThat(left.minus(right), equalTo(expected));
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
      Timestamp left = Timestamp.of(leftDays, leftSeconds);
      Timestamp right = Timestamp.of(rightDays, rightSeconds);

      Timestamp expected = result.equals("left") ? left : right;
      assertThat(Timestamp.max(left, right), equalTo(expected));
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
      Timestamp left = Timestamp.of(leftDays, leftSeconds);
      Timestamp right = Timestamp.of(rightDays, rightSeconds);

      Timestamp expected = result.equals("left") ? left : right;
      assertThat(Timestamp.min(left, right), equalTo(expected));
    }
  }

}
