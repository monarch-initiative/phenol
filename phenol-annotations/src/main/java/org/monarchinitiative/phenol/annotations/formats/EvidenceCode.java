package org.monarchinitiative.phenol.annotations.formats;

public enum EvidenceCode {
  IEA, TAS, PCS;

  /**
   * Return an enum to represent one of the HPO evidence codes. It should never happen
   * that we get a null pointer for code because of the Q/C of that annotation files,
   * but if this does happen we return the default code IEA.
   * @param code
   * @return an evidence code enum constant representing the evidence for a phenotype annotation
   */
  public static EvidenceCode fromString(String code) {
    if (code == null) return IEA;
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
