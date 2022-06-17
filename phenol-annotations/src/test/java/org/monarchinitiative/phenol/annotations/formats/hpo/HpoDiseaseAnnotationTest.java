package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalPoint;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalRange;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class HpoDiseaseAnnotationTest {

  /**
   * Tests that operate on a simple {@link HpoDiseaseAnnotation} instance.
   * All {@link HpoDiseaseAnnotationMetadata} lack the resolution feature, and use {@link TemporalRange#openEnd(TemporalPoint)}.
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
          HpoDiseaseAnnotationMetadata.of(createAnnotationReference("PMID:123456"), TemporalRange.openEnd(TemporalPoint.of(1, false)), AnnotationFrequency.of(Ratio.of(0, 5)), noModifiersForNow, Sex.MALE),
          HpoDiseaseAnnotationMetadata.of(createAnnotationReference("OMIM:614856"), TemporalRange.openEnd(TemporalPoint.of(2, false)), AnnotationFrequency.of(Ratio.of(10, 10)), noModifiersForNow, Sex.MALE),
          HpoDiseaseAnnotationMetadata.of(createAnnotationReference("ISBN:978-80-8144-105-9"), TemporalRange.openEnd(TemporalPoint.of(3, false)), AnnotationFrequency.of(Ratio.of(5, 15)), noModifiersForNow, Sex.MALE),
          HpoDiseaseAnnotationMetadata.of(createAnnotationReference("PMID:987654321"), TemporalRange.openEnd(TemporalPoint.of(4, false)), AnnotationFrequency.of(Ratio.of(0, 20)), noModifiersForNow, Sex.MALE)
        ));
    }

    @Test
    public void ratio() {
      Ratio ratio = instance.ratio();
      assertThat(ratio.numerator(), equalTo(15));
      assertThat(ratio.denominator(), equalTo(50));
    }

    @Test
    public void earliestOnset() {
      Optional<TemporalPoint> optional = instance.earliestOnset();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(TemporalPoint.of(2, false)));
    }

    @Test
    public void latestOnset() {
      Optional<TemporalPoint> optional = instance.latestOnset();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(TemporalPoint.of(3, false)));
    }

    @Test
    public void earliestResolution() {
      Optional<TemporalPoint> optional = instance.earliestResolution();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(TemporalPoint.openEnd()));
    }

    @Test
    public void latestResolution() {
      Optional<TemporalPoint> optional = instance.latestResolution();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(TemporalPoint.openEnd()));
    }

    @Test
    public void observationIntervals() {
      List<TemporalRange> intervals = instance.observationIntervals().collect(Collectors.toList());
      assertThat(intervals, hasSize(1));

      TemporalRange item = intervals.get(0);
      assertThat(item.start(), equalTo(TemporalPoint.of(1, false)));
      assertThat(item.isEndOpen(), equalTo(true));
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
      TemporalRange interval = TemporalRange.of(TemporalPoint.of(start, false), TemporalPoint.of(end, false));
      Ratio ratio = instance.observedInInterval(interval);

      assertThat(ratio, equalTo(Ratio.of(numerator, denominator)));
    }

    @Test
    public void observedInPeriod_noData() {
      TemporalRange interval = TemporalRange.of(TemporalPoint.of(0, false), TemporalPoint.of(1, false));
      assertThat(instance.observedInInterval(interval).isPositive(), is(false));
    }
  }

  public static AnnotationReference createAnnotationReference(String reference) {
    return AnnotationReference.of(TermId.of(reference), EvidenceCode.PCS);
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
      TemporalRange interval = TemporalRange.of(TemporalPoint.of(startDays, false), TemporalPoint.of(endDays, false));
      Ratio ratio = instance.observedInInterval(interval);

      assertThat(ratio, equalTo(Ratio.of(numerator, denominator)));
    }

    @Test
    public void observedInPeriod_noData() {
      TemporalRange before = TemporalRange.of(TemporalPoint.of(0, false), TemporalPoint.of(0, false));
      assertThat(instance.observedInInterval(before).isPositive(), is(false));

      TemporalRange between = TemporalRange.of(TemporalPoint.of(20, false), TemporalPoint.of(30, false));
      assertThat(instance.observedInInterval(between).isPositive(), is(false));

      TemporalRange after = TemporalRange.of(TemporalPoint.of(35, false), TemporalPoint.of(36, false));
      assertThat(instance.observedInInterval(after).isPositive(), is(false));
    }

    @Test
    public void observationIntervals() {
      List<TemporalRange> intervals = instance.observationIntervals().collect(Collectors.toList());

      assertThat(intervals, hasSize(2));
      assertThat(intervals, hasItems(
        TemporalRange.of(TemporalPoint.birth(), TemporalPoint.of(20, false)),
        TemporalRange.of(TemporalPoint.of(30, false), TemporalPoint.of(35, false))
        ));
    }
  }

  private static HpoDiseaseAnnotationMetadata createMetadata(int startDays, int endDays, int numerator, int denominator) {
    return HpoDiseaseAnnotationMetadata.of(createAnnotationReference("PMID:123456"),
      TemporalRange.of(TemporalPoint.of(startDays, false), TemporalPoint.of(endDays, false)),
      AnnotationFrequency.of(Ratio.of(numerator, denominator)),
      List.of(), Sex.MALE);
  }
}
