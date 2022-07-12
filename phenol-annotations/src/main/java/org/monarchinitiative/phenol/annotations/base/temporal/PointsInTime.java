package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * The default {@link PointInTime} implementations.
 */
class PointsInTime {

  static final PointInTime LMP = new ClosedPointInTime.GestationalClosedPointInTime(0);
  static final PointInTime BIRTH = new ClosedPointInTime.PostnatalClosedPointInTime(0);
  static final PointInTime OPEN_START = new OpenPointInTime(Integer.MIN_VALUE, true);
  static final PointInTime OPEN_END = new OpenPointInTime(Integer.MAX_VALUE, false);

  private PointsInTime() {
  }

  static PointInTime of(int days, boolean isGestational) {
    return isGestational
      ? days == 0 ? LMP : new ClosedPointInTime.GestationalClosedPointInTime(days)
      : days == 0 ? BIRTH : new ClosedPointInTime.PostnatalClosedPointInTime(days);
  }

  /**
   * The static subclasses mess here is aimed to reduce memory footprint of PointInTime objects, as we anticipate
   * loads of these to be required in a temporal-aware application.
   * <p>
   * Each PointInTime implementation residing in ClosedPointInTime is anticipated to have Java object header (12 bytes)
   * and the number of days (4 bytes), without requiring alignment padding.
   */
  private static class ClosedPointInTime {

    private static class GestationalClosedPointInTime implements PointInTime {

      private final int days;

      private GestationalClosedPointInTime(int days) {
        this.days = days;
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
        return true;
      }

      @Override
      public int hashCode() {
        return Objects.hash(days, isOpen(), isGestational());
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GestationalClosedPointInTime that = (GestationalClosedPointInTime) o;
        return days == that.days;
      }

      @Override
      public String toString() {
        return "PointInTime{" +
          "days=" + days +
          ", isGestational=true, isOpen=false}";
      }
    }

    private static class PostnatalClosedPointInTime implements PointInTime {
      private final int days;

      private PostnatalClosedPointInTime(int days) {
        this.days = days;
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
        return false;
      }

      @Override
      public int hashCode() {
        return Objects.hash(days, isOpen(), isGestational());
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostnatalClosedPointInTime that = (PostnatalClosedPointInTime) o;
        return days == that.days;
      }

      @Override
      public String toString() {
        return "PointInTime{" +
          "days=" + days +
          ", isGestational=false, isOpen=false}";
      }
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
      return Objects.hash(days, isOpen(), isGestational());
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
