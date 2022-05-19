package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class TemporalRangeDefault implements TemporalRange {

  private final TemporalPoint start, end;

  static TemporalRangeDefault of(TemporalPoint start, TemporalPoint end) {
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

    return new TemporalRangeDefault(start, end);
  }

  private TemporalRangeDefault(TemporalPoint start, TemporalPoint end) {
    this.start = start; // non-nullity checked upstream
    this.end = end; // non-nullity checked upstream
  }

  @Override
  public TemporalPoint start() {
    return start;
  }

  @Override
  public TemporalPoint end() {
    return end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TemporalRangeDefault that = (TemporalRangeDefault) o;
    return Objects.equals(start, that.start) && Objects.equals(end, that.end);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, end);
  }

  @Override
  public String toString() {
    return "TemporalRangeDefault{" +
      "start=" + start +
      ", end=" + end +
      '}';
  }
}
