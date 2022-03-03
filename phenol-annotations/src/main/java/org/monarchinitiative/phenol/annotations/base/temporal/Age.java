package org.monarchinitiative.phenol.annotations.base.temporal;

public interface Age {

  Age ZERO = Age.of(Timestamp.ZERO);

  static Age of(Timestamp timestamp) {
    return of(timestamp, ConfidenceInterval.precise());
  }

  static Age of(Timestamp timestamp, ConfidenceInterval confidenceInterval) {
    return AgeDefault.of(timestamp, confidenceInterval);
  }

  Timestamp timestamp();

  /**
   * @return confidence interval relative to the {@link #timestamp()} timestamp
   * (<em>not</em> with respect to start of the time-line!)
   */
  ConfidenceInterval confidenceInterval();

  /* **************************************************************************************************************** */

  default boolean isPrecise() {
    return confidenceInterval().isPrecise();
  }

  static int compare(Age x, Age y) {
    int result = Timestamp.compare(x.timestamp(), y.timestamp());
    if (result != 0)
      return result;

    return ConfidenceInterval.compare(x.confidenceInterval(), y.confidenceInterval());
  }
}
