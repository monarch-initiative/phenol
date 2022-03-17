package org.monarchinitiative.phenol.annotations.base.temporal;

/**
 * {@link AgeSinceBirth} represents duration since/until the start of the time-line.
 * Start of the timeline is represented by {@link AgeSinceBirth#zero()}.
 */
public interface AgeSinceBirth {

  double DAYS_IN_JULIAN_YEAR = 365.25;

  double DAYS_IN_MONTH = DAYS_IN_JULIAN_YEAR / 12;

  /**
   * Number of seconds in {@link java.time.temporal.JulianFields#JULIAN_DAY} (86,400 seconds).
   */
  int SECONDS_IN_DAY = 24 * 60 * 60;

  /**
   * To prevent numerical overflow, we do not allow creation of a {@link AgeSinceBirth} corresponding to more
   * than 2,000 Julian years (730,500 days). It should be enough for our use case of modeling life-span of organisms.
   */
  int MAX_DAYS = 20 * 36_525;

  /**
   * Duration of 0 days and 0 seconds.
   */
  static AgeSinceBirth zero() {
    return AgeSinceBirthDefault.ZERO;
  }

  static AgeSinceBirth of(int years, int months, int days) {
    days += convertYearsAndMonthsToDays(years, months);
    return of(days);
  }

  private static int convertYearsAndMonthsToDays(int years, int months) {
    return Math.toIntExact(Math.round(years * DAYS_IN_JULIAN_YEAR + months * DAYS_IN_MONTH));
  }

  /**
   * Create {@link AgeSinceBirth} representing given number of days.
   *
   * @param days    number of days
   * @return timestamp
   * @throws ArithmeticException if the number of days ends up being more than {@link #MAX_DAYS} after the normalization
   */
  static AgeSinceBirth of(int days) {
    return of(days, 0);
  }

  /**
   * Create {@link AgeSinceBirth} representing given number of days and seconds.
   *
   * @param days    number of days
   * @param seconds number of seconds
   * @return timestamp
   * @throws ArithmeticException if the number of days ends up being more than {@link #MAX_DAYS} after the normalization
   */
  static AgeSinceBirth of(int days, int seconds) {
    if (days == Integer.MAX_VALUE)
      throw new IllegalArgumentException("Integer MAX_VALUE is reserved for open end age");
    else if (days == Integer.MIN_VALUE)
      throw new IllegalArgumentException("Integer MIN_VALUE is reserved for open end age");
    else if ((days > 0 && seconds < 0) || (days < 0 && seconds > 0))
      throw new IllegalArgumentException("Days and seconds must have the same sign");
    else if (days == 0 && seconds == 0)
      return zero();
    else
      return AgeSinceBirthDefault.of(days, seconds);
  }

  static AgeSinceBirth openStart() {
    return AgeSinceBirthDefault.START;
  }

  static AgeSinceBirth openEnd() {
    return AgeSinceBirthDefault.END;
  }

  /* **************************************************************************************************************** */

  /**
   * @return number of days since start of the time-line (e.g. birth for Humans).
   * The number can be negative if the {@link AgeSinceBirth} represents an event occurring before start of the time-line.
   */
  int days();

  /**
   * @return number of outstanding seconds. The number must be <em>non-positive</em> if the {@link AgeSinceBirth} represents
   * an event before the start of the time-line. The value ranges from -{@link #SECONDS_IN_DAY} to {@link #SECONDS_IN_DAY}.
   */
  int seconds();


  boolean isOpen();

  /* **************************************************************************************************************** */

  default boolean isClosed() {
    return !isOpen();
  }

  default boolean isZero() {
    return days() == 0 && seconds() == 0;
  }

  default boolean isNegative() {
    return days() < 0 && seconds() <= 0;
  }

  default boolean isPositive() {
    return days() > 0 && seconds() >= 0;
  }

  default AgeSinceBirth plus(AgeSinceBirth other) {
    return AgeSinceBirth.of(days() + other.days(), seconds() + other.seconds());
  }

  default AgeSinceBirth negated() {
    return AgeSinceBirth.of(-days(), -seconds());
  }

  /* **************************************************************************************************************** */

  static AgeSinceBirth max(AgeSinceBirth a, AgeSinceBirth b) {
    int compare = AgeSinceBirth.compare(a, b);
    return compare >= 0 ? a : b;
  }

  static AgeSinceBirth min(AgeSinceBirth a, AgeSinceBirth b) {
    int compare = AgeSinceBirth.compare(a, b);
    return compare <= 0 ? a : b;
  }

  /* **************************************************************************************************************** */

  static int compare(AgeSinceBirth x, AgeSinceBirth y) {
    int result = Integer.compare(x.days(), y.days());
    if (result != 0)
      return result;

    return Integer.compare(x.seconds(), y.seconds());
  }
}
