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
import java.util.stream.Stream;

/**
 * {@link HpoDiseaseAnnotation} aggregates the presentation of single phenotypic feature observed in a cohort of patients
 * diagnosed with a disease (i.e. <a href="https://hpo.jax.org/app/browse/term/HP:0001166">Arachnodactyly (HP:0001166)</a>
 * in <a href="https://hpo.jax.org/app/browse/disease/OMIM:154700">Marfan syndrome (OMIM:154700)</a>).
 * <p>
 * {@link HpoDiseaseAnnotation} aggregates <em>frequency</em> of the phenotypic feature in patients diagnosed with a disease.
 * The frequency is modeled as a {@link #ratio()} <code>n/m</code> where <code>n</code> is the number of patients
 * presenting the feature, and <code>m</code> is the cohort size. The feature frequency is available through
 * {@link #frequency()}.
 * <p>
 * The temporal aspect of the feature presentation is exposed via {@link #observationIntervals()} that provides
 * {@link TemporalRange}s when the {@link HpoDiseaseAnnotation} is observable in one or more patient or via
 * {@link #observedInInterval(TemporalRange)} to get a {@link Ratio} of patients presenting the feature in provided
 * {@link TemporalRange}.
 * <p>
 * The evidence supporting the phenotypic feature is available via {@link #references()}.
 */
public interface HpoDiseaseAnnotation extends Identified, Comparable<HpoDiseaseAnnotation> {

  Comparator<HpoDiseaseAnnotation> COMPARE_BY_ID = Comparator.comparing(HpoDiseaseAnnotation::id);

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
   * @deprecated metadata are an implementation detail.
   */
  @Deprecated(forRemoval = true)
  Stream<HpoDiseaseAnnotationMetadata> metadata();

  /**
   * @return ratio representing a total number of the cohort members who displayed presence of the phenotypic feature
   * represented by {@link HpoDiseaseAnnotation} at some point in their life.
   */
  Ratio ratio();

  /**
   * @return stream of {@link TemporalRange}s representing periods when the {@link HpoDiseaseAnnotation} was observable in
   * at least one cohort individual.
   */
  Stream<TemporalRange> observationIntervals();

  /**
   * Get the {@link Ratio} of patients presenting a phenotypic feature in given {@link TemporalRange}.
   *
   * @param interval target temporal interval.
   */
  Ratio observedInInterval(TemporalRange interval);

  /**
   * @return {@link AnnotationReference}s that support presence/absence of the disease annotation.
   */
  List<AnnotationReference> references();

  /* **************************************************************************************************************** */

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
   * @return {@link TemporalPoint} representing the earliest onset of the phenotypic feature in patient cohort.
   */
  default Optional<TemporalPoint> earliestOnset() {
    return observationIntervals()
      .map(TemporalRange::start)
      .min(TemporalPoint::compare);
  }

  /**
   * @return {@link TemporalPoint} representing the latest onset of the phenotypic feature in patient cohort.
   */
  default Optional<TemporalPoint> latestOnset() {
    return observationIntervals()
      .map(TemporalRange::start)
      .max(TemporalPoint::compare);
  }

  /**
   * @return {@link TemporalPoint} representing the earliest resolution of the phenotypic feature in patient cohort.
   */
  default Optional<TemporalPoint> earliestResolution() {
    return observationIntervals()
      .map(TemporalRange::end)
      .min(TemporalPoint::compare);
  }

  /**
   * @return {@link TemporalPoint} representing the latest resolution of the phenotypic feature in patient cohort.
   */
  default Optional<TemporalPoint> latestResolution() {
    return observationIntervals()
      .map(TemporalRange::end)
      .max(TemporalPoint::compare);
  }

  /**
   * @deprecated {@link HpoDiseaseAnnotation} will not be {@link Comparable} as there are multiple ways how to compare
   * the instances.
   */
  @Override
  @Deprecated(forRemoval = true)
  default int compareTo(HpoDiseaseAnnotation other) {
    return COMPARE_BY_ID.compare(this, other);
  }

  /**
   * @deprecated use {@link #id()}.
   */
  @Deprecated(since = "2.0.0-RC1", forRemoval = true)
  default TermId termId() {
    return id();
  }

}
