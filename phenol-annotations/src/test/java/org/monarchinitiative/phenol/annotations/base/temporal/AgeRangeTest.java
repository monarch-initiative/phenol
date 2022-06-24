package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AgeRangeTest {

  @Test
  public void precise() {
    AgeRange interval = AgeRange.of(Age.postnatal(10), Age.postnatal(20));
    assertThat(interval.start(), equalTo(Age.postnatal(10)));
    assertThat(interval.end(), equalTo(Age.postnatal(20)));
    assertThat(interval.isPrecise(), equalTo(true));
    assertThat(interval.isImprecise(), equalTo(false));
  }

  @Test
  public void imprecise() {
    AgeRange interval = AgeRange.of(Age.postnatal(10, ConfidenceRange.of(-5, 15)), Age.postnatal(20));
    assertThat(interval.start().days(), equalTo(10));
    assertThat(interval.start().lowerBound(), equalTo(5));
    assertThat(interval.start().upperBound(), equalTo(25));
    assertThat(interval.end().days(), equalTo(20));
    assertThat(interval.end().lowerBound(), equalTo(20));
    assertThat(interval.end().upperBound(), equalTo(20));
    assertThat(interval.end().isPrecise(), equalTo(true));
    assertThat(interval.isPrecise(), equalTo(false));
    assertThat(interval.isImprecise(), equalTo(true));
  }

  @ParameterizedTest
  @CsvSource({
    "1,  0",
  })
  public void errorOnStartAfterEnd(int startDays, int endDays) {
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
      () -> AgeRange.of(Age.postnatal(startDays), Age.postnatal(endDays)));
    assertThat(e.getMessage(), is(String.format("Start (%d days) must not be after end (%d days)", startDays, endDays)));
  }


  @Test
  public void length_openEndpoints() {
    Age stamp = Age.postnatal(1);

    assertThat(AgeRange.openStart(stamp).length(), equalTo(AgeRange.open().length()));
    assertThat(AgeRange.openEnd(stamp).length(), equalTo(AgeRange.open().length()));
    assertThat(AgeRange.open().length(), equalTo(AgeRange.open().length()));
  }


  @Test
  public void intersection_openEnds() {
    TemporalInterval closed = TemporalInterval.of(PointInTime.of(1, false), PointInTime.of(2, false));

    TemporalInterval openStartIntersection = AgeRange.openStart(Age.postnatal(2)).intersection(closed);
    assertThat(openStartIntersection.start().days(), equalTo(closed.start().days()));
    assertThat(openStartIntersection.end().days(), equalTo(closed.end().days()));

    TemporalInterval openEndIntersection = AgeRange.openEnd(Age.postnatal(1)).intersection(closed);
    assertThat(openEndIntersection.start().days(), equalTo(closed.start().days()));
    assertThat(openEndIntersection.end().days(), equalTo(closed.end().days()));

    assertThat(AgeRange.open().intersection(closed), equalTo(closed)); // fully open
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
    AgeRange left = AgeRange.of(Age.postnatal(leftStart), Age.postnatal(leftEnd));
    AgeRange right = AgeRange.of(Age.postnatal(rightStart), Age.postnatal(rightEnd));

    assertThat(left.overlapsWith(right), equalTo(expected));
  }

  @ParameterizedTest
  @CsvSource({
    " 0, 1,   true, false",
    " 0, 2,   true,  true",
    " 1, 2,  false,  true",
  })
  public void overlapsWith_openEndpoints(int start, int end, boolean openStartExpected, boolean openEndExpected) {
    AgeRange interval = AgeRange.of(Age.postnatal(start), Age.postnatal(end));

    AgeRange openStart = AgeRange.openStart(Age.postnatal(1));
    assertThat(openStart.overlapsWith(interval), equalTo(openStartExpected));

    AgeRange openEnd = AgeRange.openEnd(Age.postnatal(1));
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
    assertThat(AgeRange.of(Age.postnatal(start), Age.postnatal(end)).contains(Age.postnatal(position)), equalTo(expected));
  }

}
