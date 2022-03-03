package org.monarchinitiative.phenol.annotations.base.temporal;

/**
 * Timestamp represents duration since/until the start of the time-line. Start of the timeline is represented by {@link Timestamp#ZERO}
 */
public interface Timestamp {

  /**
   * Number of seconds in {@link java.time.temporal.JulianFields#JULIAN_DAY} (86,400 seconds).
   */
  int SECONDS_IN_DAY = 24 * 60 * 60;

  /**
   * To prevent numerical overflow, we do not allow creation of a {@link Timestamp} corresponding to more
   * than 2,000 Julian years (730,500 days). It should be enough for our use case of modeling life-span of organisms.
   */
  int MAX_DAYS = 20 * 36_525;

  /**
   * Duration of 0 days and 0 seconds.
   */
  Timestamp ZERO = TimestampDefault.of(0, 0);

  /**
   * Create {@link Timestamp} representing given number of days.
   *
   * @param days    number of days
   * @return timestamp
   * @throws ArithmeticException if the number of days ends up being more than {@link #MAX_DAYS} after the normalization
   */
  static Timestamp of(int days) {
    return of(days, 0);
  }

  /**
   * Create {@link Timestamp} representing given number of days and seconds.
   *
   * @param days    number of days
   * @param seconds number of seconds
   * @return timestamp
   * @throws ArithmeticException if the number of days ends up being more than {@link #MAX_DAYS} after the normalization
   */
  static Timestamp of(int days, int seconds) {
    if (days == Integer.MAX_VALUE)
      throw new IllegalArgumentException("Integer MAX_VALUE is reserved for open end timestamp");
    else if (days == Integer.MIN_VALUE)
      throw new IllegalArgumentException("Integer MIN_VALUE is reserved for open end timestamp");
    else if (days == 0 && seconds == 0)
      return ZERO;
    else
      return TimestampDefault.of(days, seconds);
  }

  static Timestamp openStart() {
    return TimestampDefault.START;
  }

  static Timestamp openEnd() {
    return TimestampDefault.END;
  }

  /* **************************************************************************************************************** */

  /**
   * @return number of days since start of the time-line. The number can be negative if the {@link Timestamp} represents
   * an event occurring before start of the time-line.
   */
  int days();

  /**
   * @return number of outstanding seconds. The number must be non-positive if the {@link Timestamp} represents
   * an event before start of the time-line.
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


  static int compare(Timestamp x, Timestamp y) {
    int result = Integer.compare(x.days(), y.days());
    if (result != 0)
      return result;

    result = Integer.compare(x.seconds(), y.seconds());
    if (result != 0)
      return result;

    return Boolean.compare(x.isOpen(), y.isOpen());
  }
}