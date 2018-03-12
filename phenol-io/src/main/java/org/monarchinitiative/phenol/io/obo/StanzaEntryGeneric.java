package org.monarchinitiative.phenol.io.obo;

/**
 * Representation of a stanza entry starting with other prefixes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryGeneric extends StanzaEntry {

  /** Value of the tag. */
  private final String tag;

  /** Value of stanza entry. */
  private final String value;

  /**
   * Constructor.
   *
   * @param tag The value of the tag.
   * @param value The value of the Id stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry, <code>null
   *     </code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryGeneric(
      String tag, String value, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.GENERIC, trailingModifier, comment);
    this.tag = tag;
    this.value = value;
  }

  /** @return The entry's tag. */
  public String getTag() {
    return tag;
  }

  /** @return The entry's value. */
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "StanzaEntryGeneric [tag="
        + tag
        + ", value="
        + value
        + ", getType()="
        + getType()
        + ", getTrailingModifier()="
        + getTrailingModifier()
        + ", getComment()="
        + getComment()
        + "]";
  }
}
