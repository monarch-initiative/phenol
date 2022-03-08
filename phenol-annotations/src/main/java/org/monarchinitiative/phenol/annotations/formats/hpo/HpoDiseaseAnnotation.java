package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.base.temporal.Timestamp;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents a single phenotypic annotation for a disease, i.e. <em>Ectopia lentis</em> for Marfan syndrome.
 */
public interface HpoDiseaseAnnotation extends Identified, Comparable<HpoDiseaseAnnotation> {

  static HpoDiseaseAnnotation of(TermId termId, Collection<HpoDiseaseAnnotationMetadata> metadata) {
    float frequency = metadata.stream()
      .map(HpoDiseaseAnnotationMetadata::frequency)
      .map(AnnotationFrequency::ratio)
      .flatMap(Optional::stream)
      .reduce(Ratio::combine)
      .map(Ratio::frequency)
      .orElse(0F);

    if (frequency > 1.0)
      throw new IllegalArgumentException("Total frequency cannot be greater than 1: " + frequency);

    return HpoDiseaseAnnotationDefault.of(termId, metadata);
  }


  /**
   * @deprecated use {@link #id()}.
   */
  @Deprecated
  default TermId termId() {
    return id();
  }

  /*
   TODO - do we really have to to expose the metadata stream? Probably not.
    We may just need to make the "default" methods non-default and keep `metadata` as an implementation detail.
   */
  Stream<HpoDiseaseAnnotationMetadata> metadata();

  default Optional<Ratio> observedInPeriod(TemporalInterval temporalInterval) {
    return metadata()
      .filter(meta -> meta.temporalRange().map(range -> range.overlapsWith(temporalInterval)).orElse(false))
      .map(HpoDiseaseAnnotationMetadata::frequency)
      .map(AnnotationFrequency::ratio)
      .flatMap(Optional::stream)
      .reduce(Ratio::combine);
  }

  default Optional<Ratio> ratio() {
    return metadata()
      .map(HpoDiseaseAnnotationMetadata::frequency)
      .map(AnnotationFrequency::ratio)
      .flatMap(Optional::stream)
      .reduce(Ratio::combine);
  }

  default Optional<Timestamp> earliestOnset() {
    return metadata()
      .map(HpoDiseaseAnnotationMetadata::temporalRange)
      .flatMap(Optional::stream)
      .map(TemporalInterval::start)
      .min(Timestamp::compare);
  }

  default Optional<Timestamp> latestOnset() {
    return metadata()
      .map(HpoDiseaseAnnotationMetadata::temporalRange)
      .flatMap(Optional::stream)
      .map(TemporalInterval::start)
      .max(Timestamp::compare);
  }

  default Optional<Timestamp> earliestResolution() {
    return metadata()
      .map(HpoDiseaseAnnotationMetadata::temporalRange)
      .flatMap(Optional::stream)
      .map(TemporalInterval::end)
      .min(Timestamp::compare);
  }

  default Optional<Timestamp> latestResolution() {
    return metadata()
      .map(HpoDiseaseAnnotationMetadata::temporalRange)
      .flatMap(Optional::stream)
      .map(TemporalInterval::end)
      .max(Timestamp::compare);
  }

}
