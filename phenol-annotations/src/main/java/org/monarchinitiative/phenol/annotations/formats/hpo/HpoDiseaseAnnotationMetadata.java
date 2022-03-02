package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Age;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.base.TemporalRange;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Optional;

public interface HpoDiseaseAnnotationMetadata {

  static HpoDiseaseAnnotationMetadata of(TemporalRange temporalRange,
                                         AnnotationFrequency frequency,
                                         Collection<TermId> modifiers,
                                         Sex sex) {
    return new HpoDiseaseAnnotationMetadataDefault(temporalRange, frequency, modifiers, sex);
  }

  Optional<TemporalRange> temporalRange();

  default Optional<Age> start() {
    return temporalRange()
      .map(TemporalRange::start);
  }

  default Optional<Age> end() {
    return temporalRange()
      .map(TemporalRange::end);
  }

  AnnotationFrequency frequency();

  Collection<TermId> modifiers();

  Optional<Sex> sex();

}
