package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AgeTest {

  @Nested
  public class DayResolutionTests {

    @Test
    public void closedAge() {
      Age age = Age.postnatal(10);

      assertThat(age.days(), is(10));
      assertThat(age.seconds(), is(0));
      assertThat(age.isOpen(), is(false));
      assertThat(age.isClosed(), is(true));
      assertThat(age.isGestational(), is(false));
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
      assertThat(Age.postnatal(years, months, days).days(), equalTo(expectedDays));
    }

    @Test
    public void reservedValueThrowsAnException() {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.postnatal(Integer.MAX_VALUE));
      assertThat(e.getMessage(), is("Integer MAX_VALUE is reserved for open end age"));
    }

    @ParameterizedTest
    @CsvSource({
      "10, 10,  0",
      "10, 11, -1",
      "10,  9,  1",
      " 9, 10, -1",
      "10, 10,  0",
      "11, 10,  1",

      " 0,  0,  0",
    })
    public void compareToGestational(int left, int right, int expected) {
      Age l = Age.gestational(left, 0);
      Age r = Age.gestational(right, 0);
      assertThat(Age.compare(l, r), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
      "10, 10,  0",
      "10, 11, -1",
      "10,  9,  1",
      " 9, 10, -1",
      "10, 10,  0",
      "11, 10,  1",

      " 0,  0,  0",
    })
    public void compareToPostnatal(int left, int right, int expected) {
      Age l = Age.postnatal(left);
      Age r = Age.postnatal(right);
      assertThat(Age.compare(l, r), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
      "10, 10, -1",
      "10, 11, -1",
      "10,  9, -1",

      " 9, 10, -1",
      "10, 10, -1",
      "11, 10, -1",

      " 0,  0, -1",
    })
    public void compareTo_GestationalIsAlwaysLessThanPostnatal(int prenatalDays, int postnatalDays, int expected) {
      Age prenatal = Age.gestational(0, prenatalDays);
      Age postnatal = Age.postnatal(postnatalDays);
      assertThat(Age.compare(prenatal, postnatal), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
      " 730500",
    })
    public void compareTo_nearLimits(int days) {
      Age age = Age.postnatal(days);
      assertThat(Age.compare(age, Age.openStart()), is(1));
      assertThat(Age.compare(age, Age.openEnd()), is(-1));
    }

    @Test
    public void compareTo_limits() {
      assertThat(Age.compare(Age.openStart(), Age.openStart()), is(0));
      assertThat(Age.compare(Age.openEnd(), Age.openEnd()), is(0));

      assertThat(Age.compare(Age.openEnd(), Age.openStart()), is(1));
      assertThat(Age.compare(Age.openStart(), Age.openEnd()), is(-1));
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
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.postnatal(days, seconds));
      assertThat(e.getMessage(), equalTo("Days and seconds must not be negative, got '" + days + "' days and '" + seconds + "' seconds"));
    }

    @ParameterizedTest
    @CsvSource({
      // positive
      "0,   86399,   0,  86399",
      "0,   86400,   1,  0",
      "0,   86401,   1,  1",
      "0,  172801,   2,  1",
      "0,  172801,   2,  1",
    })
    public void ageWithOutstandingSecondsIsNormalized(int days, int seconds, int expectedDays, int expectedSeconds) {
      Age age = Age.postnatal(days, seconds);

      assertThat(age.days(), is(expectedDays));
      assertThat(age.seconds(), is(expectedSeconds));
    }

    @ParameterizedTest
    @CsvSource({
      // left    right      result
      " 1,   0,   2,      0,     3,    0",
      " 1,   1,   2,      3,     3,    4",
      " 1,  10,   2,  86395,     4,    5",
    })
    public void plus(int leftDays, int leftSeconds, int rightDays, int rightSeconds, int days, int seconds) {
      Age left = Age.postnatal(leftDays, leftSeconds);
      Age right = Age.postnatal(rightDays, rightSeconds);

      assertThat(left.plus(right), equalTo(Age.postnatal(days, seconds)));
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
      Age left = Age.postnatal(leftDays, leftSeconds);
      Age right = Age.postnatal(rightDays, rightSeconds);

      Age expected = result.equals("left") ? left : right;
      assertThat(Age.max(left, right), equalTo(expected));
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
      Age left = Age.postnatal(leftDays, leftSeconds);
      Age right = Age.postnatal(rightDays, rightSeconds);

      Age expected = result.equals("left") ? left : right;
      assertThat(Age.min(left, right), equalTo(expected));
    }
  }

}
