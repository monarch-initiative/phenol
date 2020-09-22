package org.monarchinitiative.phenol.annotations.formats;

public enum EvidenceCode {
  IEA, TAS, PCS;

  public static EvidenceCode fromString(String code) {
    switch(code.toUpperCase()) {
      case "IEA":
        return IEA;
      case "TAS":
        return TAS;
      case "PCS":
        return PCS;
      default:
        return IEA;
    }
  }

}
