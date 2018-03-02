package org.monarchinitiative.phenol.io.obo;

// TODO: parse date?

/**
 * Representation of a stanza entry starting with <code>creation_date</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryCreationDate extends StanzaEntry {

  /** Creation date value of stanza entry. */
  private final String value;

  /**
   * Constructor.
   *
   * @param value The value of the Id stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryCreationDate(String value, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.CREATION_DATE, trailingModifier, comment);
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
    return "StanzaEntryCreationDate [value=" + value + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
