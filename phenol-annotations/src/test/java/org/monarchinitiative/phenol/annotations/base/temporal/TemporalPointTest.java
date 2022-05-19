package org.monarchinitiative.phenol.annotations.base.temporal;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
}
