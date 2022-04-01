package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class AgePostnatal implements Age {

  static final AgeOpen END = new AgeOpen(Integer.MAX_VALUE, false);
  static final AgeDays BIRTH = new AgeDays(0);

  private final int days, seconds;

  static Age of(int days, int seconds) {
    return seconds == 0
      ? days == 0 ? BIRTH : new AgeDays(days)
      : new AgePostnatal(days, seconds);
  }

  private AgePostnatal(int days, int seconds) {
    if (days > MAX_DAYS)
      throw new ArithmeticException(String.format("Number of days %d must not be greater than %d", days, MAX_DAYS));
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
  public boolean isPrenatal() {
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
    return days == that.days && seconds == that.seconds;
  }

  @Override
  public int hashCode() {
    return Objects.hash(days, seconds, true);
  }

  @Override
  public String toString() {
    return "DefaultAgeSinceBirth{" +
      "days=" + days +
      ", seconds=" + seconds +
      '}';
  }

  /**
   * A {@link Age} implementation that only stores days (no seconds).
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
    public boolean isPrenatal() {
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
      AgeDays that = (AgeDays) o;
      return days == that.days;
    }

    @Override
    public int hashCode() {
      return Objects.hash(days, false);
    }

    @Override
    public String toString() {
      return "AgeSinceBirthDays{" +
        "days=" + days +
        '}';
    }
  }

}
