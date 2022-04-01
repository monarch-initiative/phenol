package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * Open age since birth.
 */
class AgeOpen implements Age {

  private final int days;
  private final boolean isGestational;

  AgeOpen(int days, boolean isGestational) {
    this.days = days;
    this.isGestational = isGestational;
  }

  @Override
  public int days() {
    return days;
  }

  @Override
  public int seconds() {
    return 0;
  }

  @Override
  public boolean isPrenatal() {
    return isGestational;
  }

  @Override
  public boolean isOpen() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AgeOpen ageOpen = (AgeOpen) o;
    return days == ageOpen.days && isGestational == ageOpen.isGestational;
  }

  @Override
  public int hashCode() {
    return Objects.hash(days, isGestational);
  }

  @Override
  public String toString() {
    return "AgeOpen{" +
      "days=" + days +
      ", isGestational=" + isGestational +
      '}';
  }
}
