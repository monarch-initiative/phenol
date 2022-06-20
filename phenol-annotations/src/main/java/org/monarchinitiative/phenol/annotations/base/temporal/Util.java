package org.monarchinitiative.phenol.annotations.base.temporal;

class Util {

  private Util() {
  }

  /**
   * Check that <code>days</code> is not infinite, NaN, or negative.
   *
   * @param days number of days to check.
   */
  static void checkDays(float days) {
    if (Float.isInfinite(days))
      throw new IllegalArgumentException("Infinite value is reserved for open end age");
    else if (Float.isNaN(days))
      throw new IllegalArgumentException("NaN values are not allowed");
    else if (days < 0)
      throw new IllegalArgumentException("Days must not be negative, got '" + days + "' days!");
  }

}
