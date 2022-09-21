package org.monarchinitiative.phenol.annotations.io.hpo;

import java.util.Optional;

/**
 * An enum for aspect column of {@link HpoAnnotationLine}.
 */
public enum Aspect {

  P,
  I,
  C,
  M;

  public static Optional<Aspect> parse(String aspect) {
    switch (aspect.toUpperCase()) {
      case "P":
        return Optional.of(Aspect.P);
      case "I":
        return Optional.of(Aspect.I);
      case "C":
        return Optional.of(Aspect.C);
      case "M":
        return Optional.of(Aspect.M);
      default:
        return Optional.empty();
    }
  }
}
