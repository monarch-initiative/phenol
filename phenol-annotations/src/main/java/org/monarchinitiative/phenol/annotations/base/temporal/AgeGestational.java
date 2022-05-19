package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class AgeGestational {

  public static final Age START = new AgeOpen(Integer.MIN_VALUE, true);
  static final Age LMP = new AgeGestationalPrecise(0);

  static Age of(int days, ConfidenceInterval ci) {
    if (ci.isPrecise())
      return days == 0 ? LMP : new AgeGestationalPrecise(days);
    return new AgeGestationalImprecise(days, ci);
  }

  private static abstract class AgeGestationalBase implements Age {
    protected final int days;

    protected AgeGestationalBase(int days) {
      this.days = days;
    }

    @Override
    public int days() {
      return days;
    }

    @Override
    public boolean isGestational() {
      return true;
    }

    @Override
    public boolean isOpen() {
      return false;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AgeGestationalBase ageDays = (AgeGestationalBase) o;
      return days == ageDays.days;
    }

    @Override
    public int hashCode() {
      return Objects.hash(days);
    }

  }

  private static class AgeGestationalPrecise extends AgeGestationalBase {

    private AgeGestationalPrecise(int days) {
      super(days);
    }

    @Override
    public TemporalPoint start() {
      return TemporalPoint.of(days, isGestational());
    }

    @Override
    public TemporalPoint end() {
      return TemporalPoint.of(days, isGestational());
    }

    @Override
    public ConfidenceInterval confidenceInterval() {
      return ConfidenceInterval.precise();
    }

    @Override
    public String toString() {
      return "AgeGestationalPrecise{" +
        "days=" + days +
        '}';
    }
  }

  private static class AgeGestationalImprecise extends AgeGestationalBase {

    private final ConfidenceInterval ci;

    private AgeGestationalImprecise(int days, ConfidenceInterval ci) {
      super(days);
      this.ci = ci;
    }

    @Override
    public TemporalPoint start() {
      return TemporalPoint.of(days + ci.lowerBound(), isGestational());
    }

    @Override
    public TemporalPoint end() {
      return TemporalPoint.of(days + ci.upperBound(), isGestational());
    }

    @Override
    public ConfidenceInterval confidenceInterval() {
      return ci;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      AgeGestationalImprecise that = (AgeGestationalImprecise) o;
      return Objects.equals(ci, that.ci);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), ci);
    }

    @Override
    public String toString() {
      return "AgeGestationalImprecise{" +
        "days=" + days +
        "ci=" + ci +
        "}";
    }
  }

}
