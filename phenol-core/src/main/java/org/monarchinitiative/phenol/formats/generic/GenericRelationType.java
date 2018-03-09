package org.monarchinitiative.phenol.formats.generic;

import org.monarchinitiative.phenol.ontology.data.RelationType;

/**
 * Enumeration for describing relation qualifiers (forked from GoRelationQualifer in Ontolib).
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public enum GenericRelationType implements RelationType {
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
