package com.github.phenomics.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>date</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryDate extends StanzaEntry {

  /** Value of stanza entry. */
  private final String value;

  /**
   * Constructor.
   *
   * @param value The value of the stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryDate(String value, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.DATE, trailingModifier, comment);
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
    return "StanzaEntryDate [value=" + value + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
