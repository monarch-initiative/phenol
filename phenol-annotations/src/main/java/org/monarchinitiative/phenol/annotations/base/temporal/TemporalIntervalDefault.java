package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class TemporalIntervalDefault implements TemporalInterval {

  static TemporalInterval of(Timestamp start, Timestamp end) {
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

  private final Timestamp start;
  private final Timestamp end;

  private TemporalIntervalDefault(Timestamp start, Timestamp end) {
    this.start = start;
    this.end = end;
  }

  @Override
  public Timestamp start() {
    return start;
  }

  @Override
  public Timestamp end() {
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

    private final Timestamp start;

    private TemporalIntervalOpenEnd(Timestamp start) {
      this.start = start;
    }

    @Override
    public Timestamp start() {
      return start;
    }

    @Override
    public Timestamp end() {
      return Timestamp.openEnd();
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

    private final Timestamp end;

    private TemporalIntervalOpenStart(Timestamp end) {
      this.end = end;
    }

    @Override
    public Timestamp start() {
      return Timestamp.openStart();
    }

    @Override
    public Timestamp end() {
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
    public Timestamp start() {
      return Timestamp.openStart();
    }

    @Override
    public Timestamp end() {
      return Timestamp.openEnd();
    }

    @Override
    public String toString() {
      return "OpenTemporalInterval";
    }
  }
}
