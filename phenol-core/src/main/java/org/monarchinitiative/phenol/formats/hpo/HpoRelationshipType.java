package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.ontology.data.RelationshipType;

/**
 * Enumeration for describing relation qualifiers in the HPO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public enum HpoRelationshipType implements RelationshipType {
  /** "Is-of" relation. */
  IS_A,
  /** Unknown, used for any other relation. */
  UNKNOWN;
}
