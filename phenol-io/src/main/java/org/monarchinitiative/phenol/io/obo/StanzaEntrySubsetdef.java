package org.monarchinitiative.phenol.io.obo;

/**
 * Representation of a stanza entry starting with <code>subsetdef</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntrySubsetdef extends StanzaEntry {

  /** Name of the subset. */
  private final String name;

  /** Description of the subset. */
  private final String description;

  /**
   * Constructor.
   *
   * @param name Name of the subset.
   * @param description Description of the subset.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry, <code>null
   *     </code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntrySubsetdef(
      String name, String description, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.SUBSETDEF, trailingModifier, comment);
    this.name = name;
    this.description = description;
  }

  /** @return The subset name. */
  public String getName() {
    return name;
  }

  /** @return The subset's description. */
  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "StanzaEntrySubsetdef [name="
        + name
        + ", description="
        + description
        + ", getType()="
        + getType()
        + ", getTrailingModifier()="
        + getTrailingModifier()
        + ", getComment()="
        + getComment()
        + "]";
  }
}
