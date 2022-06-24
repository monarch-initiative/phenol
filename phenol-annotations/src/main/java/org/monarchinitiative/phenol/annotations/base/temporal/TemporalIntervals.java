package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * The default {@link TemporalInterval} implementations.
 */
class TemporalIntervals {

  static final TemporalInterval BIRTH = TemporalInterval.of(PointsInTime.BIRTH, PointsInTime.BIRTH);

  static final TemporalInterval OPEN = new OpenTemporalInterval();

  static TemporalInterval of(PointInTime start, PointInTime end) {
    // TODO - implement
    //    if (start.isOpen() || end.isOpen()) {
    //      if (!start.isOpen()) {
    //        return new AgeRangeDefault.AgeRangeOpenEnd(start);
    //      } else if (!end.isOpen()) {
    //        return new AgeRangeDefault.AgeRangeOpenStart(end);
    //      } else {
    //        return AgeRangeDefault.AgeRangeOpen.instance();
    //      }
    //    } else {
    //      return new AgeRangeDefault(start, end);
    //    }

    return new DefaultTemporalInterval(start, end);
  }

  private TemporalIntervals() {
    // non-instantiable
  }

  static class DefaultTemporalInterval implements TemporalInterval {

    private final PointInTime start, end;

    private DefaultTemporalInterval(PointInTime start, PointInTime end) {
      this.start = start; // non-nullity checked upstream
      this.end = end; // non-nullity checked upstream
    }

    @Override
    public PointInTime start() {
      return start;
    }

    @Override
    public PointInTime end() {
      return end;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      DefaultTemporalInterval that = (DefaultTemporalInterval) o;
      return Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
      return Objects.hash(start, end);
    }

    @Override
    public String toString() {
      return "DefaultTemporalInterval{" +
        "start=" + start +
        ", end=" + end +
        '}';
    }
  }

  static class TemporalIntervalWithOpenEnd implements TemporalInterval {

    private final PointInTime start;

    TemporalIntervalWithOpenEnd(PointInTime start) {
      this.start = Objects.requireNonNull(start);
    }

    @Override
    public PointInTime start() {
      return start;
    }

    @Override
    public PointInTime end() {
      return PointsInTime.OPEN_END;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TemporalIntervalWithOpenEnd that = (TemporalIntervalWithOpenEnd) o;
      return Objects.equals(start, that.start);
    }

    @Override
    public int hashCode() {
      return Objects.hash(start);
    }

    @Override
    public String toString() {
      return "TemporalIntervalWithOpenEnd{" +
        "start=" + start +
        '}';
    }
  }

  static class TemporalIntervalWithOpenStart implements TemporalInterval {

    private final PointInTime end;

    TemporalIntervalWithOpenStart(PointInTime end) {
      this.end = Objects.requireNonNull(end);
    }

    @Override
    public PointInTime start() {
      return PointsInTime.OPEN_START;
    }

    @Override
    public PointInTime end() {
      return end;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TemporalIntervalWithOpenStart that = (TemporalIntervalWithOpenStart) o;
      return Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
      return Objects.hash(end);
    }

    @Override
    public String toString() {
      return "TemporalIntervalWithOpenStart{" +
        "end=" + end +
        '}';
    }
  }

  static class OpenTemporalInterval implements TemporalInterval {

    private OpenTemporalInterval() {
    }

    @Override
    public PointInTime start() {
      return PointsInTime.OPEN_START;
    }

    @Override
    public PointInTime end() {
      return PointsInTime.OPEN_END;
    }
    @Override
    public String toString() {
      return "OpenTemporalInterval";
    }
  }
}
