package org.monarchinitiative.phenol.ontology.algo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class is designed to test the methods isSubclass, termsAreSiblings, termsAreRelated, and
 * termsAreUnrelated methods in the class {@link OntologyAlgorithm}. The graph derives from the node
 * root and each level is denoted in an hopefully obvious way (e.g., t1_1_1 is a subclass of t_1_1).
 */
public class OntologyAlgorithmRelationshipTest {

  private static final Ontology ontology;

  private static final TermId root = TermId.of("HP:0000001");
  private static final TermId t1 = TermId.of("HP:0000002");
  private static final TermId t2 = TermId.of("HP:0000003");
  private static final TermId t3 = TermId.of("HP:0000004");
  private static final TermId t1_1 = TermId.of("HP:0000005");
  private static final TermId t1_2 = TermId.of("HP:0000006");
  private static final TermId t1_1_1 = TermId.of("HP:0000007");
  private static final TermId t1_1_2 = TermId.of("HP:0000008");
  private static final TermId t2_1 = TermId.of("HP:0000009");
  private static final TermId t2_2 = TermId.of("HP:0000010");
  private static final TermId t3_1 = TermId.of("HP:0000011");
  private static final TermId t3_2 = TermId.of("HP:0000012");

  static {
    ImmutableList.Builder<Relationship> relationsBuilder = ImmutableList.builder();
    // n.b. these were originally incorrectly entered in the original test...
    relationsBuilder.add(new Relationship(t1, root, 1, RelationshipType.IS_A));
    relationsBuilder.add(new Relationship(t2, root, 2, RelationshipType.IS_A));
    relationsBuilder.add(new Relationship(t3, root, 3, RelationshipType.IS_A));
    relationsBuilder.add(new Relationship(t1_1, t1, 4, RelationshipType.IS_A));
    relationsBuilder.add(new Relationship(t1_2, t1, 5, RelationshipType.IS_A));
    relationsBuilder.add(new Relationship(t1_1_1, t1_1, 6, RelationshipType.IS_A));
    relationsBuilder.add(new Relationship(t1_1_2, t1_1, 7, RelationshipType.IS_A));
    relationsBuilder.add(new Relationship(t2_1, t2, 8, RelationshipType.IS_A));
    relationsBuilder.add(new Relationship(t2_2, t2, 9, RelationshipType.IS_A));
    relationsBuilder.add(new Relationship(t3_1, t3, 10, RelationshipType.IS_A));
    relationsBuilder.add(new Relationship(t3_2, t3, 11, RelationshipType.IS_A));
    List<Relationship> relationships = relationsBuilder.build();

    ImmutableList.Builder<Term> termsBuilder = ImmutableList.builder();
    termsBuilder.add(Term.of(root, "root"));
    termsBuilder.add(Term.of(t1, "term1"));
    termsBuilder.add(Term.of(t2, "term2"));
    termsBuilder.add(Term.of(t3, "term3"));
    termsBuilder.add(Term.of(t1_1, "term1_1"));
    termsBuilder.add(Term.of(t1_2, "term1_2"));
    termsBuilder.add(Term.of(t1_1_1, "term1_1_1"));
    termsBuilder.add(Term.of(t1_1_2, "term1_1_2"));
    termsBuilder.add(Term.of(t2_1, "term2_1"));
    termsBuilder.add(Term.of(t2_2, "term2_2"));
    termsBuilder.add(Term.of(t3_1, "term3_1"));
    termsBuilder.add(Term.of(t3_2, "term3_2"));

    List<Term> terms = termsBuilder.build();

    ontology = ImmutableOntology.builder()
      .metaInfo(ImmutableSortedMap.of())
      .terms(terms)
      .relationships(relationships)
      .build();
  }

  /**
   * t_1_1 is a subclass of t_1 but not vice versa
   */
  @Test
  public void testIsSubclass() {
    assertTrue(OntologyAlgorithm.isSubclass(ontology, t1_1_1, t1_1));
    assertFalse(OntologyAlgorithm.isSubclass(ontology, t1_1, t1_1_1));
    assertTrue(OntologyAlgorithm.isSubclass(ontology, t1_1_2, t1_1));
    assertTrue(OntologyAlgorithm.isSubclass(ontology, t2, root));
  }

  /**
   * t1_1_1 and t1_1_2 are siblings.
   */
  @Test
  public void testIsSibling() {
    assertTrue(OntologyAlgorithm.termsAreSiblings(ontology, t1_1_1, t1_1_2));
    assertFalse(OntologyAlgorithm.termsAreSiblings(ontology, t1_1_1, t1_1));
    assertTrue(OntologyAlgorithm.termsAreSiblings(ontology, t2, t3));
    assertFalse(OntologyAlgorithm.termsAreSiblings(ontology, t1, root));
  }

  /**
   * t1_1_1 and t1_2 have t1 has a common ancestor (and thus are not just connected by the root)
   */
  @Test
  public void testTermsAreRelated() {
    assertTrue(OntologyAlgorithm.termsAreRelated(ontology, t1_1_1, t1_2));
    assertFalse(OntologyAlgorithm.termsAreRelated(ontology, t1_1_2, t3));
  }

  @Test
  public void testTermsAreNotRelated() {
    assertFalse(OntologyAlgorithm.termsAreUnrelated(ontology, t1_1_1, t1_2));
    assertTrue(OntologyAlgorithm.termsAreUnrelated(ontology, t1_1_2, t3));
  }
}
