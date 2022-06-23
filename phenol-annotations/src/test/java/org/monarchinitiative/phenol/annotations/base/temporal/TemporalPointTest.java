package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class TemporalPointTest {

  @Nested
  public class TemporalPointConstantsTests {

    @Test
    public void lastMenstrualPeriod() {
      TemporalPoint lmp = TemporalPoint.lastMenstrualPeriod();
      assertThat(lmp.days(), equalTo(0));
      assertThat(lmp.isZero(), equalTo(true));
      assertThat(lmp.isPositive(), equalTo(false));
      assertThat(lmp.isGestational(), equalTo(true));
      assertThat(lmp.isClosed(), equalTo(true));
    }

    @Test
    public void birth() {
      TemporalPoint birth = TemporalPoint.birth();
      assertThat(birth.days(), equalTo(0));
      assertThat(birth.isZero(), equalTo(true));
      assertThat(birth.isPositive(), equalTo(false));
      assertThat(birth.isPostnatal(), equalTo(true));
      assertThat(birth.isClosed(), equalTo(true));
    }
  }

  @Nested
  public class TemporalPointMethodTests {

    @Test
    public void basic() {
      TemporalPoint instance = TemporalPoint.of(10, false);

      assertThat(instance.days(), equalTo(10));
      assertThat(instance.isPostnatal(), equalTo(true));
      assertThat(instance.isGestational(), equalTo(false));
      assertThat(instance.isOpen(), equalTo(false));
      assertThat(instance.isClosed(), equalTo(true));
      assertThat(instance.isPositive(), equalTo(true));
      assertThat(instance.isZero(), equalTo(false));
    }

    @ParameterizedTest
    @CsvSource({
      " 0,    0",
      " 6,    0",
      " 7,    1",
      "13,    1", // still just one complete week
      "14,    2", // now we have two weeks
    })
    public void completeWeeksFromDays(int days, int expectedWeeks) {
      assertThat(TemporalPoint.of(days, false).completeWeeks(), equalTo(expectedWeeks));
      assertThat(TemporalPoint.of(days, true).completeWeeks(), equalTo(expectedWeeks));
    }

    @ParameterizedTest
    @CsvSource({
      "  0,       0",
      " 30,       0",
      " 31,       1",

      "365,       11", // Julian year is 365.25 days!
      "366,       12",
    })
    public void completeMonthsFromDays(int days, int expectedMonths) {
      assertThat(TemporalPoint.of(days, false).completeMonths(), equalTo(expectedMonths));
    }

    @ParameterizedTest
    @CsvSource({
      "   0,      0",
      "   1,      0",
      " 365,      0", // Julian year is 365.25 days!
      " 366,      1",
      " 730,      1",
      " 731,      2",
    })
    public void completeYearsFromYears(int days, int expectedYears) {
      assertThat(TemporalPoint.of(days, false).completeYears(), equalTo(expectedYears));
    }

    @ParameterizedTest
    @CsvSource({
      "10,  true, 10,  true,    0",
      "10, false, 10, false,    0",

      "10,  true, 10, false,   -1",
      "10, false, 10,  true,    1",

      "10,  true, 11,  true,   -1",
      "10, false, 11, false,   -1",

      "11,  true, 10,  true,    1",
      "11, false, 10, false,    1",
    })
    public void compare(int leftDays, boolean leftGestational, int rightDays, boolean rightGestational, int result) {
      TemporalPoint left = TemporalPoint.of(leftDays, leftGestational);
      TemporalPoint right = TemporalPoint.of(rightDays, rightGestational);

      assertThat(TemporalPoint.compare(left, right), equalTo(result));
    }

    @Test
    public void compare_open() {
      TemporalPoint openStart = TemporalPoint.openStart();
      TemporalPoint lastMenstrualPeriod = TemporalPoint.lastMenstrualPeriod();
      TemporalPoint gestational = TemporalPoint.of(10, true);
      TemporalPoint birth = TemporalPoint.birth();
      TemporalPoint postnatal = TemporalPoint.of(10, false);
      TemporalPoint openEnd = TemporalPoint.openEnd();
      List<TemporalPoint> sorted = List.of(openStart, lastMenstrualPeriod, gestational, birth, postnatal, openEnd);
      List<TemporalPoint> points = new ArrayList<>(sorted);

      // shuffle and re-sort the points using the comparator
      Collections.shuffle(points);
      points.sort(TemporalPoint::compare);

      for (int i = 0; i < points.size(); i++) {
        TemporalPoint a = points.get(i);
        TemporalPoint b = sorted.get(i);
        assertThat(a, equalTo(b));
      }
    }
  }
}
