package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with other prefixes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryGeneric extends StanzaEntry {

  /** Value of stanza entry. */
  private final String value;

  /**
   * Constructor.
   *
   * @param value The value of the ID stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryGeneric(String value, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.GENERIC, trailingModifier, comment);
    this.value = value;
  }

  /**
   * @return The entry's ID value.
   */
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "StanzaEntryGeneric [value=" + value + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
