package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class AgeGestational implements Age {

  public static final Age START = new AgeOpen(Integer.MIN_VALUE, true);
  static final Age LMP = new AgeGestational(0);
  private final int days;

  static Age of(int days) {
    return days == 0 ? LMP : new AgeGestational(days);
  }

  private AgeGestational(int days) {
    this.days = days;
  }

  @Override
  public int days() {
    return days;
  }

  @Override
  public boolean isGestational() {
    return true;
  }

  @Override
  public boolean isOpen() {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AgeGestational ageDays = (AgeGestational) o;
    return days == ageDays.days;
  }

  @Override
  public int hashCode() {
    return Objects.hash(days, true);
  }

  @Override
  public String toString() {
    return "AgeGestational{" +
      "days=" + days +
      '}';
  }
}
