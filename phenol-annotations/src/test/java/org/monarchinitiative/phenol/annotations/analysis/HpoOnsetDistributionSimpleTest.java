package org.monarchinitiative.phenol.annotations.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.annotations.HpoDiseaseExamples;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.base.temporal.AgeSinceBirth;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class HpoOnsetDistributionSimpleTest {

  @Nested
  public class SimpleTest {

    private HpoOnsetDistributionSimple instance;

    @BeforeEach
    public void setUp() {
      instance = HpoOnsetDistributionSimple.of();
    }

    @ParameterizedTest
    @CsvSource({
      "-1,  0,         false",
      "15, 16,         false",
      "16, 17,          true",
    })
    public void isObservableInAge(int startYears, int endYears, boolean isObservable) {
      HpoDisease disease = HpoDiseaseExamples.marfanSyndrome();
      TemporalInterval interval = TemporalInterval.of(AgeSinceBirth.of(startYears, 0, 0), AgeSinceBirth.of(endYears, 0, 0));
      assertThat(instance.isObservableInAge(disease, interval), is(isObservable));
    }

  }


}
