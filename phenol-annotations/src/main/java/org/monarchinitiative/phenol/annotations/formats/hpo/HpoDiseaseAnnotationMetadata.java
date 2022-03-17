package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.base.temporal.AgeSinceBirth;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Optional;

public interface HpoDiseaseAnnotationMetadata {

  static HpoDiseaseAnnotationMetadata of(TemporalInterval temporalInterval,
                                         AnnotationFrequency frequency,
                                         Collection<TermId> modifiers,
                                         Sex sex) {
    return new HpoDiseaseAnnotationMetadataDefault(temporalInterval, frequency, modifiers, sex);
  }

  Optional<TemporalInterval> observationInterval();

  AnnotationFrequency frequency();

  Collection<TermId> modifiers();

  Optional<Sex> sex();

  default Optional<AgeSinceBirth> start() {
    return observationInterval()
      .map(TemporalInterval::start);
  }

  default Optional<AgeSinceBirth> end() {
    return observationInterval()
      .map(TemporalInterval::end);
  }

}
