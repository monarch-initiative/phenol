package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.hpo.AnnotationFrequency;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Optional;

// TODO(ielis) - consider removing.
interface HpoDiseaseAnnotationMetadata {

//  static HpoDiseaseAnnotationMetadata of(AnnotationReference reference,
//                                         TemporalRange observationInterval,
//                                         AnnotationFrequency frequency,
//                                         Collection<TermId> modifiers,
//                                         Sex sex) {
//    return new HpoDiseaseAnnotationMetadataDefault(reference, observationInterval, frequency, modifiers, sex);
//  }

  /**
   * @return source of the annotation metadata.
   */
  AnnotationReference reference();

  /**
   * @return {@link TemporalInterval} that represents the interval when the {@link HpoDiseaseAnnotation} was observed.
   */
  Optional<TemporalInterval> observationInterval();

  /**
   * @return frequency of the disease annotation.
   */
  Optional<AnnotationFrequency> frequency();

  /**
   * @return the opposite of what {@link #isAbsent()} returns.
   * @see #isAbsent()
   */
  default boolean isPresent() {
    return !isAbsent();
  }

  /**
   * @return {@code true} if the phenotypic feature in question was annotated to be absent in the disease
   * (meaning that the numerator of {@link #frequency()} is zero, because an annotation such as <em>0/k</em> exists
   * that represents a study in which zero of <em>k</em> study participants were observed not to have the HPO term
   * in question).
   */
  default boolean isAbsent() {
    return frequency().flatMap(AnnotationFrequency::ratio)
      .map(Ratio::isZero)
      .orElse(false);
  }

  Collection<TermId> modifiers();

  Optional<Sex> sex();

  default Optional<PointInTime> start() {
    return observationInterval()
      .map(TemporalInterval::start);
  }

  default Optional<PointInTime> end() {
    return observationInterval()
      .map(TemporalInterval::end);
  }

}