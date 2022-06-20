package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.annotations.TestBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import static org.monarchinitiative.phenol.annotations.base.temporal.TemporalPoint.*;

public class TemporalPointTest {

  @Nested
  public class TemporalPointConstantsTests {

    @Test
    public void lastMenstrualPeriod() {
      TemporalPoint lmp = TemporalPoint.lastMenstrualPeriod();
      assertThat(lmp.days(), equalTo(0f));
      assertThat(lmp.isZero(), equalTo(true));
      assertThat(lmp.isPositive(), equalTo(false));
      assertThat(lmp.isGestational(), equalTo(true));
      assertThat(lmp.isClosed(), equalTo(true));
    }

    @Test
    public void birth() {
      TemporalPoint birth = TemporalPoint.birth();
      assertThat(birth.days(), equalTo(0f));
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

      assertThat(instance.days(), equalTo(10f));
      assertThat(instance.isPostnatal(), equalTo(true));
      assertThat(instance.isGestational(), equalTo(false));
      assertThat(instance.isOpen(), equalTo(false));
      assertThat(instance.isClosed(), equalTo(true));
      assertThat(instance.isPositive(), equalTo(true));
      assertThat(instance.isZero(), equalTo(false));
    }

    @ParameterizedTest
    @CsvSource({
      " 0,    .0",
      " 6,    .8571428",
      " 7,   1.",
      "13,   1.8571428",
      "14,   2.",
    })
    public void weeks(int days, double expectedWeeks) {
      assertThat((double) TemporalPoint.of(days, false).weeks(), closeTo(expectedWeeks, TestBase.ERROR));
      assertThat((double) TemporalPoint.of(days, true).weeks(), closeTo(expectedWeeks, TestBase.ERROR));
    }

    @ParameterizedTest
    @CsvSource({
      "  0,      .0",
      " 30,      .9856262",
      " 31,     1.0184804",

      "365,    11.9917864", // Julian year is 365.25 days!
      "366,    12.0246406",
    })
    public void months(int days, double expectedMonths) {
      assertThat((double) TemporalPoint.of(days, false).months(), closeTo(expectedMonths, TestBase.ERROR));
    }

    @ParameterizedTest
    @CsvSource({
      "   0,     .0",
      "   1,     .0027378",
      " 365,     .9993155", // Julian year is 365.25 days!
      " 366,    1.0020533",
      " 730,    1.9986310",
      " 731,    2.0013689",
    })
    public void years(int days, double expectedYears) {
      assertThat((double) TemporalPoint.of(days, false).years(), closeTo(expectedYears, TestBase.ERROR));
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
      TemporalPoint openStart = openStart();
      TemporalPoint lastMenstrualPeriod = lastMenstrualPeriod();
      TemporalPoint gestational = TemporalPoint.of(10, true);
      TemporalPoint birth = birth();
      TemporalPoint postnatal = TemporalPoint.of(10, false);
      TemporalPoint openEnd = openEnd();
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
