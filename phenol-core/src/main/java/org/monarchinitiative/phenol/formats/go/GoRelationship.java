package org.monarchinitiative.phenol.formats.go;

import org.monarchinitiative.phenol.ontology.data.RelationshipI;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.formats.generic.Relationship;
import org.monarchinitiative.phenol.ontology.data.RelationshipTypeI;

/**
 * {@link RelationshipI} to use for the GO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class GoRelationship extends Relationship {
  private static final long serialVersionUID = -5960246458828096676L;

  /**
   * Constructor.
   *
   * @param source Source {@link TermId}.
   * @param dest Destination {@link TermId}.
   * @param id The term relation's Id, corresponds to Id of edge in graph.
   * @param relationshipType The relation's type.
   */
  public GoRelationship(TermId source, TermId dest, int id, RelationshipTypeI relationshipType) {
    super(source, dest, id, relationshipType);
  }

  @Override
  public String toString() {
    return "GoRelationship [source="
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
