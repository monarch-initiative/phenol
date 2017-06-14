package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>alt_id</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryAltID extends StanzaEntry {

  /** alt_id value of stanza entry. */
  private final String altID;

  /**
   * Constructor.
   *
   * @param altID The value of the alt_id stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryAltID(String altID, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.ALT_ID, trailingModifier, comment);
    this.altID = altID;
  }

  /**
   * @return The entry's name value.
   */
  public String getAltID() {
    return altID;
  }

  @Override
  public String toString() {
    return "StanzaEntryAltID [altID=" + altID + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
