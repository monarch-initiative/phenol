package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>id</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryID extends StanzaEntry {

  /** ID value of stanza entry. */
  private final String id;

  /**
   * Constructor.
   *
   * @param id The value of the ID stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryID(String id, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.ID, trailingModifier, comment);
    this.id = id;
  }

  /**
   * @return The entry's ID value.
   */
  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return "StanzaEntryID [id=" + id + ", getType()=" + getType() + ", getTrailingModifier()="
        + getTrailingModifier() + ", getComment()=" + getComment() + "]";
  }

}
