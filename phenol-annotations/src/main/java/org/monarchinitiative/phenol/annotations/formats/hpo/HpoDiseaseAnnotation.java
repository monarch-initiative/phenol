package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.hpo.annotation_impl.HpoDiseaseAnnotationRecordBacked;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
 * The temporal aspect of the feature presentation is exposed via the following methods:
 * <ul>
 *   <li>{@link #earliestOnset()}</li>
 *   <li>{@link #latestOnset()}</li>
 *   <li>{@link #earliestResolution()}</li>
 *   <li>{@link #latestResolution()}</li>
 * </ul>
 * In addition, {@link #observedInInterval(TemporalInterval)} provides a {@link Ratio} of patients presenting
 * the feature in provided {@link TemporalInterval}.
 * <p>
 * The evidence supporting the phenotypic feature is available via {@link #references()}. The modifiers are exposed via
 * {@link #modifiers()}.
 */
public interface HpoDiseaseAnnotation extends Identified, Comparable<HpoDiseaseAnnotation> {

  Comparator<HpoDiseaseAnnotation> COMPARE_BY_ID = Comparator.comparing(HpoDiseaseAnnotation::id);

  static HpoDiseaseAnnotation of(TermId id, Collection<HpoDiseaseAnnotationRecord> records) {
    return HpoDiseaseAnnotationRecordBacked.of(id, records);
  }

  /**
   * @return ratio representing a total number of the cohort members who displayed presence of the phenotypic feature
   * represented by {@link HpoDiseaseAnnotation} at some point in their life.
   */
  Ratio ratio();

  /**
   * @return {@link PointInTime} representing the earliest onset of the phenotypic feature in patient cohort.
   */
  Optional<PointInTime> earliestOnset();

  /**
   * @return {@link PointInTime} representing the latest onset of the phenotypic feature in patient cohort.
   */
  Optional<PointInTime> latestOnset();

  /**
   * @return {@link PointInTime} representing the earliest resolution of the phenotypic feature in patient cohort.
   */
  Optional<PointInTime> earliestResolution();

  /**
   * @return {@link PointInTime} representing the latest resolution of the phenotypic feature in patient cohort.
   */
  Optional<PointInTime> latestResolution();

  /**
   * Get the number of patients presenting a phenotypic feature in given {@link TemporalInterval}.
   *
   * @param query query temporal interval.
   */
  int observedInInterval(TemporalInterval query);

  /**
   * @return a list of disease annotation modifiers.
   */
  List<TermId> modifiers();

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
   * Compare two {@link HpoDiseaseAnnotation}s by their {@link #id()}s.
   */
  static int compareById(HpoDiseaseAnnotation x, HpoDiseaseAnnotation y) {
    return COMPARE_BY_ID.compare(x, y);
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

  /**
   * @return iterable of {@link TemporalInterval}s representing periods when the {@link HpoDiseaseAnnotation} was observable in
   * at least one cohort individual.
   * @deprecated will not stay in the API
   */
  @Deprecated(forRemoval = true, since = "2.0.0")
  default Iterable<TemporalInterval> observationIntervals() {
    return null;
  }

}
