package org.monarchinitiative.phenol.annotations.base;

import java.time.Period;
import java.util.Objects;
import java.util.Optional;

class AgePrecise implements Age, AgeRange {

  private final Period age;

  AgePrecise(Period age) {
    this.age = age;
  }

  @Override
  public Optional<Period> age() {
    return Optional.of(age);
  }

  @Override
  public boolean isPrecise() {
    return true;
  }

  @Override
  public Period lowerBound() {
    return age;
  }

  @Override
  public Period upperBound() {
    return age;
  }

  @Override
  public Age start() {
    return this;
  }

  @Override
  public Age end() {
    return this;
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
