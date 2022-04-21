package org.monarchinitiative.phenol.annotations.base;

import java.util.Optional;

public enum Sex {
  MALE,
  FEMALE;

  public static Optional<Sex> parse(String value) {
    switch (value.toUpperCase()) {
      case "MALE":
        return Optional.of(MALE);
      case "FEMALE":
        return Optional.of(FEMALE);
      default:
        return Optional.empty();
    }
  }

}
