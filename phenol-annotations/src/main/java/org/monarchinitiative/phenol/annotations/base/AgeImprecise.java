package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;
import java.util.Objects;

class AgeImprecise implements Age {

  private final Period age;
  private final AgeConfidenceInterval confidenceInterval;

  AgeImprecise(Period age, AgeConfidenceInterval confidenceInterval) {
    this.age = age;
    this.confidenceInterval = confidenceInterval;
  }

  @Override
  public Period age() {
    return age;
  }

  @Override
  public AgeConfidenceInterval confidenceInterval() {
    return confidenceInterval;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AgeImprecise that = (AgeImprecise) o;
    return Objects.equals(age, that.age) && Objects.equals(confidenceInterval, that.confidenceInterval);
  }

  @Override
  public int hashCode() {
    return Objects.hash(age, confidenceInterval);
  }

  @Override
  public String toString() {
    return "AgeImprecise{" +
      "age=" + age +
      ", confidenceInterval=" + confidenceInterval +
      '}';
  }

}
