package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.base.temporal.Timestamp;
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

  Optional<TemporalInterval> temporalRange();

  default Optional<Timestamp> start() {
    return temporalRange()
      .map(TemporalInterval::start);
  }

  default Optional<Timestamp> end() {
    return temporalRange()
      .map(TemporalInterval::end);
  }

  AnnotationFrequency frequency();

  Collection<TermId> modifiers();

  Optional<Sex> sex();

}
