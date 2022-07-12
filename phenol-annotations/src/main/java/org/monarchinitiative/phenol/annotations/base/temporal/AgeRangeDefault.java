package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class AgeRangeDefault implements AgeRange {

  static AgeRange of(Age start, Age end) {
    if (start.isOpen() || end.isOpen()) {
      if (!start.isOpen()) {
        return new AgeRangeOpenEnd(start);
      } else if (!end.isOpen()) {
        return new AgeRangeOpenStart(end);
      } else {
        return AgeRangeOpen.instance();
      }
    } else {
      return new AgeRangeDefault(start, end);
    }
  }

  private final Age start;
  private final Age end;

  private AgeRangeDefault(Age start, Age end) {
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
    AgeRangeDefault that = (AgeRangeDefault) o;
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

  private static class AgeRangeOpenEnd implements AgeRange {

    private final Age start;

    private AgeRangeOpenEnd(Age start) {
      this.start = start;
    }

    @Override
    public Age start() {
      return start;
    }

    @Override
    public Age end() {
      return Age.openEnd();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AgeRangeOpenEnd that = (AgeRangeOpenEnd) o;
      return Objects.equals(start, that.start);
    }

    @Override
    public int hashCode() {
      return Objects.hash(start);
    }

    @Override
    public String toString() {
      return "AgeRangeOpenEnd{" +
        "start=" + start +
        '}';
    }
  }

  private static class AgeRangeOpenStart implements AgeRange {

    private final Age end;

    private AgeRangeOpenStart(Age end) {
      this.end = end;
    }

    @Override
    public Age start() {
      return Age.openStart();
    }

    @Override
    public Age end() {
      return end;
    }

    @Override
    public int hashCode() {
      return Objects.hash(end);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AgeRangeOpenStart that = (AgeRangeOpenStart) o;
      return Objects.equals(end, that.end);
    }

    @Override
    public String toString() {
      return "AgeRangeOpenStart{" +
        "end=" + end +
        '}';
    }
  }

  private static class AgeRangeOpen implements AgeRange {

    private static final AgeRangeOpen INSTANCE = new AgeRangeOpen();

    static AgeRangeOpen instance() {
      return INSTANCE;
    }

    private AgeRangeOpen() {}

    @Override
    public Age start() {
      return Age.openStart();
    }

    @Override
    public Age end() {
      return Age.openEnd();
    }

    @Override
    public String toString() {
      return "AgeRangeOpen";
    }
  }
}
