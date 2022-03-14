package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.base.temporal.Timestamp;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class HpoDiseaseAnnotationTest {

  /**
   * Tests that operate on a simple {@link HpoDiseaseAnnotation} instance.
   * All {@link HpoDiseaseAnnotationMetadata} lack the resolution feature, and use {@link TemporalInterval#openEnd(Timestamp)}.
   */
  @Nested
  public class SimpleTest {

    private HpoDiseaseAnnotation instance;

    @BeforeEach
    public void setUp() {
      List<TermId> noModifiersForNow = List.of();
      instance = HpoDiseaseAnnotation.of(
        TermId.of("HP:123456"),
        List.of(
          HpoDiseaseAnnotationMetadata.of(TemporalInterval.openEnd(Timestamp.of(1)), AnnotationFrequency.of(Ratio.of(0, 5)), noModifiersForNow, Sex.MALE),
          HpoDiseaseAnnotationMetadata.of(TemporalInterval.openEnd(Timestamp.of(2)), AnnotationFrequency.of(Ratio.of(10, 10)), noModifiersForNow, Sex.MALE),
          HpoDiseaseAnnotationMetadata.of(TemporalInterval.openEnd(Timestamp.of(3)), AnnotationFrequency.of(Ratio.of(5, 15)), noModifiersForNow, Sex.MALE),
          HpoDiseaseAnnotationMetadata.of(TemporalInterval.openEnd(Timestamp.of(4)), AnnotationFrequency.of(Ratio.of(0, 20)), noModifiersForNow, Sex.MALE)
        ));
    }

    @Test
    public void ratio() {
      Optional<Ratio> ratioOptional = instance.ratio();

      assertThat(ratioOptional.isPresent(), equalTo(true));
      Ratio ratio = ratioOptional.get();
      assertThat(ratio.numerator(), equalTo(15));
      assertThat(ratio.denominator(), equalTo(50));
    }

    @Test
    public void earliestOnset() {
      Optional<Timestamp> optional = instance.earliestOnset();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(Timestamp.of(2)));
    }

    @Test
    public void latestOnset() {
      Optional<Timestamp> optional = instance.latestOnset();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(Timestamp.of(3)));
    }

    @Test
    public void earliestResolution() {
      Optional<Timestamp> optional = instance.earliestResolution();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(Timestamp.openEnd()));
    }

    @Test
    public void latestResolution() {
      Optional<Timestamp> optional = instance.latestResolution();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(Timestamp.openEnd()));
    }

    @Test
    public void observationIntervals() {
      List<TemporalInterval> intervals = instance.observationIntervals();
      assertThat(intervals, hasSize(1));
      assertThat(intervals, hasItem(TemporalInterval.openEnd(Timestamp.of(1))));
    }

    @ParameterizedTest
    @CsvSource({
      "1,  2,       0,  5",
      "2,  3,      10, 15",
      "3,  4,      15, 30",
      "4,  5,      15, 50",
      "0, 10,      15, 50",
    })
    public void observedInPeriod(int start, int end, int numerator, int denominator) {
      TemporalInterval interval = TemporalInterval.of(Timestamp.of(start), Timestamp.of(end));
      Optional<Ratio> ratio = instance.observedInInterval(interval);

      assertThat(ratio.isPresent(), equalTo(true));
      assertThat(ratio.get(), equalTo(Ratio.of(numerator, denominator)));
    }

    @Test
    public void observedInPeriod_noData() {
      TemporalInterval interval = TemporalInterval.of(Timestamp.of(0), Timestamp.of(1));
      assertThat(instance.observedInInterval(interval).isPresent(), is(false));
    }
  }

  @Nested
  public class OtherTests {

    private HpoDiseaseAnnotation instance;

    @BeforeEach
    public void setUp() {
      instance = HpoDiseaseAnnotation.of(
        TermId.of("HP:123456"),
        List.of(
          createMetadata(0, 12, 1, 2),
          createMetadata(2, 15, 1, 1),
          createMetadata(2, 15, 3, 4),
          createMetadata(5, 9, 0, 2),
          createMetadata(11, 20, 5, 10),
          createMetadata(30, 35, 1, 5)
        ));
    }

    @ParameterizedTest
    @CsvSource({
      " 0,  1,    1,  2",
      " 2,  3,    5,  7",
      " 9, 11,    5,  7",
      " 9, 14,   10, 17",
      " 0, 10,    5,  9",
    })
    public void observedInPeriod(int startDays, int endDays, int numerator, int denominator) {
      TemporalInterval interval = TemporalInterval.of(Timestamp.of(startDays), Timestamp.of(endDays));
      Optional<Ratio> ratio = instance.observedInInterval(interval);

      assertThat(ratio.isPresent(), equalTo(true));
      assertThat(ratio.get(), equalTo(Ratio.of(numerator, denominator)));
    }

    @Test
    public void observedInPeriod_noData() {
      TemporalInterval before = TemporalInterval.of(Timestamp.of(-1), Timestamp.of(0));
      assertThat(instance.observedInInterval(before).isPresent(), is(false));

      TemporalInterval between = TemporalInterval.of(Timestamp.of(20), Timestamp.of(30));
      assertThat(instance.observedInInterval(between).isPresent(), is(false));

      TemporalInterval after = TemporalInterval.of(Timestamp.of(35), Timestamp.of(36));
      assertThat(instance.observedInInterval(after).isPresent(), is(false));
    }

    @Test
    public void observationIntervals() {
      List<TemporalInterval> intervals = instance.observationIntervals();

      assertThat(intervals, hasSize(2));
      assertThat(intervals, hasItems(
        TemporalInterval.of(Timestamp.zero(), Timestamp.of(20)),
        TemporalInterval.of(Timestamp.of(30), Timestamp.of(35))
        ));
    }
  }

  private static HpoDiseaseAnnotationMetadata createMetadata(int startDays, int endDays, int numerator, int denominator) {
    return HpoDiseaseAnnotationMetadata.of(
      TemporalInterval.of(Timestamp.of(startDays), Timestamp.of(endDays)),
      AnnotationFrequency.of(Ratio.of(numerator, denominator)),
      List.of(), Sex.MALE);
  }
}
