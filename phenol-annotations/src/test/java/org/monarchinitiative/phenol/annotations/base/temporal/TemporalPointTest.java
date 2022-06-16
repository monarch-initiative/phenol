package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class TemporalPointTest {

  @Nested
  public class TemporalPointConstantsTests {

    @Test
    public void lastMenstrualPeriod() {
      TemporalPoint lmp = TemporalPoint.lastMenstrualPeriod();
      assertThat(lmp.days(), equalTo(0));
      assertThat(lmp.isZero(), equalTo(true));
      assertThat(lmp.isPositive(), equalTo(false));
      assertThat(lmp.isGestational(), equalTo(true));
      assertThat(lmp.isClosed(), equalTo(true));
    }

    @Test
    public void birth() {
      TemporalPoint birth = TemporalPoint.birth();
      assertThat(birth.days(), equalTo(0));
      assertThat(birth.isZero(), equalTo(true));
      assertThat(birth.isPositive(), equalTo(false));
      assertThat(birth.isPostnatal(), equalTo(true));
      assertThat(birth.isClosed(), equalTo(true));
    }
  }

  @Nested
  public class TemporalPointMethodTests {

    @Test
    public void basic() {
      TemporalPoint instance = TemporalPoint.of(10, false);

      assertThat(instance.days(), equalTo(10));
      assertThat(instance.isPostnatal(), equalTo(true));
      assertThat(instance.isGestational(), equalTo(false));
      assertThat(instance.isOpen(), equalTo(false));
      assertThat(instance.isClosed(), equalTo(true));
      assertThat(instance.isPositive(), equalTo(true));
      assertThat(instance.isZero(), equalTo(false));
    }

    @ParameterizedTest
    @CsvSource({
      "10,  true, 10,  true,    0",
      "10, false, 10, false,    0",

      "10,  true, 10, false,   -1",
      "10, false, 10,  true,    1",

      "10,  true, 11,  true,   -1",
      "10, false, 11, false,   -1",

      "11,  true, 10,  true,    1",
      "11, false, 10, false,    1",
    })
    public void compare(int leftDays, boolean leftGestational, int rightDays, boolean rightGestational, int result) {
      TemporalPoint left = TemporalPoint.of(leftDays, leftGestational);
      TemporalPoint right = TemporalPoint.of(rightDays, rightGestational);

      assertThat(TemporalPoint.compare(left, right), equalTo(result));
    }
  }
}
