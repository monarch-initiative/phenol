package org.monarchinitiative.phenol.formats.uberpheno;

import org.monarchinitiative.phenol.ontology.data.RelationType;

/**
 * Enumeration for describing relation qualifiers in the Uberpheno ontology.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public enum UberphenoRelationType implements RelationType {
  /** "Is-a" relation. */
  IS_A,
  /** Unknown, used for any other relation. */
  UNKNOWN;
}
