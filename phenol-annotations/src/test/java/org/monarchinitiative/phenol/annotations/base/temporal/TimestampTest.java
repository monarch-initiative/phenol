package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TimestampTest {

  @Nested
  public class DayResolutionTests {

    @Test
    public void closedTimestamp() {
      Timestamp timestamp = Timestamp.of(10);

      assertThat(timestamp.days(), is(10));
      assertThat(timestamp.seconds(), is(0));
      assertThat(timestamp.isOpen(), is(false));
      assertThat(timestamp.isClosed(), is(true));
    }

    @Test
    public void reservedValuesThrowException() {
      IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Timestamp.of(Integer.MIN_VALUE));
      assertThat(e.getMessage(), is("Integer MIN_VALUE is reserved for open end timestamp"));

      e = assertThrows(IllegalArgumentException.class, () -> Timestamp.of(Integer.MAX_VALUE));
      assertThat(e.getMessage(), is("Integer MAX_VALUE is reserved for open end timestamp"));
    }

    @ParameterizedTest
    @CsvSource({
      "10, 10,  0",
      "10, 11, -1",
      "10,  9,  1",

      " 0,  0,  0",
      " 0, -1,  1",
      "-1,  0, -1",
    })
    public void compareTo(int left, int right, int expected) {
      Timestamp l = Timestamp.of(left);
      Timestamp r = Timestamp.of(right);
      assertThat(Timestamp.compare(l, r), is(expected));
    }

    @ParameterizedTest
    @CsvSource({
      " 730500",
      "-730500",
    })
    public void compareTo_nearLimits(int days) {
      Timestamp stamp = Timestamp.of(days);
      assertThat(Timestamp.compare(stamp, Timestamp.openStart()), is(1));
      assertThat(Timestamp.compare(stamp, Timestamp.openEnd()), is(-1));
    }

    @Test
    public void compareTo_limits() {
      assertThat(Timestamp.compare(Timestamp.openStart(), Timestamp.openStart()), is(0));
      assertThat(Timestamp.compare(Timestamp.openEnd(), Timestamp.openEnd()), is(0));

      assertThat(Timestamp.compare(Timestamp.openEnd(), Timestamp.openStart()), is(1));
      assertThat(Timestamp.compare(Timestamp.openStart(), Timestamp.openEnd()), is(-1));
    }

    @ParameterizedTest
    @CsvSource({
      // positive
      "0,   86399,   0,  86399",
      "0,   86400,   1,  0",
      "0,   86401,   1,  1",
      "0,  172801,   2,  1",
      "0,  172801,   2,  1",

      // negative
      "0,  -86399,   0, -86399",
      "0,  -86400,  -1,  0",
      "0,  -86401,  -1, -1",
      "0, -172801,  -2, -1",
    })
    public void timestampWithOutstandingSecondsIsNormalized(int days, int seconds, int expectedDays, int expectedSeconds) {
      Timestamp duration = Timestamp.of(days, seconds);

      assertThat(duration.days(), is(expectedDays));
      assertThat(duration.seconds(), is(expectedSeconds));
    }
  }


}
