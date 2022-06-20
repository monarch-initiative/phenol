package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.annotations.TestBase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AgeTest {

  @Nested
  public class AgeTests {

    @ParameterizedTest
    @CsvSource({
      "15, false,  15,  true,     false",
      "15,  true,  15, false,      true",

      "10,  true,  20,  true,      true",
      "20,  true,  10,  true,     false",

      "10, false,  20, false,      true",
      "20, false,  10, false,     false",
    })
    public void min(int leftDays, boolean leftIsGestational,
                    int rightDays, boolean rightIsGestational,
                    boolean leftIsSmaller) {
      Age left = Age.of(leftDays, leftIsGestational, ConfidenceInterval.precise());
      Age right = Age.of(rightDays, rightIsGestational, ConfidenceInterval.precise());
      Age max = Age.min(left, right);

      if (leftIsSmaller)
        assertThat(max, equalTo(left));
      else
        assertThat(max, equalTo(right));
    }

    @ParameterizedTest
    @CsvSource({
      "15, false,  15,  true,      true",
      "15,  true,  15, false,     false",

      "10,  true,  20,  true,     false",
      "20,  true,  10,  true,      true",

      "10, false,  20, false,     false",
      "20, false,  10, false,      true",
    })
    public void max(int leftDays, boolean leftIsGestational,
                    int rightDays, boolean rightIsGestational,
                    boolean leftIsGreater) {
      Age left = Age.of(leftDays, leftIsGestational, ConfidenceInterval.precise());
      Age right = Age.of(rightDays, rightIsGestational, ConfidenceInterval.precise());
      Age max = Age.max(left, right);

      if (leftIsGreater)
        assertThat(max, equalTo(left));
      else
        assertThat(max, equalTo(right));
    }

  }

  @Nested
  public class PreciseAgeTests {

    @Test
    public void closedAge() {
      Age age = Age.postnatal(10);

      assertThat(age.days(), is(10f));
      assertThat(age.isOpen(), is(false));
      assertThat(age.isClosed(), is(true));
      assertThat(age.isPostnatal(), is(true));
      assertThat(age.isGestational(), is(false));
      assertThat(age.isPrecise(), is(true));
      assertThat(age.isImprecise(), is(false));
      assertThat(age.isZero(), is(false));
      assertThat(age.isPositive(), is(true));
    }

    @ParameterizedTest
    @CsvSource({
      // check rounding of Julian years
      "  1,  0,  0,       365.25",
      " 10,  0,  0,      3652.5",
      "100,  0,  0,     36525",

      // months
      "  0,  1,  0,        30.4375",
      "  0,  2,  0,        60.875",
      "  0,  3,  0,        91.3125",
      "  0,  4,  0,       121.75",
      "  0, 12,  0,       365.25",
    })
    public void createUsingDays(int years, int months, int days, float expectedDays) {
      assertThat((double) Age.postnatal(years, months, days).days(), closeTo(expectedDays, TestBase.ERROR));
    }

    @Test
    public void reservedValueThrowsAnException() {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.postnatal(Float.POSITIVE_INFINITY));
      assertThat(e.getMessage(), is("Infinite value is reserved for open end age"));

      e = assertThrows(IllegalArgumentException.class, () -> Age.postnatal(Float.NEGATIVE_INFINITY));
      assertThat(e.getMessage(), is("Infinite value is reserved for open end age"));
    }

    @Test
    public void nanThrowsAnException() {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.postnatal(Float.NaN));
      assertThat(e.getMessage(), is("NaN values are not allowed"));
    }

    @ParameterizedTest
    @CsvSource({
      "-5,  3",
      " 5, -4"
    })
    public void gestationalAge_negativeValueThrowsAnException(int weeks, int days) {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.gestational(weeks, days));
      assertThat(e.getMessage(), containsString(String.format("'%d, %d' Weeks and days must be non-negative!", weeks, days)));
    }

    @ParameterizedTest
    @CsvSource({
      "-5,  3,  15",
      " 5, -4,  15",
      " 5, -4, -15"
    })
    public void postnatalAge_negativeValueThrowsAnException(int years, int months, int days) {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.postnatal(years, months, days));
      assertThat(e.getMessage(), containsString(String.format("'%d, %d, %d' Years, months and days must be non-negative!", years, months, days)));
    }

    @Test
    public void negativeNumberOfDaysThrowsAnException() {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.of(-1, true, ConfidenceInterval.precise()));
      assertThat(e.getMessage(), containsString("Days must not be negative, got '-1.0' days!"));
    }


    @Test
    public void tooGreatAgeThrowsAnException() {
      ArithmeticException e = assertThrows(ArithmeticException.class, () -> Age.of(Age.MAX_DAYS + 1, true, ConfidenceInterval.precise()));
      assertThat(e.getMessage(), containsString("Normalized number of days must not be greater than '730500.0'. Got '730501.0'"));
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
      "10,   0,  0,     10,   0,   0,            0",
      "10, -10, 10,     10,   0,  20,            0", // Same CI length, hence equal.
      "10,  -5, 10,     10, -10,  10,            1",
      "10, -10, 10,     10,  -5,  10,           -1",
    })
    public void compareToImprecise(int left, int leftLower, int leftUpper,
                                   int right, int rightLower, int rightUpper,
                                   int expected) {
      Age l = Age.postnatal(left, ConfidenceInterval.of(leftLower, leftUpper));
      Age r = Age.postnatal(right, ConfidenceInterval.of(rightLower, rightUpper));

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

    @Test
    public void asTemporalRange() {
      Age age = Age.postnatal(0, 0, 14);

      assertThat(age.start().days(), equalTo(14f));
      assertThat(age.end().days(), equalTo(14f));
    }
  }

  @Nested
  public class ImpreciseAgeTest {

    @Test
    public void closedAge() {
      Age age = Age.postnatal(10, ConfidenceInterval.of(-5, 10));

      assertThat(age.days(), is(10f));
      assertThat(age.isOpen(), is(false));
      assertThat(age.isClosed(), is(true));
      assertThat(age.isGestational(), is(false));
      assertThat(age.isPrecise(), is(false));
    }

    @ParameterizedTest
    @CsvSource({
      "15,  true,  -20, 20,      -15",
      "15,  true,  -10, 20,      -10",
      "15, false,  -20, 20,      -15",
      "15, false,  -10, 20,      -10",
    })
    public void closedAgeIsClipped(int days, boolean isGestational, int lowerBound, int upperBound, float expectedLowerBound) {
      Age age = Age.of(days, isGestational, ConfidenceInterval.of(lowerBound, upperBound));
      assertThat(age.confidenceInterval().lowerBound(), equalTo(expectedLowerBound));
    }

    @Test
    public void reservedValueThrowsAnException() {
      // Check this also works with imprecise age.
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.postnatal(Float.POSITIVE_INFINITY, ConfidenceInterval.of(-5, 10)));
      assertThat(e.getMessage(), is("Infinite value is reserved for open end age"));

      e = assertThrows(IllegalArgumentException.class, () -> Age.postnatal(Float.NEGATIVE_INFINITY, ConfidenceInterval.of(-5, 10)));
      assertThat(e.getMessage(), equalTo("Infinite value is reserved for open end age"));
    }

    @Test
    public void nanThrowsAnException() {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Age.postnatal(Float.NaN, ConfidenceInterval.of(-5, 10)));
      assertThat(e.getMessage(), is("NaN values are not allowed"));
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

    @Test
    public void asTemporalRange() {
      Age age = Age.postnatal(10, ConfidenceInterval.of(-5, 10));

      assertThat(age.start().days(), equalTo(5f));
      assertThat(age.end().days(), equalTo(20f));
    }
  }

}
