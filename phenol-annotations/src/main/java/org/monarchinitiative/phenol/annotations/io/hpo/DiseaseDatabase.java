package org.monarchinitiative.phenol.annotations.io.hpo;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

public enum DiseaseDatabase {
  OMIM("OMIM"),
  ORPHANET("ORPHA"),
  DECIPHER("DECIPHER"),
  UNKNOWN("UNKNOWN");

  private final String prefix;

  DiseaseDatabase(String prefix) {
    this.prefix = prefix;
  }

  public String prefix() {
    return prefix;
  }

  public static DiseaseDatabase fromString(String s) {
    switch (s.toUpperCase(Locale.ROOT)) {
      case "OMIM":
        return OMIM;
      case "ORPHA":
      case "ORPHANET":
        return ORPHANET;
      case "DECIPHER":
        return DECIPHER;
      default:
        return UNKNOWN;
    }
  }

  public static Set<DiseaseDatabase> allKnownDiseaseDatabases() {
    return EnumSet.of(OMIM, ORPHANET, DECIPHER);
  }

}
