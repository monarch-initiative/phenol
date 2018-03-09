package org.monarchinitiative.phenol.formats.generic;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.RelationshipType;
import org.monarchinitiative.phenol.ontology.data.Relationship;

/**
 * {@link Relationship} to use for general ontologies.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class GenericRelationship implements Relationship {
  private static final long serialVersionUID = -2833503015330486640L;

  /** Source {@link TermId}. */
  protected final TermId source;

  /** Destination {@link TermId}. */
  protected final TermId target;

  /** Id for this term relation, corresponds to Id of edge in graph. */
  protected final int id;

  /** {@link RelationshipType} for this term relation. */
  protected final RelationshipType relationshipType;

  /**
   * Constructor.
   *
   * @param source Source {@link TermId}.
   * @param dest Destination {@link TermId}.
   * @param id The term relation's Id, corresponds to Id of edge in graph.
   * @param relationshipType The relation's type.
   */
  public GenericRelationship(TermId source, TermId dest, int id, RelationshipType relationshipType) {
    this.source = source;
    this.target = dest;
    this.id = id;
    this.relationshipType = relationshipType;
  }

  @Override
  public TermId getSource() {
    return source;
  }

  @Override
  public TermId getTarget() {
    return target;
  }

  @Override
  public int getId() {
    return id;
  }

  /** @return The relation's qualifier. */
  public RelationshipType getrelationshipType() {
    return relationshipType;
  }

  @Override
  public String toString() {
    return "GenericRelationship [source="
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
