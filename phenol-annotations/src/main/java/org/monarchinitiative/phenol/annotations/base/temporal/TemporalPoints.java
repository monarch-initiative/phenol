package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

public class TemporalPoints {

  static final TemporalPoint LMP = new TemporalPoints.ClosedTemporalPoint(0, true);
  static final TemporalPoint BIRTH = new TemporalPoints.ClosedTemporalPoint(0, false);
  static final TemporalPoint OPEN_START = new OpenTemporalPoint(Integer.MIN_VALUE, true);
  static final TemporalPoint OPEN_END = new OpenTemporalPoint(Integer.MAX_VALUE, true);

  private TemporalPoints() {
  }

  public static TemporalPoint gestational(int days) {
    return days == 0 ? LMP : new ClosedTemporalPoint(days, true);
  }

  public static TemporalPoint postnatal(int days) {
    return days == 0 ? BIRTH : new ClosedTemporalPoint(days, false);
  }

  static class ClosedTemporalPoint implements TemporalPoint {

    private final int days;
    private final boolean isGestational;

    ClosedTemporalPoint(int days, boolean isGestational) {
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
    private final int days;
    private final boolean isGestational;

    OpenTemporalPoint(int days, boolean isGestational) {
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
