package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * Open age since birth.
 */
class AgeOpen implements Age {

  private final int days;
  private final boolean isPrenatal;

  AgeOpen(int days, boolean isPrenatal) {
    this.days = days;
    this.isPrenatal = isPrenatal;
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
  public boolean isGestational() {
    return isPrenatal;
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
    return days == ageOpen.days && isPrenatal == ageOpen.isPrenatal;
  }

  @Override
  public int hashCode() {
    return Objects.hash(days, isPrenatal);
  }

  @Override
  public String toString() {
    return "AgeOpen{" +
      "days=" + days +
      ", isPrenatal=" + isPrenatal +
      '}';
  }
}
