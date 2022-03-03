package org.monarchinitiative.phenol.annotations.base.temporal;

public interface Age {

  Age ZERO = Age.of(Timestamp.ZERO);

  static Age of(Timestamp timestamp) {
    return of(timestamp, ConfidenceInterval.precise());
  }

  static Age of(Timestamp timestamp, ConfidenceInterval confidenceInterval) {
    return AgeDefault.of(timestamp, confidenceInterval);
  }

  Timestamp age();

  /**
   * @return confidence interval relative to the {@link #age()} timestamp
   * (<em>not</em> with respect to start of the time-line!)
   */
  ConfidenceInterval confidenceInterval();


}
