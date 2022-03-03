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
}
