package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

  /**
   * Verify that the length of the intervals with endpoints located on different timelines is {@link Integer#MAX_VALUE}
   */
  @Test
  public void length_wholePeriods() {
    assertThat(TemporalInterval.gestationalPeriod().length(), equalTo(Integer.MAX_VALUE));
  }

  /**
   * Verify that the length of the half-open or fully open intervals is {@link Integer#MAX_VALUE}.
   */
  @Test
  public void openTemporalIntervalHasIntegerMaxValueLength() {
    TemporalInterval openStart = TemporalInterval.openStart(PointInTime.of(10, false));
    assertThat(openStart.length(), equalTo(Integer.MAX_VALUE));

    TemporalInterval openEnd = TemporalInterval.openEnd(PointInTime.of(10, false));
    assertThat(openEnd.length(), equalTo(Integer.MAX_VALUE));

    assertThat(TemporalInterval.open().length(), equalTo(Integer.MAX_VALUE));

    assertThat(TemporalInterval.postnatalPeriod().length(), equalTo(Integer.MAX_VALUE));
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

  @ParameterizedTest
  @CsvSource({
    // left                       right                          result
    "10,  true, 11,  true,        11,  true, 11,  true,         -1",
    "11,  true, 11,  true,        11,  true, 11,  true,          0",
    "11,  true, 12,  true,        11,  true, 11,  true,          1",

    "11,  true, 11,  true,        10,  true, 11,  true,          1",
    "11,  true, 11,  true,        11,  true, 11,  true,          0",
    "11,  true, 11,  true,        11,  true, 12,  true,         -1",

    // left is less due to start on gestational timeline
    "12,  true, 11, false,        11, false, 11, false,         -1",
    // left is less due to start & end on gestational timeline
    "12,  true, 12,  true,        11, false, 11, false,         -1",
  })
  public void compare(int leftStartDays, boolean leftStartGestational, int leftEndDays, boolean leftEndGestational,
                      int rightStartDays, boolean rightStartGestational, int rightEndDays, boolean rightEndGestational,
                      int result) {
    PointInTime leftStart = PointInTime.of(leftStartDays, leftStartGestational);
    PointInTime leftEnd = PointInTime.of(leftEndDays, leftEndGestational);
    TemporalInterval left = TemporalInterval.of(leftStart, leftEnd);

    PointInTime rightStart = PointInTime.of(rightStartDays, rightStartGestational);
    PointInTime rightEnd = PointInTime.of(rightEndDays, rightEndGestational);
    TemporalInterval right = TemporalInterval.of(rightStart, rightEnd);

    assertThat(TemporalInterval.compare(left, right), equalTo(result));
  }

  @Test
  public void throwsAnExceptionWhenStartIsAfterEnd() {
     IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
       () -> TemporalInterval.of(PointInTime.of(1, false), PointInTime.of(0, false)));
     assertThat(e.getMessage(), containsString("Start (1 days) must not be after end (0 days)"));
  }

  @ParameterizedTest
  @CsvSource({
    // LEFT          RIGHT             EXPECTED
    // General cases:
    "10,  10,        12,  14,          BEFORE",
    "10,  12,        12,  14,          BEFORE",
    "11,  13,        12,  14,          BEFORE_AND_DURING",
    "11,  14,        12,  14,          CONTAINS",
    "12,  13,        12,  14,          CONTAINED_IN",
    "13,  14,        12,  14,          CONTAINED_IN",
    "12,  15,        12,  14,          CONTAINS",
    "13,  15,        12,  14,          DURING_AND_AFTER",
    "14,  16,        12,  14,          AFTER",
    "16,  16,        12,  14,          AFTER",

    // Special cases:
    // Empty intervals at the borders are contained in.
    "12,  12,        12,  14,          CONTAINED_IN",
    "14,  14,        12,  14,          CONTAINED_IN",
    // Equal intervals are contained in.
    "12,  14,        12,  14,          CONTAINED_IN",
  })
  public void temporalOverlapType(int leftStartDays, int leftEndDays,
                                  int rightStartDays, int rightEndDays,
                                  TemporalOverlapType expected) {
    PointInTime leftStart = PointInTime.of(leftStartDays, false);
    PointInTime leftEnd = PointInTime.of(leftEndDays, false);
    TemporalInterval left = TemporalInterval.of(leftStart, leftEnd);

    PointInTime rightStart = PointInTime.of(rightStartDays, false);
    PointInTime rightEnd = PointInTime.of(rightEndDays, false);
    TemporalInterval right = TemporalInterval.of(rightStart, rightEnd);

    assertThat(left.temporalOverlapType(right), equalTo(expected));
  }

}
