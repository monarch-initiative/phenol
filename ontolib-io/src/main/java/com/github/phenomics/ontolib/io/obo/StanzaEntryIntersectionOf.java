package com.github.phenomics.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>intersection_of</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryIntersectionOf extends StanzaEntry {

  /** Id of referenced term. */
  private final String id;

  /** Optional name of the relationship type, <code>null</code>. */
  private final String relationshipType;

  /**
   * Constructor.
   *
   * @param relationshipType Optional name of the relationship type, <code>null</code>.
   * @param id The Id of the referenced term.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryIntersectionOf(String relationshipType, String id,
      TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.INTERSECTION_OF, trailingModifier, comment);
    this.id = id;
    this.relationshipType = relationshipType;
  }

  /**
   * @return The Id of the referenced term.
   */
  public String getId() {
    return id;
  }

  /**
   * @return The relationship type.
   */
  public String getRelationshipType() {
    return relationshipType;
  }

  @Override
  public String toString() {
    return "StanzaEntryIntersectionOf [id=" + id + ", relationshipType = " + relationshipType
        + ", getType()=" + getType() + ", getTrailingModifier()=" + getTrailingModifier()
        + ", getComment()=" + getComment() + "]";
  }

}
