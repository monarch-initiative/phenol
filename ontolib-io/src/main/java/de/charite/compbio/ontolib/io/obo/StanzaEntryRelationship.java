package de.charite.compbio.ontolib.io.obo;

/**
 * Representation of a stanza entry starting with <code>relationship</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class StanzaEntryRelationship extends StanzaEntry {

  /** ID of referenced term. */
  private final String id;

  /** Name of the relationship type. */
  private final String relationshipType;

  /**
   * Constructor.
   * @param relationshipType Name of the relationship type.
   * @param id The ID of the referenced term.
   * @param trailingModifier Optional {@link TrailingModifier} of the stanza entry,
   *        <code>null</code> for none.
   * @param comment Optional comment string of the stanza entry, <code>null</code> for none.
   */
  public StanzaEntryRelationship(String relationshipType, String id,
      TrailingModifier trailingModifier, String comment) {
    super(StanzaEntryType.RELATIONSHIP, trailingModifier, comment);
    this.id = id;
    this.relationshipType = relationshipType;
  }

  /**
   * @return The ID of the referenced term.
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
    return "StanzaEntryRelationship [id=" + id + ", relationshipType=" + relationshipType
        + ", getType()=" + getType() + ", getTrailingModifier()=" + getTrailingModifier()
        + ", getComment()=" + getComment() + "]";
  }

}
