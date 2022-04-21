package org.monarchinitiative.phenol.ontology.data;

/**
 * Immutable implementation of {@link TermXref}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class TermXref implements Identified {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** Referenced term Id. */
  private final TermId id;

  /** Referenced description. */
  private final String description;

  /**
   * Constructor.
   *
   * @param id The term's Id.
   * @param description The cross reference description.
   */
  public TermXref(TermId id, String description) {
    this.id = id;
    this.description = description;
  }

  @Override
  public TermId id() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "ImmutableTermXref [id=" + id + ", description=" + description + "]";
  }
}
