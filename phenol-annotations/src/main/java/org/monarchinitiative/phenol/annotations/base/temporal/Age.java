package org.monarchinitiative.phenol.annotations.base.temporal;

/**
 * {@link Age} represents duration since the start of the time-line.
 * Start of the timeline is represented by the {@link Age#lastMenstrualPeriod()} for prenatal events
 * and by {@link Age#birth()} for postnatal events.
 */
public interface Age {

  double DAYS_IN_JULIAN_YEAR = 365.25;

  double DAYS_IN_MONTH = DAYS_IN_JULIAN_YEAR / 12;

  /**
   * To prevent numerical overflow, we do not allow creation of a {@link Age} corresponding to more
   * than 2,000 Julian years (730,500 days). It should be enough for our use case of modeling life-span of organisms.
   */
  int MAX_DAYS = 20 * 36_525;

  /**
   * @return age representing the last menstrual period before the conception.
   */
  static Age lastMenstrualPeriod() {
    return AgeGestational.LMP;
  }

  /**
   * @return age representing birth.
   */
  static Age birth() {
    return AgePostnatal.BIRTH;
  }

  static Age gestational(int weeks, int days) {
    days += weeks * 7;
    return of(days, true);
  }

  static Age postnatal(int years, int months, int days) {
    days += Math.toIntExact(Math.round(convertYearsAndMonthsToDays(years, months)));
    return postnatal(days);
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
    return of(days, false);
  }

  /**
   * Create {@link Age} representing given number of days and seconds.
   *
   * @param days    number of days
   * @param gestational true if the age should be gestational, false if the age should be postnatal
   * @throws ArithmeticException if the number of days ends up being more than {@link #MAX_DAYS} after the normalization
   */
  static Age of(int days, boolean gestational) {
    if (days == Integer.MAX_VALUE)
      throw new IllegalArgumentException("Integer MAX_VALUE is reserved for open end age");
    else if (days < 0)
      throw new IllegalArgumentException("Days must not be negative, got '" + days + "' days!");

    if (days > MAX_DAYS)
      throw new ArithmeticException("Normalized number of days must not be greater than '" + MAX_DAYS + "'. Got '" + days + '\'');

    return gestational
      ? AgeGestational.of(days)
      : AgePostnatal.of(days);
  }

  /**
   * @return age that represents event that occurred in unspecified distant past.
   */
  static Age openStart() {
    return AgeGestational.START;
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
   * @return true if the {@link Age} represents the time passed since conception.
   */
  boolean isGestational();

  boolean isOpen();

  /* **************************************************************************************************************** */

  default boolean isClosed() {
    return !isOpen();
  }

  default boolean isZero() {
    return days() == 0;
  }

  default boolean isPositive() {
    return days() > 0;
  }

  default Age plus(Age other) {
    int days = days() + other.days();
    return Age.of(days, isGestational());
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
    if (x.isGestational() ^ y.isGestational())
      return x.isGestational() ? -1 : 1;


    return Integer.compare(x.days(), y.days());
  }
}
