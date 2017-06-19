package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>xref</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryXref extends StanzaEntry {

  /** dbXRef value of stanza entry. */
  private final DbXref dbXRef;

  /**
   * Constructor.
   *
   * @param dbXref The value of the stanza entry.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryXref(DbXref dbXref, TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.XREF, trailingModifier, comment);
    this.dbXRef = dbXref;
  }

  /**
   * @return The entry's Id value.
   */
  public DbXref getDbXRef() {
    return dbXRef;
  }

  @Override
  public String toString() {
    return "StanzaEntryXRef [dbXRef=" + dbXRef + ", getType()=" + getType()
        + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()=" + getComment()
        + "]";
  }

}
