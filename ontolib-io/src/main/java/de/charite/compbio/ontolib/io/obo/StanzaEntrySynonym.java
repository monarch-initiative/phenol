package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>synonym</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntrySynonym extends StanzaEntry {

  /** Text of synonym. */
  private final String text;

  /** The scope identifier string. */
  private final SynonymScopeIdentifier scopeIdentifier;

  /** Optional synonym type name, <code>null</code> if missing. */
  private final String synonymTypeName;

  /** Optional dbXRef list of stanza entry, <code>null</code> if missing. */
  private final DBXRefList dbXRefList;

  /**
   * Constructor.
   *
   * @param text Synonym text
   * @param scopeIdentifier {@link SynonymScopeIdentifier} of this synonym.
   * @param synonymTypeName Optional synonym type name, <code>null</code> if missing.
   * @param dbXRefList Optional {@link DBXRefList}, <code>null</code> if missing.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntrySynonym(String text, SynonymScopeIdentifier scopeIdentifier,
      String synonymTypeName, DBXRefList dbXRefList, TrailingModifier trailingModifier,
      String comment) {
    super(StanzaEntryType.SYNONYM, trailingModifier, comment);
    this.text = text;
    this.scopeIdentifier = scopeIdentifier;
    this.synonymTypeName = synonymTypeName;
    this.dbXRefList = dbXRefList;
  }

  /**
   * @return The synonym text.
   */
  public String getText() {
    return text;
  }

  /**
   * @return The {@link SynonymScopeIdentifier} of this synonym.
   */
  public SynonymScopeIdentifier getScopeIdentifier() {
    return scopeIdentifier;
  }

  /**
   * @return Optional synonym type name, <code>null</code> if empty.
   */
  public String getSynonymTypeName() {
    return synonymTypeName;
  }

  /**
   * @return Optional {@link DBXRefList}, <code>null</code> if empty.
   */
  public DBXRefList getDbXRefList() {
    return dbXRefList;
  }

  @Override
  public String toString() {
    return "StanzaEntrySynonym [text=" + text + ", scopeIdentifier=" + scopeIdentifier
        + ", synonymTypeName=" + synonymTypeName + ", dbXRefList=" + dbXRefList + ", getType()="
        + getType() + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()="
        + getComment() + "]";
  }

}
