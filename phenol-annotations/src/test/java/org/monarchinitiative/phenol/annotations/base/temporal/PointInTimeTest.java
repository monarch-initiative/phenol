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

public class PointInTimeTest {

  @Nested
  public class PointInTimeConstantsTests {

    @Test
    public void lastMenstrualPeriod() {
      PointInTime lmp = PointInTime.lastMenstrualPeriod();
      assertThat(lmp.days(), equalTo(0));
      assertThat(lmp.isZero(), equalTo(true));
      assertThat(lmp.isPositive(), equalTo(false));
      assertThat(lmp.isGestational(), equalTo(true));
      assertThat(lmp.isClosed(), equalTo(true));
    }

    @Test
    public void birth() {
      PointInTime birth = PointInTime.birth();
      assertThat(birth.days(), equalTo(0));
      assertThat(birth.isZero(), equalTo(true));
      assertThat(birth.isPositive(), equalTo(false));
      assertThat(birth.isPostnatal(), equalTo(true));
      assertThat(birth.isClosed(), equalTo(true));
    }
  }

  @Nested
  public class PointInTimeMethodTests {

    @Test
    public void basic() {
      PointInTime instance = PointInTime.of(10, false);

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
      assertThat(PointInTime.of(days, false).completeWeeks(), equalTo(expectedWeeks));
      assertThat(PointInTime.of(days, true).completeWeeks(), equalTo(expectedWeeks));
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
      assertThat(PointInTime.of(days, false).completeMonths(), equalTo(expectedMonths));
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
    public void completeYearsFromDays(int days, int expectedYears) {
      assertThat(PointInTime.of(days, false).completeYears(), equalTo(expectedYears));
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
      PointInTime left = PointInTime.of(leftDays, leftGestational);
      PointInTime right = PointInTime.of(rightDays, rightGestational);

      assertThat(PointInTime.compare(left, right), equalTo(result));
    }

    @Test
    public void compare_open() {
      PointInTime openStart = PointInTime.openStart();
      PointInTime lastMenstrualPeriod = PointInTime.lastMenstrualPeriod();
      PointInTime gestational = PointInTime.of(10, true);
      PointInTime birth = PointInTime.birth();
      PointInTime postnatal = PointInTime.of(10, false);
      PointInTime openEnd = PointInTime.openEnd();
      List<PointInTime> sorted = List.of(openStart, lastMenstrualPeriod, gestational, birth, postnatal, openEnd);
      List<PointInTime> points = new ArrayList<>(sorted);

      // shuffle and re-sort the points using the comparator
      Collections.shuffle(points);
      points.sort(PointInTime::compare);

      for (int i = 0; i < points.size(); i++) {
        PointInTime a = points.get(i);
        PointInTime b = sorted.get(i);
        assertThat(a, equalTo(b));
      }
    }
  }
}
