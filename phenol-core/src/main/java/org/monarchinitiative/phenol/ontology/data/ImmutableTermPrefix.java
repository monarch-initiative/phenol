package org.monarchinitiative.phenol.ontology.data;

/**
 * Implementation of an immutable {@link TermPrefix}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ImmutableTermPrefix implements TermPrefix {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /** The prefix value. */
  private final String value;

  /** The precomputed hash value. */
  private final int hashCodeValue;

  /**
   * Constructor.
   *
   * @param value The value of the prefix.
   */
  public ImmutableTermPrefix(String value) {
    this.value = value;
    this.hashCodeValue = value.hashCode();
  }

  @Override
  public int compareTo(TermPrefix o) {
    if (this == o) {
      return 0;
    } else {
      return value.compareTo(o.getValue());
    }
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "ImmutableTermPrefix [value=" + value + "]";
  }

  @Override
  public int hashCode() {
    return hashCodeValue;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ImmutableTermPrefix other = (ImmutableTermPrefix) obj;
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

}
