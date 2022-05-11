package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.base.temporal.Age;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a single phenotypic annotation for a disease, i.e. <em>Ectopia lentis</em> for Marfan syndrome.
 */
public interface HpoDiseaseAnnotation extends Identified, Comparable<HpoDiseaseAnnotation> {

  // TODO - implement real comparator
  Comparator<HpoDiseaseAnnotation> COMPARATOR = Comparator.comparing(HpoDiseaseAnnotation::id);

  static HpoDiseaseAnnotation of(TermId termId, Collection<HpoDiseaseAnnotationMetadata> metadata) {
    float frequency = metadata.stream()
      .map(meta -> meta.frequency().flatMap(AnnotationFrequency::ratio))
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
  @Deprecated(since = "2.0.0-RC1", forRemoval = true)
  default TermId termId() {
    return id();
  }

  Stream<HpoDiseaseAnnotationMetadata> metadata();

  /**
   * @return ratio representing number of individuals with this {@link HpoDiseaseAnnotation} or an empty optional
   * if no occurrence data is available.
   */
  Optional<Ratio> ratio();

  default Optional<Float> frequency() {
    return ratio().map(Ratio::frequency);
  }

  /**
   * @return {@code true} if the phenotypic feature in question was annotated to be absent in the disease
   * (meaning that the numerator of {@link #ratio()} is zero, because an annotation such as <em>0/k</em> exists
   * that represents a study in which zero of <em>k</em> study participants were observed not to have the HPO term
   * in question).
   */
  default boolean isAbsent() {
    return ratio().map(Ratio::isZero)
      .orElse(false);
  }

  /**
   * @return list of {@link TemporalInterval}s representing periods when the {@link HpoDiseaseAnnotation} is observable.
   */
  List<TemporalInterval> observationIntervals();

  /**
   * @param target temporal interval
   * @return ratio of patients with {@link HpoDiseaseAnnotation} observable in given <code>target</code> {@link TemporalInterval}
   * or an empty {@link Optional} if no occurrence data is available
   */
  Optional<Ratio> observedInInterval(TemporalInterval target);

  /* **************************************************************************************************************** */

  default Optional<Age> earliestOnset() {
    return metadata()
      .filter(meta -> meta.frequency().flatMap(AnnotationFrequency::ratio).map(Ratio::isPositive).orElse(false))
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalInterval::start)
      .min(Age::compare);
  }

  default Optional<Age> latestOnset() {
    return metadata()
      .filter(meta -> meta.frequency().flatMap(AnnotationFrequency::ratio).map(Ratio::isPositive).orElse(false))
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalInterval::start)
      .max(Age::compare);
  }

  default Optional<Age> earliestResolution() {
    return metadata()
      .filter(meta -> meta.frequency().flatMap(AnnotationFrequency::ratio).map(Ratio::isPositive).orElse(false))
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalInterval::end)
      .min(Age::compare);
  }

  default Optional<Age> latestResolution() {
    return metadata()
      .filter(meta -> meta.frequency().flatMap(AnnotationFrequency::ratio).map(Ratio::isPositive).orElse(false))
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalInterval::end)
      .max(Age::compare);
  }

  default List<AnnotationReference> references() {
    return metadata()
      .map(HpoDiseaseAnnotationMetadata::reference)
      .collect(Collectors.toList());
  }

  @Override
  default int compareTo(HpoDiseaseAnnotation other) {
    return COMPARATOR.compare(this, other);
  }

}
