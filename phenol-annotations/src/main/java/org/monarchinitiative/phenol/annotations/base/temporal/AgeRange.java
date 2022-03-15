package org.monarchinitiative.phenol.annotations.base.temporal;

import java.util.Objects;

/**
 * @deprecated due to deprecation of {@link Age}.
 */
@Deprecated(forRemoval = true)
public class AgeRange {

  private final Age start;
  private final Age end;

  public static AgeRange of(Age start, Age end) {
    return new AgeRange(start, end);
  }

  private AgeRange(Age start, Age end) {
    this.start = start;
    this.end = end;
  }

  public Age start() {
    return start;
  }

  public Age end() {
    return end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AgeRange ageRange = (AgeRange) o;
    return Objects.equals(start, ageRange.start) && Objects.equals(end, ageRange.end);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, end);
  }

  @Override
  public String toString() {
    return "AgeRange{" +
      "start=" + start +
      ", end=" + end +
      '}';
  }
}
