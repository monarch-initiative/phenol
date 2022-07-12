package org.monarchinitiative.phenol.annotations.base;

import java.util.Objects;

/**
 * Default implementation of {@link Ratio} that stores <code>numerator</code> and <code>denominator</code>
 * as signed integers. What a waste of space!
 */
class RatioDefault implements Ratio {

  private final int numerator;
  private final int denominator;

  RatioDefault(int numerator, int denominator) {
    this.numerator = numerator;
    this.denominator = denominator;
  }

  @Override
  public int numerator() {
    return numerator;
  }

  @Override
  public int denominator() {
    return denominator;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RatioDefault ratio = (RatioDefault) o;
    return numerator == ratio.numerator && denominator == ratio.denominator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(numerator, denominator);
  }

  @Override
  public String toString() {
    return "RatioDefault{" +
      "numerator=" + numerator +
      ", denominator=" + denominator +
      '}';
  }

}
