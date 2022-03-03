package org.monarchinitiative.phenol.annotations.base;

import java.util.Objects;

class TemporalRangeDefault implements TemporalRange {

  private final Age start;
  private final Age end;

  TemporalRangeDefault(Age start, Age end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public Age start() {
    return start;
  }

  @Override
  public Age end() {
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
    return "AgeRangeDefault{" +
      "start=" + start +
      ", end=" + end +
      '}';
  }
}