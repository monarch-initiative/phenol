package org.monarchinitiative.phenol.formats.mpo;

public class MpModifier {
  private final MpModifierType type;
  private final static String EMPTY_STRING="";
  /** A String to hold data that is specific to a modifier type */
  private final String payload;

  MpModifier(MpModifierType mt){
    this.type=mt;
    payload=EMPTY_STRING; // i.e., this modifier has no additional information
  }

  public MpModifier(MpModifierType mt, String data){
    this.type=mt;
    payload=data;
  }

  /**
   * @return The "payload", an optional bit of additional information for some modifier types formated as a String.
   */
  public String getPayload() {
    return payload;
  }
  public MpModifierType getType() { return this.type; }
  @Override
  public String toString() { return this.type.toString(); }

  boolean maleSpecific() { return type.equals(MpModifierType.MALE_SPECIFIC); }
  boolean femaleSpecific() { return type.equals(MpModifierType.FEMALE_SPECIFIC); }
  boolean sexSpecific() { return ( type.equals(MpModifierType.MALE_SPECIFIC) ||
    type.equals(MpModifierType.FEMALE_SPECIFIC) ); }
  boolean maleSpecificNormal() { return type.equals(MpModifierType.MALE_SPECIFIC_NORMAL); }
  boolean femaleSpecificNormal() { return type.equals(MpModifierType.FEMALE_SPECIFIC_NORMAL); }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MpModifier that = (MpModifier) o;

    if (type != that.type) return false;
    return payload != null ? payload.equals(that.payload) : that.payload == null;
  }

  @Override
  public int hashCode() {
    int result = type.hashCode();
    result = 31 * result + (payload != null ? payload.hashCode() : 0);
    return result;
  }
}
