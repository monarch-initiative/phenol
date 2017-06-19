package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>import</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryImport extends StanzaEntry {

  /** Either a URL or an Id to import. */
  private final String value;

  /**
   * Constructor.
   *
   * @param value The URL or Id to import.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryImport(String value, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.IMPORT, trailingModifier, comment);
    this.value = value;
  }

  /**
   * @return The URL or Id to import.
   */
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "StanzaEntryImport [value=" + value + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
