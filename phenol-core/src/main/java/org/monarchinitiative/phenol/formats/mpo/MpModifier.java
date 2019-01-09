package org.monarchinitiative.phenol.formats.mpo;

public class MpModifier {
  private final MpModifierType type;


  MpModifier(MpModifierType mt){
    this.type=mt;
  }

  public MpModifierType getType() { return this.type; }
  @Override
  public String toString() { return this.type.toString(); }

  boolean maleSpecific() { return type.equals(MpModifierType.MALE_SPECIFIC_ABNORMAL); }
  boolean femaleSpecific() { return type.equals(MpModifierType.FEMALE_SPECIFIC_ABNORMAL); }
  boolean sexSpecific() { return ( type.equals(MpModifierType.MALE_SPECIFIC_ABNORMAL) || type.equals(MpModifierType.FEMALE_SPECIFIC_ABNORMAL) ); }
  boolean maleSpecificNormal() { return type.equals(MpModifierType.MALE_SPECIFIC_NORMAL); }
  boolean femaleSpecificNormal() { return type.equals(MpModifierType.FEMALE_SPECIFIC_NORMAL); }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MpModifier that = (MpModifier) o;

    return type.equals(that.type);

  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }
}
