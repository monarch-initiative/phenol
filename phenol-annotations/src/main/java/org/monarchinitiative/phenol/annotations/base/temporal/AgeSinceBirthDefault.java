package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class AgeSinceBirthDefault implements AgeSinceBirth {

  static final AgeSinceBirthOpen START = new AgeSinceBirthOpen(Integer.MIN_VALUE);
  static final AgeSinceBirthOpen END = new AgeSinceBirthOpen(Integer.MAX_VALUE);
  static final AgeSinceBirthZero ZERO = new AgeSinceBirthZero();

  private final int days, seconds;

  static AgeSinceBirth of(int days, int seconds) {
    if (Math.abs(seconds) >= SECONDS_IN_DAY) {
      DaysSecondConstant constant = normalizeOutstandingSeconds(seconds);
      days += constant.days;
      seconds += constant.seconds;
    }

    return seconds == 0
      ? new AgeSinceBirthDays(days)
      : new AgeSinceBirthDefault(days, seconds);
  }

  private static DaysSecondConstant normalizeOutstandingSeconds(int seconds) {
    int d = seconds / SECONDS_IN_DAY;
    int s = -d * SECONDS_IN_DAY;
    return new DaysSecondConstant(d, s);
  }

  private AgeSinceBirthDefault(int days, int seconds) {
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
    AgeSinceBirthDefault that = (AgeSinceBirthDefault) o;
    return days == that.days && seconds == that.seconds;
  }

  @Override
  public int hashCode() {
    return Objects.hash(days, seconds);
  }

  @Override
  public String toString() {
    return "DefaultAgeSinceBirth{" +
      "days=" + days +
      ", seconds=" + seconds +
      '}';
  }

  /**
   * Open age since birth.
   */
  private static class AgeSinceBirthOpen implements AgeSinceBirth {

    private final int days;
    private AgeSinceBirthOpen(int days) {
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
      AgeSinceBirthOpen that = (AgeSinceBirthOpen) o;
      return days == that.days;
    }

    @Override
    public int hashCode() {
      return Objects.hash(days);
    }

    @Override
    public String toString() {
      return this.equals(START) ? "OpenStartAgeSinceBirth" : "OpenEndAgeSinceBirth";
    }
  }

  private static class AgeSinceBirthZero implements AgeSinceBirth {

    private AgeSinceBirthZero() {
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
      return "AgeSinceBirthZero";
    }
  }

  /**
   * A {@link AgeSinceBirth} implementation that only stores days (no seconds).
   */
  private static class AgeSinceBirthDays implements AgeSinceBirth {

    private final int days;

    private AgeSinceBirthDays(int days) {
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
      return false;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AgeSinceBirthDays that = (AgeSinceBirthDays) o;
      return days == that.days;
    }

    @Override
    public int hashCode() {
      return Objects.hash(days);
    }

    @Override
    public String toString() {
      return "AgeSinceBirthDays{" +
        "days=" + days +
        '}';
    }
  }

  private static class DaysSecondConstant {
    private final int days;
    private final int seconds;

    private DaysSecondConstant(int days, int seconds) {
      this.days = days;
      this.seconds = seconds;
    }
  }
}
