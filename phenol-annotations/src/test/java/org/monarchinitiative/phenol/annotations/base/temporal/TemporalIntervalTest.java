package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TemporalIntervalTest {

  @Test
  public void create() {
    TemporalInterval interval = TemporalInterval.of(Timestamp.of(10), Timestamp.of(20));
    assertThat(interval.start(), equalTo(Timestamp.of(10)));
    assertThat(interval.end(), equalTo(Timestamp.of(20)));
  }

  @ParameterizedTest
  @CsvSource({
    "0,  1,  0,  0",
    "0,  0,  0, -1",

    "1,  0,  0,  0",
    "0,  0, -1,  0",
  })
  public void errorOnStartAfterEnd(int startDays, int startSeconds, int endDays, int endSeconds) {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
      () -> TemporalInterval.of(Timestamp.of(startDays, startSeconds), Timestamp.of(endDays, endSeconds)));
    assertThat(e.getMessage(), is(String.format("Start (%d days, %d seconds) must not be after end (%d days, %d seconds)", startDays, startSeconds, endDays, endSeconds)));
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
  public void intersection(int leftStart, int leftEnd, int rightStart, int rightEnd, int expectedStart, int expectedEnd) {
    TemporalInterval left = TemporalInterval.of(Timestamp.of(leftStart), Timestamp.of(leftEnd));
    TemporalInterval right = TemporalInterval.of(Timestamp.of(rightStart), Timestamp.of(rightEnd));

    TemporalInterval intersection = left.intersection(right);
    assertThat(intersection, equalTo(TemporalInterval.of(Timestamp.of(expectedStart), Timestamp.of(expectedEnd))));
  }

  @Test
  public void intersection_openEnds() {
    TemporalInterval closed = TemporalInterval.of(Timestamp.of(1), Timestamp.of(2));

    assertThat(TemporalInterval.openStart(Timestamp.of(2)).intersection(closed), equalTo(closed)); // left open
    assertThat(TemporalInterval.openEnd(Timestamp.of(1)).intersection(closed), equalTo(closed)); // right open
    assertThat(TemporalInterval.open().intersection(closed), equalTo(closed)); // fully open
  }

  @ParameterizedTest
  @CsvSource({
    // intersecting
    "1, 3,    2, 4,    true",

    // boundaries
    "1, 2,    1, 1,    false",
    "1, 2,    2, 2,    false",

    // adjacent
    "1, 2,    2, 3,    false",
    "2, 3,    1, 2,    false",

    // disjoint
    "1, 2,    3, 4,    false",
    "3, 4,    1, 2,    false",
  })
  public void overlapsWith(int leftStart, int leftEnd, int rightStart, int rightEnd, boolean expected) {
    TemporalInterval left = TemporalInterval.of(Timestamp.of(leftStart), Timestamp.of(leftEnd));
    TemporalInterval right = TemporalInterval.of(Timestamp.of(rightStart), Timestamp.of(rightEnd));

    assertThat(left.overlapsWith(right), equalTo(expected));
  }

  @ParameterizedTest
  @CsvSource({
    "1, 2,    0,    false",
    "1, 2,    1,    true",
    "1, 2,    2,    false",
    "1, 2,    3,    false",
  })
  public void contains(int start, int end, int position, boolean expected) {
    assertThat(TemporalInterval.of(Timestamp.of(start), Timestamp.of(end)).contains(Timestamp.of(position)), equalTo(expected));
  }

}
