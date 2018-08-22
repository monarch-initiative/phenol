package org.monarchinitiative.phenol.formats.mpo;

import org.monarchinitiative.phenol.base.PhenolException;

public enum MpSex {
  FEMALE,MALE;


  public static MpSex fromString(String s) throws PhenolException {
    switch (s) {
      case "M":
      case "m":
        return MALE;
      case "F":
      case "f":
        return FEMALE;
      default:
        throw new PhenolException("Did not recognize sex symbol: \""+s+"\"");
    }
  }


}
