package com.github.phenomics.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>saved-by</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntrySavedBy extends StanzaEntry {

  /** User name of saving entity. */
  private final String value;

  /**
   * Constructor.
   *
   * @param value User name of saving entity.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntrySavedBy(String value, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.SAVED_BY, trailingModifier, comment);
    this.value = value;
  }

  /**
   * @return The entry's user name value.
   */
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "StanzaEntrySavedBy [value=" + value + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
