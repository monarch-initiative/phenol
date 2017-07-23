package com.github.phenomics.ontolib.io.obo;

import java.util.List;

/**
 * Representation of a stanza entry starting with <code>relationship</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryRelationship extends StanzaEntry {

  /** Name of the relationship type. */
  private final String relationshipType;

  /** Id of referenced terms. */
  private final List<String> ids;

  /**
   * Constructor.
   *
   * @param relationshipType Name of the relationship type.
   * @param ids The Ids of the referenced terms.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryRelationship(String relationshipType, List<String> ids,
      TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.RELATIONSHIP, trailingModifier, comment);
    this.ids = ids;
    this.relationshipType = relationshipType;
  }

  /**
   * @return The Ids of the referenced term.
   */
  public List<String> getIds() {
    return ids;
  }

  /**
   * @return The relationship type.
   */
  public String getRelationshipType() {
    return relationshipType;
  }

  @Override
  public String toString() {
    return "StanzaEntryRelationship [ids=" + ids + ", relationshipType=" + relationshipType
        + ", getType()=" + getType() + ", getTrailingModifier()=" + getTrailingModifier()
        + ", getComment()=" + getComment() + "]";
  }

}
