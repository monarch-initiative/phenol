package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class AgeGestational implements Age {

  public static final Age START = new AgeOpen(Integer.MIN_VALUE, true);
  static final Age LMP = new AgeDays(0);

  private final int days, seconds;

  static Age of(int days, int seconds) {
    return seconds == 0
      ? days == 0 ? LMP : new AgeDays(days)
      : new AgeGestational(days, seconds);
  }

  private AgeGestational(int days, int seconds) {
    this.days = days;
    this.seconds = seconds;
  }

  @Override
  public int days() {
    return days;
  }

  @Override
  public int seconds() {
    return seconds;
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
    AgeGestational that = (AgeGestational) o;
    return days == that.days && seconds == that.seconds;
  }

  @Override
  public int hashCode() {
    return Objects.hash(days, seconds, false);
  }

  @Override
  public String toString() {
    return "AgeGestational{" +
      "days=" + days +
      ", seconds=" + seconds +
      '}';
  }

  /**
   * An {@link Age} implementation that only stores days. The number of seconds is always 0.
   */
  private static class AgeDays implements Age {

    private final int days;

    private AgeDays(int days) {
      this.days = days;
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
      AgeDays ageDays = (AgeDays) o;
      return days == ageDays.days;
    }

    @Override
    public int hashCode() {
      return Objects.hash(days, true);
    }

    @Override
    public String toString() {
      return "AgeGestationalDays{" +
        "days=" + days +
        '}';
    }
  }
}
