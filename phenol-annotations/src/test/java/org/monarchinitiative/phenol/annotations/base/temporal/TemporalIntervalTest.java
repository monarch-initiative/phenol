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
    TemporalInterval interval = TemporalInterval.of(AgeSinceBirth.of(10), AgeSinceBirth.of(20));
    assertThat(interval.start(), equalTo(AgeSinceBirth.of(10)));
    assertThat(interval.end(), equalTo(AgeSinceBirth.of(20)));
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
      () -> TemporalInterval.of(AgeSinceBirth.of(startDays, startSeconds), AgeSinceBirth.of(endDays, endSeconds)));
    assertThat(e.getMessage(), is(String.format("Start (%d days, %d seconds) must not be after end (%d days, %d seconds)", startDays, startSeconds, endDays, endSeconds)));
  }

  @ParameterizedTest
  @CsvSource({
    // positive
    " 1,   0,   2,   0,      1,      0",
    " 1,   0,   1,  20,      0,     20",
    " 1,  20,   2,  10,      0,  86390",

    // negative
    "-2,   0,  -1,   0,      1,      0",
    "-1, -20,  -1, -10,      0,     10",
    "-2, -10,  -1, -20,      0,  86390",

    // crossing zero
    "-1,   0,   2,   0,      3,      0",
    "-1, -10,   2,  20,      3,     30",
    "-1, -20,   2,  10,      3,     30",
  })
  public void length(int startDays, int startSeconds, int endDays, int endSeconds, int days, int seconds) {
    TemporalInterval interval = TemporalInterval.of(AgeSinceBirth.of(startDays, startSeconds), AgeSinceBirth.of(endDays, endSeconds));
    assertThat(interval.length(), equalTo(TemporalInterval.of(AgeSinceBirth.zero(), AgeSinceBirth.of(days, seconds))));
  }

  @Test
  public void length_openEndpoints() {
    AgeSinceBirth stamp = AgeSinceBirth.of(1, 0);

    assertThat(TemporalInterval.openStart(stamp).length(), equalTo(TemporalInterval.openEnd(AgeSinceBirth.zero())));
    assertThat(TemporalInterval.openEnd(stamp).length(), equalTo(TemporalInterval.openEnd(AgeSinceBirth.zero())));
    assertThat(TemporalInterval.open().length(), equalTo(TemporalInterval.openEnd(AgeSinceBirth.zero())));
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
    TemporalInterval left = TemporalInterval.of(AgeSinceBirth.of(leftStart), AgeSinceBirth.of(leftEnd));
    TemporalInterval right = TemporalInterval.of(AgeSinceBirth.of(rightStart), AgeSinceBirth.of(rightEnd));

    TemporalInterval intersection = left.intersection(right);
    assertThat(intersection, equalTo(TemporalInterval.of(AgeSinceBirth.of(expectedStart), AgeSinceBirth.of(expectedEnd))));
  }

  @Test
  public void intersection_openEnds() {
    TemporalInterval closed = TemporalInterval.of(AgeSinceBirth.of(1), AgeSinceBirth.of(2));

    assertThat(TemporalInterval.openStart(AgeSinceBirth.of(2)).intersection(closed), equalTo(closed)); // left open
    assertThat(TemporalInterval.openEnd(AgeSinceBirth.of(1)).intersection(closed), equalTo(closed)); // right open
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
    TemporalInterval left = TemporalInterval.of(AgeSinceBirth.of(leftStart), AgeSinceBirth.of(leftEnd));
    TemporalInterval right = TemporalInterval.of(AgeSinceBirth.of(rightStart), AgeSinceBirth.of(rightEnd));

    assertThat(left.overlapsWith(right), equalTo(expected));
  }

  @ParameterizedTest
  @CsvSource({
    "-1, 1,  false, false",
    "-1, 2,  false,  true",
    "-2, 1,   true, false",
    "-2, 2,   true,  true",
  })
  public void overlapsWith_openEndpoints(int start, int end, boolean openStartExpected, boolean openEndExpected) {
    TemporalInterval interval = TemporalInterval.of(AgeSinceBirth.of(start), AgeSinceBirth.of(end));

    TemporalInterval openStart = TemporalInterval.openStart(AgeSinceBirth.of(-1));
    assertThat(openStart.overlapsWith(interval), equalTo(openStartExpected));

    TemporalInterval openEnd = TemporalInterval.openEnd(AgeSinceBirth.of(1));
    assertThat(openEnd.overlapsWith(interval), equalTo(openEndExpected));
  }

  @ParameterizedTest
  @CsvSource({
    "1, 2,    0,    false",
    "1, 2,    1,    true",
    "1, 2,    2,    false",
    "1, 2,    3,    false",
  })
  public void contains(int start, int end, int position, boolean expected) {
    assertThat(TemporalInterval.of(AgeSinceBirth.of(start), AgeSinceBirth.of(end)).contains(AgeSinceBirth.of(position)), equalTo(expected));
  }

}
