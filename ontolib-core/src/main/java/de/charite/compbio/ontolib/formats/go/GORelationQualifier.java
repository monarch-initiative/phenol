package de.charite.compbio.ontolib.formats.go;

/**
 * Enumeration for describing relation qualifiers in the GO.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public enum GORelationQualifier {
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
