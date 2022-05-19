package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class TemporalRangeTest {


  @ParameterizedTest
  @CsvSource({
    " 1,   0,   2,   0,      7",
    " 1,   0,   1,   6,      6",
  })
  public void length_Gestational(int startWeeks, int startDays, int endWeeks, int endDays, int days) {
    TemporalRange gestational = TemporalRange.of(Age.gestational(startWeeks, startDays), Age.gestational(endWeeks, endDays));
    assertThat(gestational.length(), equalTo(TemporalRange.of(TemporalPoint.birth(), Age.postnatal(days))));
  }

  @ParameterizedTest
  @CsvSource({
    " 1,   2,      1",
    " 1,   1,      0",
  })
  public void length_Postnatal(int startDays, int endDays, int days) {
    TemporalRange postnatal = TemporalRange.of(Age.postnatal(startDays), Age.postnatal(endDays));
    assertThat(postnatal.length(), equalTo(TemporalRange.of(TemporalPoint.birth(), Age.postnatal(days))));
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
    AgeRange left = AgeRange.of(Age.postnatal(leftStart), Age.postnatal(leftEnd));
    AgeRange right = AgeRange.of(Age.postnatal(rightStart), Age.postnatal(rightEnd));

    TemporalRange intersection = left.intersection(right);
    assertThat(intersection, equalTo(TemporalRange.of(Age.postnatal(expectedStart), Age.postnatal(expectedEnd))));
  }

}
