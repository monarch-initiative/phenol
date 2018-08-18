package org.monarchinitiative.phenol.formats.mpo;

import java.util.Objects;

/**
 * This class represents the Allelic Composition field of the MGI_PhenoGenoMP.rpt file as a string (without breaking
 * down its subparts).
 *
 * @author Hannah Blau (blauh)
 * @version 0.0.1
 * @since 12 Dec 2017
 */
public class MpAllelicComposition implements Comparable<MpAllelicComposition> {
  private final String allelicCompStr;

  /**
   * Private constructor for ImmutableAllelicComp objects.
   * @param allelicComp  string read from input data file
   */
  private MpAllelicComposition(String allelicComp) {
    allelicCompStr = allelicComp;
  }

  /**
   * Factory method to construct an ImmutableAllelicComp object.
   * @param allelicComp           string read from input data file
   * @return ImmutableAllelicComp object constructed from the string
   */
  public static MpAllelicComposition fromString(String allelicComp) {
    return new MpAllelicComposition(allelicComp);
  }

  /**
   * Method to compare two ImmutableAllelicComp objects relies on compareTo method of class String.
   * @param ac    ImmutableAllelicComp object to which this object is compared
   * @return int  outcome of comparing the two allelicCompStr fields
   */
  @Override
  public int compareTo( MpAllelicComposition ac) {
    Objects.requireNonNull(ac, "Cannot compare to null ImmutableAllelicComp object");
    return allelicCompStr.compareTo(ac.allelicCompStr);
  }

  /**
   * Two ImmutableAllelicComp objects are equal if their allelicCompStr fields are equal.
   * @param o          Object tested for equality to this ImmutableAllelicComp object
   * @return boolean   true if the two objects have equal allelicCompStr fields, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MpAllelicComposition that = (MpAllelicComposition) o;

    return allelicCompStr.equals(that.allelicCompStr);
  }

  /**
   * Getter method for allelicCompStr field.
   * @return   String the allelicCompStr
   */
  public String getAllelicCompStr() {
    return allelicCompStr;
  }

  /**
   * hashCode method for ImmutableAllelicComp relies on hashCode method of class String.
   * @return int    hash code for this ImmutableAllelicComp object
   */
  @Override
  public int hashCode() {
    return allelicCompStr.hashCode();
  }

  /**
   * toString method of ImmutableAllelicComp.
   * @return String     printable representation of this ImmutableGene object
   */
  @Override
  public String toString() {
    return allelicCompStr;
  }
}

