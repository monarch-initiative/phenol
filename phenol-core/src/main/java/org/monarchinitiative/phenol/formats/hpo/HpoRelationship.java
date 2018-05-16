package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.formats.generic.Relationship;
import org.monarchinitiative.phenol.ontology.data.RelationshipTypeI;
import org.monarchinitiative.phenol.ontology.data.RelationshipI;

/**
 * {@link RelationshipI} to use for the HPO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class HpoRelationship extends Relationship {
  private static final long serialVersionUID = 1792288507510989462L;

  /**
   * Constructor.
   *
   * @param source Source {@link TermId}.
   * @param dest Destination {@link TermId}.
   * @param id The term relation's Id, corresponds to Id of edge in graph.
   * @param relationshipType The relation's type.
   */
  public HpoRelationship(TermId source, TermId dest, int id, RelationshipTypeI relationshipType) {
    super(source, dest, id, relationshipType);
  }

  @Override
  public String toString() {
    return "HpoRelationship [source="
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
