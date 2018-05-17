package org.monarchinitiative.phenol.formats.generic;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.RelationshipTypeI;

/**
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class Relationship {
  private static final long serialVersionUID = -2833503015330486640L;

  /** Source {@link TermId}. */
  protected final TermId source;

  /** Destination {@link TermId}. */
  protected final TermId target;

  /** Id for this term relation, corresponds to Id of edge in graph. */
  protected final int id;

  /** {@link RelationshipTypeI} for this term relation. */
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
