package org.monarchinitiative.phenol.io.obo;

import org.monarchinitiative.phenol.ontology.data.TermSynonymScope;

/**
 * Representation of a stanza entry starting with <code>synomymtypedef</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntrySynonymtypedef extends StanzaEntry {

  /** Name of the synonym typedef. */
  private final String termSynonymScope;

  /** Description string. */
  private final String description;

  /** Optional {@link TermSynonymScope}, <code>null</code> for none. */
  private final TermSynonymScope synonymScopeIdentifier;

  /**
   * Constructor.
   *
   * @param synonymTypeName Synonym type name.
   * @param description Description string.
   * @param termSynonymScope Optional {@link TermSynonymScope}, <code>null</code> for none.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry, <code>null
   *     </code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntrySynonymtypedef(
      String synonymTypeName,
      String description,
      TermSynonymScope termSynonymScope,
      TrailingModifier trailingModifier,
      String comment) {
    super(StanzaEntryType.SYNONYMTYPEDEF, trailingModifier, comment);
    this.termSynonymScope = synonymTypeName;
    this.description = description;
    this.synonymScopeIdentifier = termSynonymScope;
  }

  /** @return The stanza's synonym type name. */
  public String getTermSynonymScope() {
    return termSynonymScope;
  }

  /** @return Stanza's description string. */
  public String getDescription() {
    return description;
  }

  /** @return Optional {@link TermSynonymScope}, <code>null</code> for none. */
  public TermSynonymScope getSynonymScopeIdentifier() {
    return synonymScopeIdentifier;
  }

  @Override
  public String toString() {
    return "StanzaEntrySynonytypedef [termSynonymScope="
        + termSynonymScope
        + ", description="
        + description
        + ", synonymScopeIdentifier="
        + synonymScopeIdentifier
        + ", getType()="
        + getType()
        + ", getTrailingModifier()="
        + getTrailingModifier()
        + ", getComment()="
        + getComment()
        + "]";
  }
}
