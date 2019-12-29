package org.monarchinitiative.phenol.ontology.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enumeration for describing relation qualifiers (forked from GoRelationQualifer in Ontolib).
 * A class that encapsulates the edge type in the ontology graph.
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:j.jacobsen@qmul.ac.uk">Jules Jacobsen</a>
 */
public class RelationshipType {

  private static final Map<String, RelationshipType> cache = new ConcurrentHashMap<>();

  /** "Is-a" relation. */
  public static final RelationshipType IS_A = new RelationshipType("is_a", "is_a");
//  /** "Intersection-of" relation. */
//  INTERSECTION_OF("intersection_of"),
//  /** "Union-of" relation. */
//  UNION_OF("union_of"),
//  /** "DISJOINT_FROM" relation. */
//  DISJOINT_FROM("disjoint_from"),
  public static final RelationshipType PART_OF = new RelationshipType("http://purl.obolibrary.org/obo/BFO_0000050","part of");
//  HAS_PART("has_part"),
//  REGULATES("regulates"),
//  NEGATIVELY_REGULATES("negatively_regulates"),
//  POSITIVELY_REGULATES("positively_regulates"),
//  OCCURS_IN("occurs_in"),
//  HAPPENS_DURING("happens_during"),
//  ENDS_DURING("ends_during"),
  public static final RelationshipType SUBPROPERTY_OF = new RelationshipType("subPropertyOf", "subPropertyOf");
//  INVERSE_OF("inverseOf"),
  public static final RelationshipType HAS_MODIFIER = new RelationshipType("http://purl.obolibrary.org/obo/RO_0002573","hasModifier");
//  DISEASE_HAS_BASIS_IN_FEATURE("disease_has_basis_in_feature"),
//  DISEASE_SHARES_FEATURES_OF("disease_shares_features_of"),
//  DISEASE_HAS_FEATURE("disease_has_feature"),
//  DISEASE_HAS_MAJOR_FEATURE("disease_has_major_feature"),
//  DISEASE_CAUSES_FEATURE("disease_causes_feature"),
//  DISEASE_HAS_LOCATION("disease_has_location"),
//  PART_OF_PROGRESSION_OF_DISEASE("part_of_progression_of_disease"),
//  REALIZED_IN_RESPONSE_TO_STIMULUS("realized_in_response_to_stimulus"),
//  /** Unknown, used for any other relation. */
//  UNKNOWN("unknown");

  private static final Logger logger = LoggerFactory.getLogger(RelationshipType.class);

  private final String id;
  private final String label;

  private RelationshipType(String id, String name) {
    this.id = id;
    this.label = name;
  }

  public String getId() {
    return id;
  }

  public String getLabel() {
    return this.label;
  }

  public static RelationshipType of(String id, String label) {
    switch (id) {
      case "is_a":
        return IS_A;
      case "http://purl.obolibrary.org/obo/BFO_0000050":
      case "part of":
      case "part_of":
        return PART_OF;
//      case "http://purl.obolibrary.org/obo/BFO_0000051":
//      case "has part":
//        return HAS_PART;
//      case "http://purl.obolibrary.org/obo/RO_0002211":
//      case "regulates":
//        return REGULATES;
//      case "http://purl.obolibrary.org/obo/RO_0002212":
//      case "negatively regulates":
//        return NEGATIVELY_REGULATES;
//      case "http://purl.obolibrary.org/obo/RO_0002213":
//      case "positively regulates":
//        return POSITIVELY_REGULATES;
//      case "http://purl.obolibrary.org/obo/BFO_0000066":
//      case "occurs in":
//        return OCCURS_IN;
//      case "http://purl.obolibrary.org/obo/RO_0002092":
//      case "happens during":
//        return HAPPENS_DURING;
//      case "http://purl.obolibrary.org/obo/RO_0002093":
//      case "ends during":
//        return ENDS_DURING;
      case "subPropertyOf":
        return SUBPROPERTY_OF;
//      case "inverseOf":
//        return INVERSE_OF;
      case "http://purl.obolibrary.org/obo/RO_0002573":
      case "has modifier":
        return HAS_MODIFIER;
//      case "http://purl.obolibrary.org/obo/RO_0004022":
//        return DISEASE_HAS_BASIS_IN_FEATURE;
//      case "http://purl.obolibrary.org/obo/mondo#disease_shares_features_of":
//        return DISEASE_SHARES_FEATURES_OF;
//      case "http://purl.obolibrary.org/obo/RO_0004029":
//        return DISEASE_HAS_FEATURE;
//      case "http://purl.obolibrary.org/obo/mondo#disease_has_major_feature":
//        return DISEASE_HAS_MAJOR_FEATURE;
//      case "http://purl.obolibrary.org/obo/RO_0004026":
//        return DISEASE_HAS_LOCATION;
//      case "http://purl.obolibrary.org/obo/mondo#disease_causes_feature":
//        return DISEASE_CAUSES_FEATURE;
//      case "http://purl.obolibrary.org/obo/mondo#part_of_progression_of_disease":
//        return PART_OF_PROGRESSION_OF_DISEASE;
//      case "http://purl.obolibrary.org/obo/RO_0004028":
//        return REALIZED_IN_RESPONSE_TO_STIMULUS;
      default:
        return cache.computeIfAbsent(id, key -> new RelationshipType(id, label));
    }
  }

  /**
   * This function returns true if a RelationshipType obeys the annotation propagation rule (aka true-path rule)
   * @return true for IS_A and PART_OF, otherwise false
   */
  public boolean propagates() {
    return this.equals(IS_A) || this.equals(PART_OF);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RelationshipType that = (RelationshipType) o;
    return Objects.equals(id, that.id) &&
      Objects.equals(label, that.label);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, label);
  }

  @Override
  public String toString() {
    return "RelationshipType{" +
      "id='" + id + '\'' +
      ", label='" + label + '\'' +
      '}';
  }
}
