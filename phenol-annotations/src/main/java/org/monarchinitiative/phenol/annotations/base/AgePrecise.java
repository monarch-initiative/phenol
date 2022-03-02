package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;
import java.util.Objects;

class AgePrecise implements Age {

  private final Period age;

  AgePrecise(Period age) {
    this.age = age;
  }


  @Override
  public Period age() {
    return age;
  }

  @Override
  public AgeConfidenceInterval confidenceInterval() {
    return AgeConfidenceInterval.precise();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AgePrecise that = (AgePrecise) o;
    return Objects.equals(age, that.age);
  }

  @Override
  public int hashCode() {
    return Objects.hash(age);
  }

  @Override
  public String toString() {
    return "PreciseAge{" +
      "age=" + age +
      '}';
  }

}
