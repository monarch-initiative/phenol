package org.monarchinitiative.phenol.formats.mpo;


/**
 * This class represents the Genetic Background field of the {@code MGI_PhenoGenoMP.rpt} file as a string (without breaking
 * down its subparts).
 *
 * @author Hannah Blau (blauh)
 * @version 0.0.2
 * @since 12 Dec 2017
 */
public class MpStrain implements Comparable<MpStrain> {
  private final String strainStr;

  /**
   * Private constructor for ImmutableStrain objects.
   * @param background  string read from input data file
   */
  private MpStrain(String background) {
    strainStr = background;
  }

  /**
   * Factory method to construct an ImmutableStrain object.
   * @param strain           string read from input data file
   * @return MpStrain object constructed from the string
   */
  public static MpStrain fromString(String strain) {
    return new MpStrain(strain);
  }

  /**
   * Method to compare two ImmutableStrain objects relies on compareTo method of class String.
   * @param s     ImmutableStrain object to which this object is compared
   * @return int  outcome of comparing the two strainStr fields
   */
  @Override
  public int compareTo(MpStrain s) {
    return strainStr.compareTo(s.strainStr);
  }

  /**
   * Two ImmutableStrain objects are equal if their strainStr fields are equal.
   * @param o          Object tested for equality to this ImmutableStrain object
   * @return boolean   true if the two objects have equal strainStr fields, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MpStrain strain = (MpStrain) o;

    return strainStr.equals(strain.strainStr);
  }

  /**
   * Getter method for strainStr field.
   * @return   String the strainStr
   */
  public String getStrainStr() {
    return strainStr;
  }

  /**
   * hashCode method for ImmutableStrain relies on hashCode method of class String.
   * @return int    hash code for this ImmutableStrain object
   */
  @Override
  public int hashCode() {
    return strainStr.hashCode();
  }

  /**
   * toString method of ImmutableStrain.
   * @return String     printable representation of this ImmutableStrain object
   */
  @Override
  public String toString() {
    return  strainStr;
  }
}
