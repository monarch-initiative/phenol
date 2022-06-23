package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class TemporalRangeTest {


  @ParameterizedTest
  @CsvSource({
    " 6,  16,     10",
  })
  public void length_Gestational(int start, int end, int days) {
    TemporalRange gestational = TemporalRange.of(TemporalPoint.of(start, true), TemporalPoint.of(end, true));
    assertThat(gestational.length(), equalTo(days));
  }

  @ParameterizedTest
  @CsvSource({
    " 1,   2,      1",
    " 1,   1,      0",
  })
  public void length_Postnatal(int startDays, int endDays, int days) {
    TemporalRange postnatal = TemporalRange.of(TemporalPoint.of(startDays, false), TemporalPoint.of(endDays, false));
    assertThat(postnatal.length(), equalTo(days));
  }

  @Test
  public void openTemporalRangeHasIntegerMaxValueLength() {
    TemporalRange openStart = TemporalRange.openStart(TemporalPoint.of(10, false));
    assertThat(openStart.length(), equalTo(Integer.MAX_VALUE));

    TemporalRange openEnd = TemporalRange.openEnd(TemporalPoint.of(10, false));
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
    TemporalRange left = TemporalRange.of(TemporalPoint.of(leftStart,false), TemporalPoint.of(leftEnd, false));
    TemporalRange right = TemporalRange.of(TemporalPoint.of(rightStart,false), TemporalPoint.of(rightEnd, false));

    TemporalRange intersection = left.intersection(right);
    assertThat(intersection, equalTo(TemporalRange.of(TemporalPoint.of(expectedStart ,false), TemporalPoint.of(expectedEnd, false))));
  }

}
