package org.monarchinitiative.phenol.io.obo;

import org.monarchinitiative.phenol.ontology.data.TermSynonymScope;

/**
 * Representation of a stanza entry starting with <code>synonym</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntrySynonym extends StanzaEntry {

  /** Text of synonym. */
  private final String text;

  /** The scope identifier string. */
  private final TermSynonymScope termSynonymScope;

  /** Optional synonym type name, <code>null</code> if missing. */
  private final String synonymTypeName;

  /** Optional dbXref list of stanza entry, <code>null</code> if missing. */
  private final DbXrefList dbXrefList;

  /**
   * Constructor.
   *
   * @param text Synonym text
   * @param termSynonymScope {@link TermSynonymScope} of this synonym.
   * @param synonymTypeName Optional synonym type name, <code>null</code> if missing.
   * @param dbXrefList Optional {@link DbXrefList}, <code>null</code> if missing.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry, <code>null
   *     </code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntrySynonym(
      String text,
      TermSynonymScope termSynonymScope,
      String synonymTypeName,
      DbXrefList dbXrefList,
      TrailingModifier trailingModifier,
      String comment) {
    super(StanzaEntryType.SYNONYM, trailingModifier, comment);
    this.text = text;
    this.termSynonymScope = termSynonymScope;
    this.synonymTypeName = synonymTypeName;
    this.dbXrefList = dbXrefList;
  }

  /** @return The synonym text. */
  public String getText() {
    return text;
  }

  /** @return The {@link TermSynonymScope} of this synonym. */
  public TermSynonymScope getTermSynonymScope() {
    return termSynonymScope;
  }

  /** @return Optional synonym type name, <code>null</code> if empty. */
  public String getSynonymTypeName() {
    return synonymTypeName;
  }

  /** @return Optional {@link DbXrefList}, <code>null</code> if empty. */
  public DbXrefList getDbXrefList() {
    return dbXrefList;
  }

  @Override
  public String toString() {
    return "StanzaEntrySynonym [text="
        + text
        + ", termSynonymScope="
        + termSynonymScope
        + ", synonymTypeName="
        + synonymTypeName
        + ", dbXrefList="
        + dbXrefList
        + ", getType()="
        + getType()
        + ", getTrailingModifier()="
        + getTrailingModifier()
        + ", getComment()="
        + getComment()
        + "]";
  }
}
