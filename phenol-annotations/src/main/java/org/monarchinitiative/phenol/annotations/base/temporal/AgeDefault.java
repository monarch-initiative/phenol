package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * @deprecated due to {@link Age} being deprecated.
 */
@Deprecated
class AgeDefault {

  static final Age BIRTH = new PreciseAge(Timestamp.zero());

  static Age of(Timestamp timestamp, ConfidenceInterval confidenceInterval) {
    if (confidenceInterval.isPrecise()) {
      return timestamp.isZero()
        ? BIRTH
        : new PreciseAge(timestamp);
    } else {
      return new ImpreciseAge(timestamp, confidenceInterval);
    }
  }

  private AgeDefault() {
  }

  private static class PreciseAge implements Age {

    private final Timestamp timestamp;

    private PreciseAge(Timestamp timestamp) {
      this.timestamp = timestamp;
    }

    @Override
    public Timestamp timestamp() {
      return timestamp;
    }

    @Override
    public ConfidenceInterval confidenceInterval() {
      return ConfidenceInterval.precise();
    }

    @Override
    public TemporalInterval asTemporalInterval() {
      return TemporalInterval.of(timestamp, timestamp);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      PreciseAge that = (PreciseAge) o;
      return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
      return Objects.hash(timestamp);
    }

    @Override
    public String toString() {
      return "PreciseAge{" +
        "timestamp=" + timestamp +
        '}';
    }
  }

  private static class ImpreciseAge implements Age {

    private final Timestamp timestamp;
    private final ConfidenceInterval confidenceInterval;

    private ImpreciseAge(Timestamp timestamp, ConfidenceInterval confidenceInterval) {
      this.timestamp = timestamp;
      this.confidenceInterval = confidenceInterval;
    }

    @Override
    public Timestamp timestamp() {
      return timestamp;
    }

    @Override
    public ConfidenceInterval confidenceInterval() {
      return confidenceInterval;
    }

    @Override
    public TemporalInterval asTemporalInterval() {
      return TemporalInterval.of(timestamp.plus(confidenceInterval.lowerBound()), timestamp.plus(confidenceInterval.upperBound()));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ImpreciseAge that = (ImpreciseAge) o;
      return Objects.equals(timestamp, that.timestamp) && Objects.equals(confidenceInterval, that.confidenceInterval);
    }

    @Override
    public int hashCode() {
      return Objects.hash(timestamp, confidenceInterval);
    }

    @Override
    public String toString() {
      return "ImpreciseAge{" +
        "timestamp=" + timestamp +
        ", confidenceInterval=" + confidenceInterval +
        '}';
    }
  }

}
