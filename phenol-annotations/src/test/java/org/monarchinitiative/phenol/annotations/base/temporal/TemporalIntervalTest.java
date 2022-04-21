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
    TemporalInterval interval = TemporalInterval.of(Age.postnatal(10), Age.postnatal(20));
    assertThat(interval.start(), equalTo(Age.postnatal(10)));
    assertThat(interval.end(), equalTo(Age.postnatal(20)));
  }

  @ParameterizedTest
  @CsvSource({
    "1,  0",
  })
  public void errorOnStartAfterEnd(int startDays, int endDays) {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
      () -> TemporalInterval.of(Age.postnatal(startDays), Age.postnatal(endDays)));
    assertThat(e.getMessage(), is(String.format("Start (%d days) must not be after end (%d days)", startDays, endDays)));
  }

  @ParameterizedTest
  @CsvSource({
    " 1,   0,   2,   0,      7",
    " 1,   0,   1,   6,      6",
  })
  public void length_Gestational(int startWeeks, int startDays, int endWeeks, int endDays, int days) {
    TemporalInterval gestational = TemporalInterval.of(Age.gestational(startWeeks, startDays), Age.gestational(endWeeks, endDays));
    assertThat(gestational.length(), equalTo(TemporalInterval.of(Age.birth(), Age.postnatal(days))));
  }

  @ParameterizedTest
  @CsvSource({
    " 1,   2,      1",
    " 1,   1,      0",
  })
  public void length_Postnatal(int startDays, int endDays, int days) {
    TemporalInterval postnatal = TemporalInterval.of(Age.postnatal(startDays), Age.postnatal(endDays));
    assertThat(postnatal.length(), equalTo(TemporalInterval.of(Age.birth(), Age.postnatal(days))));
  }

  @Test
  public void length_openEndpoints() {
    Age stamp = Age.postnatal(1);

    assertThat(TemporalInterval.openStart(stamp).length(), equalTo(TemporalInterval.open()));
    assertThat(TemporalInterval.openEnd(stamp).length(), equalTo(TemporalInterval.open()));
    assertThat(TemporalInterval.open().length(), equalTo(TemporalInterval.open()));
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
    TemporalInterval left = TemporalInterval.of(Age.postnatal(leftStart), Age.postnatal(leftEnd));
    TemporalInterval right = TemporalInterval.of(Age.postnatal(rightStart), Age.postnatal(rightEnd));

    TemporalInterval intersection = left.intersection(right);
    assertThat(intersection, equalTo(TemporalInterval.of(Age.postnatal(expectedStart), Age.postnatal(expectedEnd))));
  }

  @Test
  public void intersection_openEnds() {
    TemporalInterval closed = TemporalInterval.of(Age.postnatal(1), Age.postnatal(2));

    assertThat(TemporalInterval.openStart(Age.postnatal(2)).intersection(closed), equalTo(closed)); // left open
    assertThat(TemporalInterval.openEnd(Age.postnatal(1)).intersection(closed), equalTo(closed)); // right open
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
    TemporalInterval left = TemporalInterval.of(Age.postnatal(leftStart), Age.postnatal(leftEnd));
    TemporalInterval right = TemporalInterval.of(Age.postnatal(rightStart), Age.postnatal(rightEnd));

    assertThat(left.overlapsWith(right), equalTo(expected));
  }

  @ParameterizedTest
  @CsvSource({
    " 0, 1,   true, false",
    " 0, 2,   true,  true",
    " 1, 2,  false,  true",
  })
  public void overlapsWith_openEndpoints(int start, int end, boolean openStartExpected, boolean openEndExpected) {
    TemporalInterval interval = TemporalInterval.of(Age.postnatal(start), Age.postnatal(end));

    TemporalInterval openStart = TemporalInterval.openStart(Age.postnatal(1));
    assertThat(openStart.overlapsWith(interval), equalTo(openStartExpected));

    TemporalInterval openEnd = TemporalInterval.openEnd(Age.postnatal(1));
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
    assertThat(TemporalInterval.of(Age.postnatal(start), Age.postnatal(end)).contains(Age.postnatal(position)), equalTo(expected));
  }

}
