package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>def</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryDef extends StanzaEntry {

  /** Comment value of stanza entry. */
  private final String text;

  /** The dbXRef list of stanza entry. */
  private final DbXrefList dbXRefList;

  /**
   * Constructor.
   *
   * @param text The value of the text stanza entry.
   * @param dbXRefList The stanza's {@link DbXrefList}.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryDef(String text, DbXrefList dbXRefList, TrailingModifier trailingModifier,
      String comment) {
    super(StanzaEntryType.DEF, trailingModifier, comment);
    this.text = text;
    this.dbXRefList = dbXRefList;
  }

  /**
   * @return The entry's text value.
   */
  public String getText() {
    return text;
  }

  /**
   * @return The entry's dbXRef list.
   */
  public DbXrefList getDbXRefList() {
    return dbXRefList;
  }

  @Override
  public String toString() {
    return "StanzaEntryDef [text=" + text + ", dbXRefList=" + dbXRefList + ", getType()="
        + getType() + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()="
        + getComment() + "]";
  }

}
