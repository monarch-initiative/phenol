package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * The default {@link PointInTime} implementations.
 */
class PointsInTime {

  static final PointInTime LMP = new ClosedPointInTime(0, true);
  static final PointInTime BIRTH = new ClosedPointInTime(0, false);
  static final PointInTime OPEN_START = new OpenPointInTime(Integer.MIN_VALUE, true);
  static final PointInTime OPEN_END = new OpenPointInTime(Integer.MAX_VALUE, false);

  private PointsInTime() {
  }

  static PointInTime gestational(int days) {
    return days == 0 ? LMP : new ClosedPointInTime(days, true);
  }

  static PointInTime postnatal(int days) {
    return days == 0 ? BIRTH : new ClosedPointInTime(days, false);
  }

  static class ClosedPointInTime implements PointInTime {

    // TODO split further into sub-classes

    private final int days;
    private final boolean isGestational;

    ClosedPointInTime(int days, boolean isGestational) {
      this.days = days;
      this.isGestational = isGestational;
    }

    @Override
    public int days() {
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
      ClosedPointInTime that = (ClosedPointInTime) o;
      return days == that.days && isGestational == that.isGestational;
    }

    @Override
    public String toString() {
      return "ClosedPointInTime{" +
        "days=" + days +
        ", isGestational=" + isGestational +
        '}';
    }
  }

  static class OpenPointInTime implements PointInTime {
    private final int days;
    private final boolean isGestational;

    OpenPointInTime(int days, boolean isGestational) {
      this.days = days;
      this.isGestational = isGestational;
    }

    @Override
    public int days() {
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
      OpenPointInTime that = (OpenPointInTime) o;
      return days == that.days && isGestational == that.isGestational;
    }

    @Override
    public String toString() {
      return "OpenPointInTime{" +
        "days=" + days +
        ", isGestational=" + isGestational +
        '}';
    }
  }

}
