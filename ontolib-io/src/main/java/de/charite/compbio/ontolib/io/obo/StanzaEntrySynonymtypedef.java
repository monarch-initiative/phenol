package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>synomymtypedef</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntrySynonymtypedef extends StanzaEntry {

  /** Name of the synonym typedef. */
  private final String synonymTypeName;

  /** Description string. */
  private final String description;

  /** Optional {@link SynonymScopeIdentifier}, <code>null</code> for none. */
  private final SynonymScopeIdentifier synonymScopeIdentifier;

  /**
   * Constructor.
   *
   * @param synonymTypeName Synonym type name.
   * @param description Description string.
   * @param synonymScopeIdentifier Optional {@link SynonymScopeIdentifier}, <code>null</code> for
   *        none.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntrySynonymtypedef(String synonymTypeName, String description,
      SynonymScopeIdentifier synonymScopeIdentifier, TrailingModifier trailingModifier,
      String comment) {
    super(StanzaEntryType.SYNONYMTYPEDEF, trailingModifier, comment);
    this.synonymTypeName = synonymTypeName;
    this.description = description;
    this.synonymScopeIdentifier = synonymScopeIdentifier;
  }

  /**
   * @return The stanza's synonym type name.
   */
  public String getSynonymTypeName() {
    return synonymTypeName;
  }

  /**
   * @return Stanza's description string.
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return Optional {@link SynonymScopeIdentifier}, <code>null</code> for none.
   */
  public SynonymScopeIdentifier getSynonymScopeIdentifier() {
    return synonymScopeIdentifier;
  }

  @Override
  public String toString() {
    return "StanzaEntrySynonytypedef [synonymTypeName=" + synonymTypeName + ", description="
        + description + ", synonymScopeIdentifier=" + synonymScopeIdentifier + ", getType()="
        + getType() + ", getTrailingModifier()=" + getTrailingModifier() + ", getComment()="
        + getComment() + "]";
  }

}
