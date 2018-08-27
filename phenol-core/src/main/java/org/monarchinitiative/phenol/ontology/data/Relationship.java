package org.monarchinitiative.phenol.ontology.data;


/**
 * A class that encapsulates the edge type in the ontology graph.
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class Relationship {
  private static final long serialVersionUID = 2L;

  /** Source {@link TermId}. */
  protected final TermId source;

  /** Destination {@link TermId}. */
  protected final TermId target;

  /** Id for this term relation, corresponds to Id of edge in graph. */
  protected final int id;

  /** {@link RelationshipType} for this term relation. */
  private final RelationshipType relationshipType;

  /**
   * Constructor.
   *
   * @param source Source {@link TermId}.
   * @param dest Destination {@link TermId}.
   * @param id The term relation's Id, corresponds to Id of edge in graph.
   * @param relationshipType The relation's type.
   */
  public Relationship(
    TermId source, TermId dest, int id, RelationshipType relationshipType) {
    this.source = source;
    this.target = dest;
    this.id = id;
    this.relationshipType = relationshipType;
  }

  public TermId getSource() {
    return source;
  }

  public TermId getTarget() {
    return target;
  }

  public int getId() {
    return id;
  }

  /** @return The relation's qualifier. */
  public RelationshipType getRelationshipType() {
    return relationshipType;
  }

  @Override
  public String toString() {
    return "Relationship [source="
        + source
        + ", dest="
        + target
        + ", id="
        + id
        + ", relationshipType="
        + relationshipType
        + "]";
  }
}
