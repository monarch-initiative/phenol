package org.monarchinitiative.phenol.annotations.formats;

public enum Sex {
  MALE,
  FEMALE,
  UNKNOWN;


  public static Sex fromString(String value) {
    switch (value.toUpperCase()) {
      case "MALE":
        return MALE;
      case "FEMALE":
        return FEMALE;
      default:
        return UNKNOWN;
    }
  }
}
