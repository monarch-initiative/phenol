package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.base.temporal.Age;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Optional;

public interface HpoDiseaseAnnotationMetadata {

  static HpoDiseaseAnnotationMetadata of(AnnotationReference reference,
                                         TemporalInterval temporalInterval,
                                         AnnotationFrequency frequency,
                                         Collection<TermId> modifiers,
                                         Sex sex) {
    return new HpoDiseaseAnnotationMetadataDefault(reference, temporalInterval, frequency, modifiers, sex);
  }

  /**
   * @return source of the annotation metadata.
   */
  AnnotationReference reference();

  Optional<TemporalInterval> observationInterval();

  /**
   * @return frequency of the disease annotation.
   */
  Optional<AnnotationFrequency> frequency();

  Collection<TermId> modifiers();

  Optional<Sex> sex();

  default Optional<Age> start() {
    return observationInterval()
      .map(TemporalInterval::start);
  }

  default Optional<Age> end() {
    return observationInterval()
      .map(TemporalInterval::end);
  }

}
