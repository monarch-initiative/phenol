package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>alt_id</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryAltId extends StanzaEntry {

  /** alt_id value of stanza entry. */
  private final String altId;

  /**
   * Constructor.
   *
   * @param altId The value of the alt_id stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryAltId(String altId, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.ALT_Id, trailingModifier, comment);
    this.altId = altId;
  }

  /**
   * @return The entry's name value.
   */
  public String getAltId() {
    return altId;
  }

  @Override
  public String toString() {
    return "StanzaEntryAltId [altId=" + altId + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
