package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * Static utility class with default {@link TemporalPoint} implementation.
 */
class TemporalPoints {

  static final float TOLERANCE = 5e-6f;
  static final TemporalPoint LMP = new ClosedTemporalPoint(0, true);
  static final TemporalPoint BIRTH = new ClosedTemporalPoint(0, false);
  static final TemporalPoint OPEN_START = new OpenTemporalPoint(Float.NEGATIVE_INFINITY, true);
  static final TemporalPoint OPEN_END = new OpenTemporalPoint(Float.POSITIVE_INFINITY, false);

  private TemporalPoints() {
  }

  static TemporalPoint gestational(float days) {
    return days < TOLERANCE ? LMP : new ClosedTemporalPoint(days, true);
  }

  static TemporalPoint postnatal(float days) {
    return days < TOLERANCE ? BIRTH : new ClosedTemporalPoint(days, false);
  }

  static class ClosedTemporalPoint implements TemporalPoint {

    private final float days;
    private final boolean isGestational;

    ClosedTemporalPoint(float days, boolean isGestational) {
      this.days = days;
      this.isGestational = isGestational;
    }

    @Override
    public float days() {
      return days;
    }

    @Override
    public boolean isOpen() {
      return false;
    }

    @Override
    public boolean isGestational() {
      return isGestational;
    }

    @Override
    public int hashCode() {
      return Objects.hash(days, isGestational);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ClosedTemporalPoint that = (ClosedTemporalPoint) o;
      return days == that.days && isGestational == that.isGestational;
    }

    @Override
    public String toString() {
      return "ClosedTemporalPoint{" +
        "days=" + days +
        ", isGestational=" + isGestational +
        '}';
    }
  }

  static class OpenTemporalPoint implements TemporalPoint {
    private final float days;
    private final boolean isGestational;

    OpenTemporalPoint(float days, boolean isGestational) {
      this.days = days;
      this.isGestational = isGestational;
    }

    @Override
    public float days() {
      return days;
    }

    @Override
    public boolean isOpen() {
      return true;
    }

    @Override
    public boolean isGestational() {
      return isGestational;
    }

    @Override
    public int hashCode() {
      return Objects.hash(days, isGestational);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      OpenTemporalPoint that = (OpenTemporalPoint) o;
      return days == that.days && isGestational == that.isGestational;
    }

    @Override
    public String toString() {
      return "OpenTemporalPoint{" +
        "days=" + days +
        ", isGestational=" + isGestational +
        '}';
    }
  }

}
