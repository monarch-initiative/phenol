package org.monarchinitiative.phenol.annotations.base;

import java.util.Objects;

/**
 * Ratio to represent a proportion in range <code>[0, 1]</code>.
 */
public class Ratio {

  private final int numerator;
  private final int denominator;

  public static Ratio combine(Ratio left, Ratio right) {
    return Ratio.of(left.numerator + right.numerator, left.denominator + right.denominator);
  }

  /**
   * @param numerator   non-negative numerator
   * @param denominator positive denominator
   * @throws IllegalArgumentException if the numerator or denominator do not meet the above requirements,
   *                                  or if the numerator is greater than denominator
   */
  public static Ratio of(int numerator, int denominator) {
    if (numerator < 0)
      throw new IllegalArgumentException("Numerator must be non-negative");
    if (denominator <= 0)
      throw new IllegalArgumentException("Denominator must be positive");

    if (numerator > denominator)
      throw new IllegalArgumentException("Numerator " + numerator + " must be less than or equal to denominator " + denominator);

    return new Ratio(numerator, denominator);
  }

  private Ratio(int numerator, int denominator) {
    this.numerator = numerator;
    this.denominator = denominator;
  }

  public int numerator() {
    return numerator;
  }

  public int denominator() {
    return denominator;
  }

  public float frequency() {
    return ((float) numerator) / denominator;
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
