package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalPoint;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalRange;
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
   * @return ratio representing number of individuals with this {@link HpoDiseaseAnnotation}.
   */
  Ratio ratio();

  /**
   * @return frequency of this {@link HpoDiseaseAnnotation} in the cohort of individuals used to assert
   * the presence of the annotation in an {@link HpoDisease}.
   */
  default float frequency() {
    return ratio().frequency();
  }

  /**
   * @return {@code true} if the phenotypic feature in question was annotated to be absent in the disease
   * (meaning that the numerator of {@link #ratio()} is zero, because an annotation such as <em>0/k</em> exists
   * that represents a study in which zero of <em>k</em> study participants were observed not to have the HPO term
   * in question).
   */
  default boolean isAbsent() {
    return ratio().isZero();
  }

  /**
   * @return the opposite of what {@link #isAbsent()} returns.
   * @see #isAbsent()
   */
  default boolean isPresent() {
    return !isAbsent();
  }


  /**
   * @return list of {@link TemporalRange}s representing periods when the {@link HpoDiseaseAnnotation} is observable.
   */
  List<TemporalRange> observationIntervals();

  /**
   * @param target temporal interval
   * @return ratio of patients with {@link HpoDiseaseAnnotation} observable in given <code>target</code> {@link TemporalRange}
   * or an empty {@link Optional} if no occurrence data is available
   */
  Optional<Ratio> observedInInterval(TemporalRange target);

  /* **************************************************************************************************************** */

  default Optional<TemporalPoint> earliestOnset() {
    return metadata()
      .filter(HpoDiseaseAnnotationMetadata::isPresent)
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalRange::start)
      .min(TemporalPoint::compare);
  }

  default Optional<TemporalPoint> latestOnset() {
    return metadata()
      .filter(HpoDiseaseAnnotationMetadata::isPresent)
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalRange::start)
      .max(TemporalPoint::compare);
  }

  default Optional<TemporalPoint> earliestResolution() {
    return metadata()
      .filter(HpoDiseaseAnnotationMetadata::isPresent)
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalRange::end)
      .min(TemporalPoint::compare);
  }

  default Optional<TemporalPoint> latestResolution() {
    return metadata()
      .filter(HpoDiseaseAnnotationMetadata::isPresent)
      .map(HpoDiseaseAnnotationMetadata::observationInterval)
      .flatMap(Optional::stream)
      .map(TemporalRange::end)
      .max(TemporalPoint::compare);
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
