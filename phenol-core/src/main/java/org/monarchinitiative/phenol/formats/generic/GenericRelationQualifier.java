package org.monarchinitiative.phenol.formats.generic;

/**
 * Enumeration for describing relation qualifiers (folked from GoRelationQualifer)
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public enum GenericRelationQualifier {
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
