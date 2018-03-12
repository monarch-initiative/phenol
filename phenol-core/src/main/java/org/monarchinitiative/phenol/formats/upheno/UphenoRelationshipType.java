package org.monarchinitiative.phenol.formats.upheno;

import org.monarchinitiative.phenol.ontology.data.RelationshipType;

/**
 * Enumeration for describing relation qualifiers in uPheno.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public enum UphenoRelationshipType implements RelationshipType {
  /** "Is-a" relation. */
  IS_A,
  /** Unknown, used for any other relation. */
  UNKNOWN;
}
