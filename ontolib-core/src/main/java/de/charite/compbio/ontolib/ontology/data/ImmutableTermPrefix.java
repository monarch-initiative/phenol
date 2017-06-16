package de.charite.compbio.ontolib.ontology.data;

/**
 * Implementation of an immutable {@link TermPrefix}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ImmutableTermPrefix implements TermPrefix {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** The prefix value. */
  private final String value;

  /**
   * Constructor.
   *
   * @param value The value of the prefix.
   */
  public ImmutableTermPrefix(String value) {
    this.value = value;
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

}
