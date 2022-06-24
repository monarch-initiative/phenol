package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class TemporalIntervalTest {


  @ParameterizedTest
  @CsvSource({
    " 6,  16,     10",
  })
  public void length_Gestational(int start, int end, int days) {
    TemporalInterval gestational = TemporalInterval.of(PointInTime.of(start, true), PointInTime.of(end, true));
    assertThat(gestational.length(), equalTo(days));
  }

  @ParameterizedTest
  @CsvSource({
    " 1,   2,      1",
    " 1,   1,      0",
  })
  public void length_Postnatal(int startDays, int endDays, int days) {
    TemporalInterval postnatal = TemporalInterval.of(PointInTime.of(startDays, false), PointInTime.of(endDays, false));
    assertThat(postnatal.length(), equalTo(days));
  }

  @Test
  public void openTemporalRangeHasIntegerMaxValueLength() {
    TemporalInterval openStart = TemporalInterval.openStart(PointInTime.of(10, false));
    assertThat(openStart.length(), equalTo(Integer.MAX_VALUE));

    TemporalInterval openEnd = TemporalInterval.openEnd(PointInTime.of(10, false));
    assertThat(openEnd.length(), equalTo(Integer.MAX_VALUE));
  }

  @ParameterizedTest
  @CsvSource({
    // intersecting
    "1, 3,    2, 4,    2, 3",

    // boundaries
    "1, 2,    1, 1,    1, 1",
    "1, 2,    2, 2,    2, 2",

    // adjacent
    "1, 2,    2, 3,    2, 2",
    "2, 3,    1, 2,    2, 2",

    // disjoint
    "1, 2,    3, 4,    0, 0",
    "3, 4,    1, 2,    0, 0",
  })
  public void intersection(int leftStart, int leftEnd,
                           int rightStart, int rightEnd,
                           int expectedStart, int expectedEnd) {
    TemporalInterval left = TemporalInterval.of(PointInTime.of(leftStart,false), PointInTime.of(leftEnd, false));
    TemporalInterval right = TemporalInterval.of(PointInTime.of(rightStart,false), PointInTime.of(rightEnd, false));

    TemporalInterval intersection = left.intersection(right);
    assertThat(intersection, equalTo(TemporalInterval.of(PointInTime.of(expectedStart ,false), PointInTime.of(expectedEnd, false))));
  }

}
