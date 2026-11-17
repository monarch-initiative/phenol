package org.monarchinitiative.phenol.annotations.io.hpo;

class PercentageUtil {

  private PercentageUtil() {
  }

  /**
   * @param percentage value in range [0, 100]
   * @param cohortSize the
   */
  static int computeNumerator(float percentage, int cohortSize) {
    if (cohortSize <= 0)
      throw new IllegalArgumentException("cohortSize " + cohortSize + " must be positive");
    int numerator = Math.round(percentage * cohortSize / 100F);
    if (percentage >= 1.)
      // Prevent treating very rare events (1-4% of the cases) as excluded due to rounding issues.
      return Math.max(1, numerator);
    else {
      return numerator;
    }
  }
}
