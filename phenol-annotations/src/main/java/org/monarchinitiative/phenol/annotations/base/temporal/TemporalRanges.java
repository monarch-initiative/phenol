package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class TemporalRanges {

  static final TemporalRange BIRTH = TemporalRange.of(TemporalPoints.BIRTH, TemporalPoints.BIRTH);

  static final TemporalRange OPEN = new TemporalRangeOpen();

  static class TemporalRangeOpenEnd implements TemporalRange {

    private final TemporalPoint start;

    TemporalRangeOpenEnd(TemporalPoint start) {
      this.start = Objects.requireNonNull(start);
    }

    @Override
    public TemporalPoint start() {
      return start;
    }

    @Override
    public TemporalPoint end() {
      return TemporalPoints.OPEN_END;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TemporalRangeOpenEnd that = (TemporalRangeOpenEnd) o;
      return Objects.equals(start, that.start);
    }

    @Override
    public int hashCode() {
      return Objects.hash(start);
    }

    @Override
    public String toString() {
      return "TemporalRangeOpenEnd{" +
        "start=" + start +
        '}';
    }
  }

  static class TemporalRangeOpenStart implements TemporalRange {

    private final TemporalPoint end;

    TemporalRangeOpenStart(TemporalPoint end) {
      this.end = Objects.requireNonNull(end);
    }

    @Override
    public TemporalPoint start() {
      return TemporalPoints.OPEN_START;
    }

    @Override
    public TemporalPoint end() {
      return end;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TemporalRangeOpenStart that = (TemporalRangeOpenStart) o;
      return Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
      return Objects.hash(end);
    }

    @Override
    public String toString() {
      return "TemporalRangeOpenStart{" +
        "end=" + end +
        '}';
    }
  }

  static class TemporalRangeOpen implements TemporalRange {

    private TemporalRangeOpen() {
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
    public String toString() {
      return "TemporalRangeOpen";
    }
  }

}
