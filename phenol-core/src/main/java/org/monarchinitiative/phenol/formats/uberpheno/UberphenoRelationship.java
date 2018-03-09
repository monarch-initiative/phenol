package org.monarchinitiative.phenol.formats.uberpheno;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.formats.generic.GenericRelationship;
import org.monarchinitiative.phenol.ontology.data.Relationship;

/**
 * {@link Relationship} to use for the Uberpheno ontology.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class UberphenoRelationship extends GenericRelationship {
  private static final long serialVersionUID = 7825873706025851142L;

  /**
   * Constructor.
   *
   * @param source Source {@link TermId}.
   * @param dest Destination {@link TermId}.
   * @param id The term relation's Id, corresponds to Id of edge in graph.
   * @param relationType The relation's type.
   */
  public UberphenoRelationship(
      TermId source, TermId dest, int id, UberphenoRelationType relationType) {
    super(source, dest, id, relationType);
  }

  @Override
  public String toString() {
    return "UberphenoRelationship [source="
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
