package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;
import java.util.Objects;
import java.util.Optional;

public class AgeImprecise implements Age {

  private final Period lower, upper;

  AgeImprecise(Period lower, Period upper) {
    this.lower = lower;
    this.upper = upper;
  }

  @Override
  public Optional<Period> age() {
    return Optional.empty();
  }

  @Override
  public boolean isPrecise() {
    return false;
  }

  @Override
  public Period lowerBound() {
    return lower;
  }

  @Override
  public Period upperBound() {
    return upper;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AgeImprecise that = (AgeImprecise) o;
    return Objects.equals(lower, that.lower) && Objects.equals(upper, that.upper);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lower, upper);
  }

  @Override
  public String toString() {
    return "AgeImprecise{" +
      "lower=" + lower +
      ", upper=" + upper +
      '}';
  }
}
