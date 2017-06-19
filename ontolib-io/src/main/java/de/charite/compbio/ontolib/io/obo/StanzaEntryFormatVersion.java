package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>format-version</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryFormatVersion extends StanzaEntry {

  /** Value of stanza entry. */
  private final String value;

  /**
   * Constructor.
   *
   * @param value The value of the Id stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryFormatVersion(String value, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.FORMAT_VERSION, trailingModifier, comment);
    this.value = value;
  }

  /**
   * @return The entry's value.
   */
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "StanzaEntryFormatVersion [value=" + value + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
