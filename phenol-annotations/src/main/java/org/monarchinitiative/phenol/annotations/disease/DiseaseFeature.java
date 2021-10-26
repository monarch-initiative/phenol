package org.monarchinitiative.phenol.annotations.disease;

import org.monarchinitiative.phenol.annotations.base.AgeRange;
import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Objects;
import java.util.Optional;

/**
 * Disease feature aggregates duration, frequency and sex data for a phenotypic feature observed in individuals
 * with a {@link Disease}.
 */
public interface DiseaseFeature {

  static DiseaseFeature of(TermId id, AgeRange duration, DiseaseFeatureFrequency frequency, Sex sex) {
    return new DiseaseFeatureDefault(Objects.requireNonNull(id, "ID must not be null"),
      Objects.requireNonNull(duration, "Duration must not be null"),
      Objects.requireNonNull(frequency, "Frequency must not be null"),
      sex);
  }

  TermId id();

  AgeRange duration();

  DiseaseFeatureFrequency frequency();

  Optional<Sex> sex();

  // TODO - severity, position/spatial pattern ?

}
