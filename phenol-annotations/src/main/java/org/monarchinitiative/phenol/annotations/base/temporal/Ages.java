package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * Static utility class with default {@link Age} implementation.
 */
class Ages {

  static final Age START = new AgeOpen(Integer.MIN_VALUE, true);
  static final Age END = new AgeOpen(Integer.MAX_VALUE, false);

  private static final Age LMP = new AgeGestational.AgeGestationalPrecise(0);
  private static final Age BIRTH = new AgePostnatal.AgePostnatalPrecise(0);

  static Age gestational(int days, ConfidenceInterval ci) {
    if (ci.isPrecise())
      return days == 0 ? LMP : new AgeGestational.AgeGestationalPrecise(days);
    return new AgeGestational.AgeGestationalImprecise(days, ci);
  }

  static Age postnatal(int days, ConfidenceInterval ci) {
    if (ci.isPrecise())
      return days == 0 ? BIRTH : new AgePostnatal.AgePostnatalPrecise(days);

    return new AgePostnatal.AgePostnatalImprecise(days, ci);
  }

  static class AgeGestational {

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

  static class AgePostnatal {

    private static abstract class AgePostnatalBase implements Age {

      protected final int days;

      protected AgePostnatalBase(int days) {
        this.days = days;
      }

      @Override
      public int days() {
        return days;
      }

      @Override
      public boolean isGestational() {
        return false;
      }

      @Override
      public boolean isOpen() {
        return false;
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgePostnatalBase that = (AgePostnatalBase) o;
        return days == that.days;
      }

      @Override
      public int hashCode() {
        return Objects.hash(days);
      }
    }

    private static class AgePostnatalPrecise extends AgePostnatalBase {

      private AgePostnatalPrecise(int days) {
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
      public boolean equals(Object o) {
        return super.equals(o);
      }

      @Override
      public int hashCode() {
        return super.hashCode();
      }

      @Override
      public String toString() {
        return "AgePostnatalPrecise{" +
          "days=" + days +
          '}';
      }

    }

    private static class AgePostnatalImprecise extends AgePostnatalBase {

      private final ConfidenceInterval ci;

      private AgePostnatalImprecise(int days, ConfidenceInterval ci) {
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
        AgePostnatalImprecise that = (AgePostnatalImprecise) o;
        return Objects.equals(ci, that.ci);
      }

      @Override
      public int hashCode() {
        return Objects.hash(super.hashCode(), ci);
      }

      @Override
      public String toString() {
        return "AgePostnatalImprecise{" +
          "days=" + days +
          "ci=" + ci +
          "}";
      }
    }

  }

  /**
   * Open age.
   */
  static class AgeOpen implements Age {

    private final int days;
    private final boolean isGestational;

    AgeOpen(int days, boolean isGestational) {
      this.days = days;
      this.isGestational = isGestational;
    }

    @Override
    public int days() {
      return days;
    }

    @Override
    public boolean isGestational() {
      return isGestational;
    }

    @Override
    public boolean isOpen() {
      return true;
    }

    @Override
    public ConfidenceInterval confidenceInterval() {
      return ConfidenceInterval.precise();
    }

    @Override
    public TemporalPoint start() {
      return TemporalPoints.OPEN_START;
    }

    @Override
    public TemporalPoint end() {
      return TemporalPoints.OPEN_END;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AgeOpen ageOpen = (AgeOpen) o;
      return days == ageOpen.days && isGestational == ageOpen.isGestational;
    }

    @Override
    public int hashCode() {
      return Objects.hash(days, isGestational);
    }

    @Override
    public String toString() {
      return "AgeOpen{" +
        "days=" + days +
        ", isGestational=" + isGestational +
        '}';
    }
  }

}
