package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

class AgePostnatal {

  static final AgeOpen END = new AgeOpen(Integer.MAX_VALUE, false);
  static final AgePostnatalPrecise BIRTH = new AgePostnatalPrecise(0);

  static Age of(int days, ConfidenceInterval ci) {
    if (ci.isPrecise())
      return days == 0 ? BIRTH : new AgePostnatalPrecise(days);

    return new AgePostnatalImprecise(days, ci);
  }

  private static abstract class AgePostnatalBase implements Age {

    private final int days;

    protected AgePostnatalBase(int days) {
      if (days > MAX_DAYS)
        throw new ArithmeticException(String.format("Number of days %d must not be greater than %d", days, MAX_DAYS));
      this.days = days;
    }

    @Override
    public int days() {
      return days;
    }

    @Override
    public boolean isGestational() {
      return false;
    }

    @Override
    public boolean isOpen() {
      return false;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AgePostnatalBase that = (AgePostnatalBase) o;
      return days == that.days;
    }

    @Override
    public int hashCode() {
      return Objects.hash(days);
    }
  }

  private static class AgePostnatalPrecise extends AgePostnatalBase {

    private AgePostnatalPrecise(int days) {
      super(days);
    }

    @Override
    public ConfidenceInterval confidenceInterval() {
      return ConfidenceInterval.precise();
    }

    @Override
    public boolean equals(Object o) {
      return super.equals(o);
    }

    @Override
    public int hashCode() {
      return super.hashCode();
    }

    @Override
    public String toString() {
      return "AgePostnatalPrecise{" +
        "days=" + days() +
        '}';
    }

  }

  private static class AgePostnatalImprecise extends AgePostnatalBase {

    private final ConfidenceInterval ci;

    private AgePostnatalImprecise(int days, ConfidenceInterval ci) {
      super(days);
      this.ci = ci;
    }

    @Override
    public ConfidenceInterval confidenceInterval() {
      return ci;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      AgePostnatalImprecise that = (AgePostnatalImprecise) o;
      return Objects.equals(ci, that.ci);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), ci);
    }

    @Override
    public String toString() {
      return "AgePostnatalImprecise{" +
        "days=" + days() +
        "ci=" + ci +
        "}";
    }
  }

}
