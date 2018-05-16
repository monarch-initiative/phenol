package org.monarchinitiative.phenol.formats.mpo;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.formats.generic.Relationship;
import org.monarchinitiative.phenol.ontology.data.RelationshipI;

/**
 * {@link RelationshipI} to use for the MPO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class MpoRelationship extends Relationship {
  private static final long serialVersionUID = -631825267033654540L;

  /**
   * Constructor.
   *
   * @param source Source {@link TermId}.
   * @param dest Destination {@link TermId}.
   * @param id The term relation's Id, corresponds to Id of edge in graph.
   * @param relationshipType The relation's type.
   */
  public MpoRelationship(TermId source, TermId dest, int id, MpoRelationshipType relationshipType) {
    super(source, dest, id, relationshipType);
  }

  @Override
  public String toString() {
    return "MpoRelationship [source="
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
