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
  HAS_PART("has_part"),
  REGULATES("regulates"),
  NEGATIVELY_REGULATES("negatively_regulates"),
  POSITIVELY_REGULATES("positively_regulates"),
  OCCURS_IN("occurs_in"),
  HAPPENS_DURING("happens_during"),
  ENDS_DURING("ends_during"),
  SUBPROPERTY_OF("subpropertyOf"),
  INVERSE_OF("inverseOf"),
  HAS_MODIFIER("hasModifier"),
  DISEASE_HAS_BASIS_IN_FEATURE("disease_has_basis_in_feature"),
  DISEASE_SHARES_FEATURES_OF("disease_shares_features_of"),
  DISEASE_HAS_FEATURE("disease_has_feature"),
  DISEASE_HAS_MAJOR_FEATURE("disease_has_major_feature"),
  DISEASE_CAUSES_FEATURE("disease_causes_feature"),
  DISEASE_HAS_LOCATION("disease_has_location"),
  PART_OF_PROGRESSION_OF_DISEASE("part_of_progression_of_disease"),
  REALIZED_IN_RESPONSE_TO_STIMULUS("realized_in_response_to_stimulus"),
  /** Unknown, used for any other relation. */
  UNKNOWN("unknown");

  private final String relationshipName;

  RelationshipType(String name) {
    this.relationshipName = name;
  }

  public String getRelationshipName() {
    return this.relationshipName;
  }

  static public RelationshipType fromString(String reltype) {
    switch (reltype) {
      case "is_a":
        return IS_A;
      case "http://purl.obolibrary.org/obo/BFO_0000050":
      case "part of":
      case "part_of":
        return PART_OF;
      case "http://purl.obolibrary.org/obo/BFO_0000051":
      case "has part":
        return HAS_PART;
      case "http://purl.obolibrary.org/obo/RO_0002211":
      case "regulates":
        return REGULATES;
      case "http://purl.obolibrary.org/obo/RO_0002212":
      case "negatively regulates":
        return NEGATIVELY_REGULATES;
      case "http://purl.obolibrary.org/obo/RO_0002213":
      case "positively regulates":
        return POSITIVELY_REGULATES;
      case "http://purl.obolibrary.org/obo/BFO_0000066":
      case "occurs in":
        return OCCURS_IN;
      case "http://purl.obolibrary.org/obo/RO_0002092":
      case "happens during":
        return HAPPENS_DURING;
      case "http://purl.obolibrary.org/obo/RO_0002093":
      case "ends during":
        return ENDS_DURING;
      case "subPropertyOf":
        return SUBPROPERTY_OF;
      case "inverseOf":
        return INVERSE_OF;
      case "http://purl.obolibrary.org/obo/RO_0002573":
      case "has modifier":
        return HAS_MODIFIER;
      case "http://purl.obolibrary.org/obo/RO_0004022":
        return DISEASE_HAS_BASIS_IN_FEATURE;
      case "http://purl.obolibrary.org/obo/mondo#disease_shares_features_of":
        return DISEASE_SHARES_FEATURES_OF;
      case "http://purl.obolibrary.org/obo/RO_0004029":
        return DISEASE_HAS_FEATURE;
      case "http://purl.obolibrary.org/obo/mondo#disease_has_major_feature":
        return DISEASE_HAS_MAJOR_FEATURE;
      case "http://purl.obolibrary.org/obo/RO_0004026":
        return DISEASE_HAS_LOCATION;
      case "http://purl.obolibrary.org/obo/mondo#disease_causes_feature":
        return DISEASE_CAUSES_FEATURE;
      case "http://purl.obolibrary.org/obo/mondo#part_of_progression_of_disease":
        return PART_OF_PROGRESSION_OF_DISEASE;
      case "http://purl.obolibrary.org/obo/RO_0004028":
        return REALIZED_IN_RESPONSE_TO_STIMULUS;
      default:
        return UNKNOWN;
    }
  }

  /**
   * This function returns true if a RelationshipType obeys the annotation propagation rule (aka true-path rule)
   * @return true for IS_A and PART_OF, otherwise false
   */
  public boolean propagates() {
    switch (this) {
      case IS_A:
      case PART_OF:
        return true;
      default:
        return false;
    }
  }
}
