package org.monarchinitiative.phenol.annotations.base.temporal;

/**
 * {@link Age} represents duration since the start of the time-line.
 * Start of the timeline is represented by {@link Age#conception()} for prenatal events and
 * by {@link Age#birth()} for postnatal events.
 */
public interface Age {

  double DAYS_IN_JULIAN_YEAR = 365.25;

  double DAYS_IN_MONTH = DAYS_IN_JULIAN_YEAR / 12;

  /**
   * Number of seconds in {@link java.time.temporal.JulianFields#JULIAN_DAY} (86,400 seconds).
   */
  int SECONDS_IN_DAY = 24 * 60 * 60;

  /**
   * To prevent numerical overflow, we do not allow creation of a {@link Age} corresponding to more
   * than 2,000 Julian years (730,500 days). It should be enough for our use case of modeling life-span of organisms.
   */
  int MAX_DAYS = 20 * 36_525;

  static Age birth() {
    return AgePostnatal.BIRTH;
  }

  static Age conception() {
    return AgePrenatal.CONCEPTION;
  }

  static Age prenatal(int weeks, int days) {
    days += weeks * 7;
    return of(days, 0, true);
  }

  static Age postnatal(int years, int months, int days) {
    days += Math.toIntExact(Math.round(convertYearsAndMonthsToDays(years, months)));
    return postnatal(days, 0);
  }

  /**
   * The preferred way for converting <code>years</code> and <code>months</code> to days.
   * @return number of days that occur in given <code>years</code> and <code>months</code>
   */
  static double convertYearsAndMonthsToDays(int years, int months) {
    return years * DAYS_IN_JULIAN_YEAR + months * DAYS_IN_MONTH;
  }

  /**
   * Create {@link Age} representing a number of days since birth.
   * @param days number of days
   */
  static Age postnatal(int days) {
    return postnatal(days, 0);
  }

  /**
   * Create {@link Age} representing a number of days and seconds since birth.
   * @param days number of days
   * @param seconds number of seconds
   */
  static Age postnatal(int days, int seconds) {
    return of(days, seconds, false);
  }

  /**
   * Create {@link Age} representing given number of days and seconds.
   *
   * @param days    number of days
   * @param seconds number of seconds
   * @throws ArithmeticException if the number of days ends up being more than {@link #MAX_DAYS} after the normalization
   */
  static Age of(int days, int seconds, boolean gestational) {
    if (days == Integer.MAX_VALUE)
      throw new IllegalArgumentException("Integer MAX_VALUE is reserved for open end age");
    else if (days < 0 || seconds < 0)
      throw new IllegalArgumentException("Days and seconds must not be negative, got '" + days + "' days and '" + seconds + "' seconds");

    // Ensure we don't have more than 86,400 seconds, which is greater than the number of seconds in one day.
    if (seconds >= SECONDS_IN_DAY) {
      DaysSecondConstant constant = normalizeOutstandingSeconds(seconds);
      days += constant.days();
      seconds += constant.seconds();
    }
    if (days > MAX_DAYS)
      throw new ArithmeticException("Normalized number of days must not be greater than '" + MAX_DAYS + "'. Got '" + days + '\'');

    return gestational
      ? AgePrenatal.of(days, seconds)
      : AgePostnatal.of(days, seconds);
  }

  private static DaysSecondConstant normalizeOutstandingSeconds(int seconds) {
    int d = seconds / SECONDS_IN_DAY;
    int s = -d * SECONDS_IN_DAY;
    return new DaysSecondConstant(d, s);
  }

  /**
   * @return age that represents event that occurred in unspecified distant past.
   */
  static Age openStart() {
    return AgePrenatal.START;
  }

  /**
   * @return age that represents event that occurrs in unspecified distant future.
   */
  static Age openEnd() {
    return AgePostnatal.END;
  }

  /* **************************************************************************************************************** */

  /**
   * @return number of days since start of the time-line (e.g. birth for Humans).
   * The number can be negative if the {@link Age} represents an event occurring before start of the time-line.
   */
  int days();

  /**
   * @return number of outstanding seconds. The number must be <em>non-positive</em> if the {@link Age} represents
   * an event before the start of the time-line. The value ranges from -{@link #SECONDS_IN_DAY} to {@link #SECONDS_IN_DAY}.
   */
  int seconds();

  /**
   * @return true if the {@link Age} represents the time passed since conception.
   */
  boolean isPrenatal();

  boolean isOpen();

  /* **************************************************************************************************************** */

  default boolean isClosed() {
    return !isOpen();
  }

  default boolean isZero() {
    return days() == 0 && seconds() == 0;
  }

  default boolean isPositive() {
    return days() > 0 && seconds() >= 0;
  }

  default Age plus(Age other) {
    int days = days() + other.days();
    int seconds = seconds() + other.seconds();
    return Age.of(days, seconds, isPrenatal());
  }

  /* **************************************************************************************************************** */

  static Age max(Age a, Age b) {
    int compare = Age.compare(a, b);
    return compare >= 0 ? a : b;
  }

  static Age min(Age a, Age b) {
    int compare = Age.compare(a, b);
    return compare <= 0 ? a : b;
  }

  /* **************************************************************************************************************** */

  static int compare(Age x, Age y) {
    if (x.isPrenatal() ^ y.isPrenatal())
      return x.isPrenatal() ? -1 : 1;


    int result = Integer.compare(x.days(), y.days());
    if (result != 0)
      return result;

    return Integer.compare(x.seconds(), y.seconds());
  }
}
