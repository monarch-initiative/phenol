package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class DaysSecondConstant {
  private final int days;
  private final int seconds;

  DaysSecondConstant(int days, int seconds) {
    this.days = days;
    this.seconds = seconds;
  }

  public int days() {
    return days;
  }

  public int seconds() {
    return seconds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DaysSecondConstant that = (DaysSecondConstant) o;
    return days == that.days && seconds == that.seconds;
  }

  @Override
  public int hashCode() {
    return Objects.hash(days, seconds);
  }
}
