package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>range</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryRange extends StanzaEntry {

  /** The range ID or value. */
  private final String value;

  /**
   * Constructor.
   *
   * @param value The range ID or value.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryRange(String value, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.RANGE, trailingModifier, comment);
    this.value = value;
  }

  /**
   * @return The range ID or value.
   */
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return "StanzaEntryRange [value=" + value + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
