package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>consider</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryConsider extends StanzaEntry {

  /** ID of referred term in stanza entry. */
  private final String id;

  /**
   * Constructor.
   *
   * @param id The ID of the referred-to term.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryConsider(String id, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.CONSIDER, trailingModifier, comment);
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
    return "StanzaEntryConsider [id=" + id + ", getType()=" + getType() + ", getTrailingModifier()="
        + getTrailingModifier() + ", getComment()=" + getComment() + "]";
  }

}
