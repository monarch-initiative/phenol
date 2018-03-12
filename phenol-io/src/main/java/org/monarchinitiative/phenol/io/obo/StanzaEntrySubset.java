package org.monarchinitiative.phenol.io.obo;

/**
 * Representation of a stanza entry starting with <code>subset</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntrySubset extends StanzaEntry {

  /** Name value of stanza entry. */
  private final String name;

  /**
   * Constructor.
   *
   * @param name The name of the subset stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry, <code>null
   *     </code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntrySubset(String name, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.SUBSET, trailingModifier, comment);
    this.name = name;
  }

  /** @return The entry's name value. */
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return "StanzaEntrySubset [name="
        + name
        + ", getType()="
        + getType()
        + ", getTrailingModifier()="
        + getTrailingModifier()
        + ", getComment()="
        + getComment()
        + "]";
  }
}
