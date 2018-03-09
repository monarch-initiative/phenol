package org.monarchinitiative.phenol.formats.go;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.formats.generic.GenericRelationship;
import org.monarchinitiative.phenol.ontology.data.RelationType;
import org.monarchinitiative.phenol.ontology.data.Relationship;

/**
 * {@link Relationship} to use for the GO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class GoRelationship extends GenericRelationship {
  private static final long serialVersionUID = -5960246458828096676L;

  /**
   * Constructor.
   *
   * @param source Source {@link TermId}.
   * @param dest Destination {@link TermId}.
   * @param id The term relation's Id, corresponds to Id of edge in graph.
   * @param relationType The relation's type.
   */
  public GoRelationship(TermId source, TermId dest, int id, RelationType relationType) {
    super(source, dest, id, relationType);
  }

  @Override
  public String toString() {
    return "GoRelationship [source="
        + source
        + ", dest="
        + target
        + ", id="
        + id
        + ", relationType="
        + relationType
        + "]";
  }
}
