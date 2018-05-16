package org.monarchinitiative.phenol.formats.mpo;

import org.monarchinitiative.phenol.ontology.data.RelationshipTypeI;

/**
 * Enumeration for describing relation qualifiers in the MPO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public enum MpoRelationshipType implements RelationshipTypeI {
  /** "Is-of" relation. */
  IS_A,
  /** Unknown, used for any other relation. */
  UNKNOWN
}
