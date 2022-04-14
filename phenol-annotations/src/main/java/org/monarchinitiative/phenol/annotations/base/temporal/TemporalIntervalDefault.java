package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class TemporalIntervalDefault implements TemporalInterval {

  static TemporalInterval of(Age start, Age end) {
    if (start.isOpen() || end.isOpen()) {
      if (!start.isOpen()) {
        return new TemporalIntervalOpenEnd(start);
      } else if (!end.isOpen()) {
        return new TemporalIntervalOpenStart(end);
      } else {
        return TemporalIntervalOpen.instance();
      }
    } else {
      return new TemporalIntervalDefault(start, end);
    }
  }

  private final Age start;
  private final Age end;

  private TemporalIntervalDefault(Age start, Age end) {
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
    TemporalIntervalDefault that = (TemporalIntervalDefault) o;
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

  private static class TemporalIntervalOpenEnd implements TemporalInterval {

    private final Age start;

    private TemporalIntervalOpenEnd(Age start) {
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
      TemporalIntervalOpenEnd that = (TemporalIntervalOpenEnd) o;
      return Objects.equals(start, that.start);
    }

    @Override
    public int hashCode() {
      return Objects.hash(start);
    }

    @Override
    public String toString() {
      return "OpenEndTemporalInterval{" +
        "start=" + start +
        '}';
    }
  }

  private static class TemporalIntervalOpenStart implements TemporalInterval {

    private final Age end;

    private TemporalIntervalOpenStart(Age end) {
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
      TemporalIntervalOpenStart that = (TemporalIntervalOpenStart) o;
      return Objects.equals(end, that.end);
    }

    @Override
    public String toString() {
      return "OpenStartTemporalInterval{" +
        "end=" + end +
        '}';
    }
  }

  private static class TemporalIntervalOpen implements TemporalInterval {

    private static final TemporalIntervalOpen INSTANCE = new TemporalIntervalOpen();

    static TemporalIntervalOpen instance() {
      return INSTANCE;
    }

    private TemporalIntervalOpen() {}

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
      return "OpenTemporalInterval";
    }
  }
}
