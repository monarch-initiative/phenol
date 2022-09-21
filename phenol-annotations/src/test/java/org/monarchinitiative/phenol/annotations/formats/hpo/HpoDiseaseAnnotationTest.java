package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class HpoDiseaseAnnotationTest {

  /**
   * Tests that operate on a simple {@link HpoDiseaseAnnotation} instance.
   * All {@link HpoDiseaseAnnotationRecord} lack the resolution feature, and use {@link TemporalInterval#openEnd(PointInTime)}.
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
          HpoDiseaseAnnotationRecord.of(Ratio.of(0, 5), TemporalInterval.openEnd(PointInTime.of(1, false)), List.of(AnnotationReference.of(TermId.of("PMID:123456"), EvidenceCode.PCS)), Sex.MALE, noModifiersForNow),
          HpoDiseaseAnnotationRecord.of(Ratio.of(10, 10), TemporalInterval.openEnd(PointInTime.of(2, false)), List.of(AnnotationReference.of(TermId.of("OMIM:614856"), EvidenceCode.PCS)), Sex.MALE, noModifiersForNow),
          HpoDiseaseAnnotationRecord.of(Ratio.of(5, 15), TemporalInterval.openEnd(PointInTime.of(3, false)), List.of(AnnotationReference.of(TermId.of("ISBN:978-80-8144-105-9"), EvidenceCode.PCS)), Sex.MALE, noModifiersForNow),
          HpoDiseaseAnnotationRecord.of(Ratio.of(0, 20), TemporalInterval.openEnd(PointInTime.of(4, false)), List.of(AnnotationReference.of(TermId.of("PMID:987654321"), EvidenceCode.PCS)), Sex.MALE, noModifiersForNow)
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
      Optional<PointInTime> optional = instance.earliestOnset();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(PointInTime.of(2, false)));
    }

    @Test
    public void latestOnset() {
      Optional<PointInTime> optional = instance.latestOnset();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(PointInTime.of(3, false)));
    }

    @Test
    public void earliestResolution() {
      Optional<PointInTime> optional = instance.earliestResolution();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(PointInTime.openEnd()));
    }

    @Test
    public void latestResolution() {
      Optional<PointInTime> optional = instance.latestResolution();

      assertThat(optional.isPresent(), equalTo(true));
      assertThat(optional.get(), equalTo(PointInTime.openEnd()));
    }

    @ParameterizedTest
    @CsvSource({
      "1,  2,       0",
      "2,  3,      10",
      "3,  4,      15",
      "4,  5,      15",
      "0, 10,      15",
    })
    public void observedInPeriod(int start, int end, int numerator) {
      TemporalInterval interval = TemporalInterval.of(PointInTime.of(start, false), PointInTime.of(end, false));
      int nPatients = instance.observedInInterval(interval);

      assertThat(nPatients, equalTo(numerator));
    }

    @Test
    public void observedInPeriod_noData() {
      TemporalInterval interval = TemporalInterval.of(PointInTime.of(0, false), PointInTime.of(1, false));
      assertThat(instance.observedInInterval(interval) > 0, equalTo(false));
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
          createAnnotationRecords(1, 12, 1, 2),
          createAnnotationRecords(2, 15, 1, 1),
          createAnnotationRecords(2, 15, 3, 4),
          createAnnotationRecords(5, 9, 0, 2),
          createAnnotationRecords(11, 20, 5, 10),
          createAnnotationRecords(30, 35, 1, 5)
        ));
    }

    @ParameterizedTest
    @CsvSource({
      " 1,  2,    1",
      " 2,  3,    5",
      " 9, 11,    5",
      " 9, 14,   10",
      " 0, 10,    5",
    })
    public void observedInPeriod(int startDays, int endDays, int numerator) {
      TemporalInterval interval = TemporalInterval.of(PointInTime.of(startDays, false), PointInTime.of(endDays, false));
      int nPatients = instance.observedInInterval(interval);

      assertThat(nPatients, equalTo(numerator));
    }

    @Test
    public void observedInPeriod_noData() {
      TemporalInterval before = TemporalInterval.of(PointInTime.of(0, false), PointInTime.of(0, false));
      assertThat(instance.observedInInterval(before) > 0, equalTo(false));

      TemporalInterval between = TemporalInterval.of(PointInTime.of(20, false), PointInTime.of(30, false));
      assertThat(instance.observedInInterval(between) > 0, equalTo(false));

      TemporalInterval after = TemporalInterval.of(PointInTime.of(35, false), PointInTime.of(36, false));
      assertThat(instance.observedInInterval(after) > 0, equalTo(false));
    }

  }

  private static HpoDiseaseAnnotationRecord createAnnotationRecords(int startDays, int endDays, int numerator, int denominator) {
    return HpoDiseaseAnnotationRecord.of(
      Ratio.of(numerator, denominator),
      TemporalInterval.of(PointInTime.of(startDays, false), PointInTime.of(endDays, false)),
      List.of(AnnotationReference.of(TermId.of("PMID:123456"), EvidenceCode.PCS)),
      Sex.MALE,
      List.of());
  }
}
