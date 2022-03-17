package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * @deprecated due to {@link Age} being deprecated.
 */
@Deprecated
class AgeDefault {

  static final Age BIRTH = new PreciseAge(AgeSinceBirth.zero());

  static Age of(AgeSinceBirth ageSinceBirth, ConfidenceInterval confidenceInterval) {
    if (confidenceInterval.isPrecise()) {
      return ageSinceBirth.isZero()
        ? BIRTH
        : new PreciseAge(ageSinceBirth);
    } else {
      return new ImpreciseAge(ageSinceBirth, confidenceInterval);
    }
  }

  private AgeDefault() {
  }

  private static class PreciseAge implements Age {

    private final AgeSinceBirth ageSinceBirth;

    private PreciseAge(AgeSinceBirth ageSinceBirth) {
      this.ageSinceBirth = ageSinceBirth;
    }

    @Override
    public AgeSinceBirth timestamp() {
      return ageSinceBirth;
    }

    @Override
    public ConfidenceInterval confidenceInterval() {
      return ConfidenceInterval.precise();
    }

    @Override
    public TemporalInterval asTemporalInterval() {
      return TemporalInterval.of(ageSinceBirth, ageSinceBirth);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      PreciseAge that = (PreciseAge) o;
      return Objects.equals(ageSinceBirth, that.ageSinceBirth);
    }

    @Override
    public int hashCode() {
      return Objects.hash(ageSinceBirth);
    }

    @Override
    public String toString() {
      return "PreciseAge{" +
        "timestamp=" + ageSinceBirth +
        '}';
    }
  }

  private static class ImpreciseAge implements Age {

    private final AgeSinceBirth ageSinceBirth;
    private final ConfidenceInterval confidenceInterval;

    private ImpreciseAge(AgeSinceBirth ageSinceBirth, ConfidenceInterval confidenceInterval) {
      this.ageSinceBirth = ageSinceBirth;
      this.confidenceInterval = confidenceInterval;
    }

    @Override
    public AgeSinceBirth timestamp() {
      return ageSinceBirth;
    }

    @Override
    public ConfidenceInterval confidenceInterval() {
      return confidenceInterval;
    }

    @Override
    public TemporalInterval asTemporalInterval() {
      return TemporalInterval.of(ageSinceBirth.plus(confidenceInterval.lowerBound()), ageSinceBirth.plus(confidenceInterval.upperBound()));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ImpreciseAge that = (ImpreciseAge) o;
      return Objects.equals(ageSinceBirth, that.ageSinceBirth) && Objects.equals(confidenceInterval, that.confidenceInterval);
    }

    @Override
    public int hashCode() {
      return Objects.hash(ageSinceBirth, confidenceInterval);
    }

    @Override
    public String toString() {
      return "ImpreciseAge{" +
        "timestamp=" + ageSinceBirth +
        ", confidenceInterval=" + confidenceInterval +
        '}';
    }
  }

}
