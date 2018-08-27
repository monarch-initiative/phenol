package org.monarchinitiative.phenol.ontology.data;

/**
 * Enumeration for describing relation qualifiers (forked from GoRelationQualifer in Ontolib).
 * A class that encapsulates the edge type in the ontology graph.
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public enum RelationshipType {
  /** "Is-a" relation. */
  IS_A("is_a"),
  /** "Intersection-of" relation. */
  INTERSECTION_OF("intersection_of"),
  /** "Union-of" relation. */
  UNION_OF("union_of"),
  /** "DISJOINT_FROM" relation. */
  DISJOINT_FROM("disjoint_from"),
  PART_OF("part_of"),
  REGULATES("regulates"),
  NEGATIVELY_REGULATES("negatively_regulates"),
  POSITIVELY_REGULATES("positively_regulates"),
  /** Unknown, used for any other relation. */
  UNKNOWN("unknown");

  private final String relationshipName;
  private RelationshipType(String name){
    this.relationshipName=name;
  }


}
