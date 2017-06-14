package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>xref</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryXRef extends StanzaEntry {

  /** dbXRef value of stanza entry. */
  private final DBXRef dbXRef;

  /**
   * Constructor.
   *
   * @param dbXRef The value of the stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryXRef(DBXRef dbXRef, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.XREF, trailingModifier, comment);
    this.dbXRef = dbXRef;
  }

  /**
   * @return The entry's ID value.
   */
  public DBXRef getDBXRef() {
    return dbXRef;
  }

  @Override
  public String toString() {
    return "StanzaEntryXRef [dbXRef=" + dbXRef + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
