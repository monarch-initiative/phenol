package org.monarchinitiative.phenol.annotations.formats;

import java.util.Optional;

public enum Aspect {

  PHENOTYPIC_ABNORMALITY,
  INHERITANCE,
  ONSET_AND_CLINICAL_COURSE,
  CLINICAL_MODIFIER;

  public static Optional<Aspect> fromString(String value) {
    switch (value.toUpperCase()) {
      case "P":
        return Optional.of(PHENOTYPIC_ABNORMALITY);
      case "I":
        return Optional.of(INHERITANCE);
      case "C":
        return Optional.of(ONSET_AND_CLINICAL_COURSE);
      case "M":
        return Optional.of(CLINICAL_MODIFIER);
      default:
        return Optional.empty();
    }
  }
}
