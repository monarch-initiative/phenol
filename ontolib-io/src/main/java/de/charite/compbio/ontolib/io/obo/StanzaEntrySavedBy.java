package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>saved-by</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntrySavedBy extends StanzaEntry {

  /** User name of saving entity. */
  private final String userName;

  /**
   * Constructor.
   *
   * @param userName User name of saving entity.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntrySavedBy(String userName, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.SAVED_BY, trailingModifier, comment);
    this.userName = userName;
  }

  /**
   * @return The entry's user name value.
   */
  public String getUserName() {
    return userName;
  }

  @Override
  public String toString() {
    return "StanzaEntrySavedBy [userName=" + userName + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
