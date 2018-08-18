package org.monarchinitiative.phenol.formats.mpo;

public class MpModifier {
  private final ModifierType type;
  private final static String EMPTY_STRING="";
  /** A String to hold data that is specific to a modifier type */
  private final String payload;

  public MpModifier(ModifierType mt){
    this.type=mt;
    payload=EMPTY_STRING; // i.e., this modifier has no additional information
  }

  public MpModifier(ModifierType mt,String data){
    this.type=mt;
    payload=data;
  }

  public ModifierType getType() { return this.type; }
  @Override
  public String toString() { return this.type.toString(); }

  public boolean maleSpecific() { return type.equals(ModifierType.MALE_SPECIFIC); }
  public boolean femaleSpecific() { return type.equals(ModifierType.FEMALE_SPECIFIC); }
  public boolean sexSpecific() { return ( type.equals(ModifierType.MALE_SPECIFIC) ||
    type.equals(ModifierType.FEMALE_SPECIFIC) ); }
}
