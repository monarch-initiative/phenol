package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class AgePostnatal implements Age {

  static final AgeOpen END = new AgeOpen(Integer.MAX_VALUE, false);
  static final AgePostnatal BIRTH = new AgePostnatal(0);

  private final int days;

  static Age of(int days) {
    return days == 0 ? BIRTH : new AgePostnatal(days);
  }

  private AgePostnatal(int days) {
    if (days > MAX_DAYS)
      throw new ArithmeticException(String.format("Number of days %d must not be greater than %d", days, MAX_DAYS));
    this.days = days;
  }

  @Override
  public int days() {
    return days;
  }

  @Override
  public boolean isGestational() {
    return false;
  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AgePostnatal that = (AgePostnatal) o;
    return days == that.days;
  }

  @Override
  public int hashCode() {
    return Objects.hash(days, true);
  }

  @Override
  public String toString() {
    return "AgePostnatal{" +
      "days=" + days +
      '}';
  }

}
