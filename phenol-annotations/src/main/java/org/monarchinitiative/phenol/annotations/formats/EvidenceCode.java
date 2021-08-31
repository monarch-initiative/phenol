package org.monarchinitiative.phenol.annotations.formats;

public enum EvidenceCode {
  IEA,
  TAS,
  PCS;

  /**
   * Return an enum to represent one of the HPO evidence codes. It should never happen
   * that we get a null pointer for code because of the Q/C of that annotation files,
   * but if this does happen we return the default value IEA.
   * @param value code value
   * @return an evidence value enum constant representing the evidence for a phenotype annotation
   */
  public static EvidenceCode fromString(String value) {
    if (value == null) return IEA;
    switch(value.toUpperCase()) {
      case "TAS":
        return TAS;
      case "PCS":
        return PCS;
      case "IEA":
      default:
        return IEA;
    }
  }

}
