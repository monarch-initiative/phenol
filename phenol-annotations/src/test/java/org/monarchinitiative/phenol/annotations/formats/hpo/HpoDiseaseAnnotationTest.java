package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

  private HpoDiseaseAnnotation annotation;

  @BeforeEach
  public void setUp() {
    annotation = HpoDiseaseAnnotation.of(TermId.of("HP:123456"),
      List.of(
        HpoDiseaseAnnotationMetadata.of(TemporalInterval.openEnd(Timestamp.zero()), AnnotationFrequency.of(Ratio.of(11, 20)), List.of(), Sex.MALE),
        HpoDiseaseAnnotationMetadata.of(TemporalInterval.openEnd(Timestamp.of(1, 0, 0)), AnnotationFrequency.of(Ratio.of(5, 10)), List.of(), Sex.MALE),
        HpoDiseaseAnnotationMetadata.of(TemporalInterval.openEnd(Timestamp.of(2, 0, 0)), AnnotationFrequency.of(Ratio.of(0, 2)), List.of(), Sex.MALE)
      ));
  }

  @Test
  public void ratio() {
    Optional<Ratio> ratioOptional = annotation.ratio();

    assertThat(ratioOptional.isPresent(), equalTo(true));
    Ratio ratio = ratioOptional.get();
    assertThat(ratio.numerator(), equalTo(16));
    assertThat(ratio.denominator(), equalTo(32));
  }

  @Test
  public void earliestOnset() {
    Optional<Timestamp> onsetOptional = annotation.earliestOnset();

    assertThat(onsetOptional.isPresent(), equalTo(true));
    Timestamp onset = onsetOptional.get();
    assertThat(onset, equalTo(Timestamp.zero()));
  }

  @Test
  public void latestOnset() {
    Optional<Timestamp> onsetOptional = annotation.latestOnset();

    assertThat(onsetOptional.isPresent(), equalTo(true));
    Timestamp onset = onsetOptional.get();
    assertThat(onset, equalTo(Timestamp.of(2, 0, 0)));
  }
}
