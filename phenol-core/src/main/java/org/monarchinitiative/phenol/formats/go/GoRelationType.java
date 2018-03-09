package org.monarchinitiative.phenol.formats.go;

import org.monarchinitiative.phenol.ontology.data.RelationType;

/**
 * Enumeration for describing relation qualifiers in the GO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public enum GoRelationType implements RelationType {
  /** "Is-a" relation. */
  IS_A,
  /** "Intersection-of" relation. */
  INTERSECTION_OF,
  /** "Union-of" relation. */
  UNION_OF,
  /** "DISJOINT_FROM" relation. */
  DISJOINT_FROM,
  /** Unknown, used for any other relation. */
  UNKNOWN;
}
