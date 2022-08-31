package org.monarchinitiative.phenol.annotations.formats.hpo.annotation_impl;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.Age;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoOnset;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests that {@link TemporalRatios} must pass to ensure the correct implementation.
 */
public class TemporalRatiosTest {

  /**
   * Test of naive {@link TemporalRatios} implementation at {@link TemporalRatiosNaive}.
   */
  @Nested
  public class TemporalRatiosNaiveTest {

    private final TemporalRatios simple = TemporalRatiosNaive.of(simplePayload());
    private final TemporalRatios padded = TemporalRatiosNaive.of(paddedByAbsentRegions());

    @Test
    public void baseState_simple() {
      Ratio ratio = simple.ratio();

      assertThat(ratio.numerator(), equalTo(11));
      assertThat(ratio.denominator(), equalTo(11));

      Optional<TemporalInterval> tio = simple.temporalInterval();
      assertThat(tio.isPresent(), equalTo(true));
      assertThat(tio.get().start().days(), equalTo(0));
      assertThat(tio.get().end(), equalTo(PointInTime.openEnd()));
    }

    @Test
    public void baseState_padded() {
      Ratio ratio = padded.ratio();

      assertThat(ratio.numerator(), equalTo(10));
      assertThat(ratio.denominator(), equalTo(25));

      Optional<TemporalInterval> tio = padded.temporalInterval();
      assertThat(tio.isPresent(), equalTo(true));
      assertThat(tio.get().start().days(), equalTo(731));
      assertThat(tio.get().end().days(), equalTo(21_915));
    }

    @Test
    public void gestationalAge() {
      int gestational = simple.observedInInterval(TemporalInterval.gestationalPeriod());

      assertThat(gestational, equalTo(0));
    }

    @ParameterizedTest
    @CsvSource({
      " 0, 100,     11", // Entire postnatal period
      " 0,   2,      1", // First  2 years
      " 0,   3,      5", // First  3 years
      " 0,  18,      6", // First 18 years
      " 0,  20,      6", // First 20 years
      "10,  18,      5",
      "15,  18,      4",
      "15,  41,      9",
      "30,  40,      1",
      "30,  41,      6",
      "50, 100,      6",
    })
    public void postnatalAge(int start, int end, int numerator) {
      TemporalInterval query = TemporalInterval.of(Age.postnatal(start, 0, 0), Age.postnatal(end, 0, 0));

      int ratio = simple.observedInInterval(query);

      assertThat(ratio, equalTo(numerator));
    }
  }

  /**
   * See pic in resources for visual representation of the payload.
   */
  private static List<RatioAndTemporalIntervalAware> simplePayload() {
    return List.of(
      // One has it during entire life.
      RatioAndTemporalIntervalAware.of(
        Ratio.of(1, 1),
        TemporalInterval.of(PointInTime.birth(), Age.openEnd())
      ),

      // 1, 1, and 3 in childhood
      RatioAndTemporalIntervalAware.of(
        Ratio.of(1, 1),
        TemporalInterval.of(Age.postnatal(5, 0, 0), Age.postnatal(10, 0, 0))
      ),
      RatioAndTemporalIntervalAware.of(
        Ratio.of(1, 1),
        TemporalInterval.of(Age.postnatal(2, 0, 0), Age.postnatal(15, 0, 0))
      ),
      RatioAndTemporalIntervalAware.of(
        Ratio.of(3, 3),
        TemporalInterval.of(Age.postnatal(2, 0, 0), Age.postnatal(18, 0, 0))
      ),

      // 5 in middle age until death
      RatioAndTemporalIntervalAware.of(
        Ratio.of(5, 5),
        TemporalInterval.of(HpoOnset.MIDDLE_AGE_ONSET.start(), Age.openEnd())
      )
    );
  }

  private static List<RatioAndTemporalIntervalAware> paddedByAbsentRegions() {
    return List.of(
      // 5 do not have it during the first year
      RatioAndTemporalIntervalAware.of(
        Ratio.of(0, 5),
        TemporalInterval.of(PointInTime.birth(), Age.postnatal(1, 0, 0))
      ),

      // 1, 1, and 3 in childhood
      RatioAndTemporalIntervalAware.of(
        Ratio.of(1, 1),
        TemporalInterval.of(Age.postnatal(5, 0, 0), Age.postnatal(10, 0, 0))
      ),
      RatioAndTemporalIntervalAware.of(
        Ratio.of(1, 1),
        TemporalInterval.of(Age.postnatal(2, 0, 0), Age.postnatal(15, 0, 0))
      ),
      RatioAndTemporalIntervalAware.of(
        Ratio.of(3, 3),
        TemporalInterval.of(Age.postnatal(2, 0, 0), Age.postnatal(18, 0, 0))
      ),

      // 5 during the middle age
      RatioAndTemporalIntervalAware.of(
        Ratio.of(5, 5),
        HpoOnset.MIDDLE_AGE_ONSET),

      // 10 do not have it during the late age
      RatioAndTemporalIntervalAware.of(
        Ratio.of(0, 10),
        HpoOnset.LATE_ONSET)
    );
  }

}
