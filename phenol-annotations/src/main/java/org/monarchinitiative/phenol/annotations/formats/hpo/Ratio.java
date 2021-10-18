package org.monarchinitiative.phenol.annotations.formats.hpo;

import java.util.Objects;

public class Ratio {

  private final int numerator;
  private final int denominator;

  private Ratio(int numerator, int denominator) {
    this.numerator = numerator;
    this.denominator = denominator;
  }

  public static Ratio of(int numerator, int denominator) {
    if (numerator < 0)
      throw new IllegalArgumentException("Numerator must be non-negative");
    if (denominator <= 0)
      throw new IllegalArgumentException("Denominator must be positive");

    if (numerator > denominator)
      throw new IllegalArgumentException("Numerator " + numerator + " must be less than or equal to denominator " + denominator);

    return new Ratio(numerator, denominator);
  }

  public int numerator() {
    return numerator;
  }

  public int denominator() {
    return denominator;
  }

  public double frequency() {
    return ((double) numerator) / denominator;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Ratio ratio = (Ratio) o;
    return numerator == ratio.numerator && denominator == ratio.denominator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(numerator, denominator);
  }

  @Override
  public String toString() {
    return "Ratio{" +
      "numerator=" + numerator +
      ", denominator=" + denominator +
      '}';
  }
}
