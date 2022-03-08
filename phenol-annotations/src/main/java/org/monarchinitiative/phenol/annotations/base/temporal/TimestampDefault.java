package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class TimestampDefault implements Timestamp {

  static final TimestampOpen START = new TimestampOpen(Integer.MIN_VALUE);
  static final TimestampOpen END = new TimestampOpen(Integer.MAX_VALUE);
  static final TimestampZero ZERO = new TimestampZero();

  private final int days, seconds;

  static TimestampDefault of(int days, int seconds) {
    if (Math.abs(seconds) >= SECONDS_IN_DAY)
      return normalize(days, seconds);
    return new TimestampDefault(days, seconds);
  }

  private static TimestampDefault normalize(int days, int seconds) {
    int leapDays = seconds / SECONDS_IN_DAY;
    days = days + leapDays;
    seconds = seconds - leapDays * SECONDS_IN_DAY;
    return new TimestampDefault(days, seconds);
  }

  private TimestampDefault(int days, int seconds) {
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
  public boolean isOpen() {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TimestampDefault that = (TimestampDefault) o;
    return days == that.days && seconds == that.seconds;
  }

  @Override
  public int hashCode() {
    return Objects.hash(days, seconds);
  }

  @Override
  public String toString() {
    return "DefaultTimestamp{" +
      "days=" + days +
      ", seconds=" + seconds +
      '}';
  }

  /**
   * Open timestamp
   */
  private static class TimestampOpen implements Timestamp {

    private final int days;
    private TimestampOpen(int days) {
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
    public boolean isOpen() {
      return true;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TimestampOpen that = (TimestampOpen) o;
      return days == that.days;
    }

    @Override
    public int hashCode() {
      return Objects.hash(days);
    }

    @Override
    public String toString() {
      return this.equals(START) ? "OpenStartTimestamp" : "OpenEndTimestamp";
    }
  }

  private static class TimestampZero implements Timestamp {

    private TimestampZero() {
    }

    @Override
    public int days() {
      return 0;
    }

    @Override
    public int seconds() {
      return 0;
    }

    @Override
    public boolean isOpen() {
      return false;
    }

    @Override
    public String toString() {
      return "TimestampZero";
    }
  }
}
