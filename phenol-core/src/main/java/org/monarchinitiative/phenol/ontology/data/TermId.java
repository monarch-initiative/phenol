package org.monarchinitiative.phenol.ontology.data;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable  TermId.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public final class TermId implements Comparable<TermId>, Serializable {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 2L;

  private final int separatorPos;
  private final String value;

  /**
   * Construct from term ID including prefix. e.g. HP:1234567
   *
   * @param termId String with term Id to construct with.
   * @return Resulting {@link TermId}.
   * @throws PhenolRuntimeException if the string does not have a prefix
   */
  public static TermId of(String termId) {
    int pos = findPrefixSeparatorPosition(':', termId);
    return new TermId(pos, termId);
  }

  public static TermId of(String termPrefix, String id) {
    int pos = termPrefix.length();
    String termId = String.join(":", termPrefix, id);
    return new TermId(pos, termId);
  }

  private static int findPrefixSeparatorPosition(char separator, String termIdString) {
    int pos = termIdString.indexOf(separator);
    if (pos == -1) {
      throw new PhenolRuntimeException(
          "TermId construction error: '" + termIdString + "' does not have a prefix!");
    }
    return pos;
  }

  private TermId(int separatorPos, String termId) {
    this.separatorPos = separatorPos;
    this.value = termId;
  }

  public String getPrefix() {
    return value.substring(0, separatorPos);
  }

  public String getId() {
    return value.substring(separatorPos + 1);
  }

  public String getValue() {
    return value;
  }

  @Override
  public int compareTo(TermId that) {
    return this.value.compareTo(that.value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TermId termId1 = (TermId) o;
    return Objects.equals(value, termId1.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
