package org.monarchinitiative.phenol.ontology.algo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.*;

public class OntologyAlgorithmTest {

  /*
     The example graph:
                  id5
                 ^ ^ ^
                /  |  \
              id2 id3 id4
                ^  ^  ^
                 \ | /
                  id1
   */
  private ImmutableOntology ontology;

  private TermId id1;
  private TermId id2;
  private TermId id3;
  private TermId id4;
  private TermId id5;

  @BeforeEach
  public void setUp() {
    id1 = TermId.of("HP:0000001");
    id2 = TermId.of("HP:0000002");
    id3 = TermId.of("HP:0000003");
    id4 = TermId.of("HP:0000004");
    id5 = TermId.of("HP:0000005");

    List<Term> terms = List.of(
      Term.builder(id1)
        .name("term1")
        .definition("some definition 1")
        .build(),
      Term.builder(id2)
        .name("term2")
        .definition("some definition 2")
        .build(),
      Term.builder(id3)
        .name("term3")
        .definition("some definition 3")
        .build(),
      Term.builder(id4)
        .name("term4")
        .definition("some definition 4")
        .build(),
      Term.builder(id5)
        .name("term5")
        .definition("some definition 5")
        .build());

    List<Relationship> relationships = List.of(
      new Relationship(id1, id2, 1, RelationshipType.IS_A),
      new Relationship(id1, id3, 2, RelationshipType.IS_A),
      new Relationship(id1, id4, 3, RelationshipType.IS_A),
      new Relationship(id2, id5, 4, RelationshipType.IS_A),
      new Relationship(id3, id5, 5, RelationshipType.IS_A),
      new Relationship(id4, id5, 6, RelationshipType.IS_A)
    );

    ontology = ImmutableOntology.builder()
      .terms(terms)
      .relationships(relationships)
      .build();
  }

  /** The example graph has id1->id2, id1->id3, id1->id4, id2->id5, id3-> id5, id4->id5 */
  @Test
  public void testPathExists() {
    assertTrue(existsPath(ontology, id1, id2));
    assertFalse(existsPath(ontology, id2, id1));
    assertTrue(existsPath(ontology, id1, id3));
    assertFalse(existsPath(ontology, id3, id1));
    assertTrue(existsPath(ontology, id1, id4));
    assertFalse(existsPath(ontology, id4, id1));
    assertTrue(existsPath(ontology, id1, id5));
    assertFalse(existsPath(ontology, id5, id1));
    assertTrue(existsPath(ontology, id2, id5));
    assertFalse(existsPath(ontology, id5, id2));
    assertTrue(existsPath(ontology, id4, id5));
    assertFalse(existsPath(ontology, id5, id4));
    // test that a term cannot have a path to itself.
    assertFalse(existsPath(ontology, id5, id5));
  }

  /** Test the default function, which includes the term itself in the set of returned terms */
  @Test
  public void testGetTermChildrenId4andId1() {
    // id4 has only one child term, id1: id1->id4
    Set<TermId> expected = Set.of(id4, id1);
    assertEquals(expected, getChildTerms(ontology, id4));
    // id1 is a leaf term and thus has no children
    expected = Set.of(id1);
    assertEquals(expected, getChildTerms(ontology, id1));
  }

  /** Test the default function, which includes the term itself in the set of returned terms */
  @Test
  public void testGetTermChildrenId5() {
    // id5 has 3 children: id2->id5, id3-> id5, id4->id5
    Set<TermId> expected = Set.of(id2, id3, id4, id5);
    assertEquals(expected, getChildTerms(ontology, id5));
  }

  @Test
  public void testGetChildrenOfSet() {
    // the child of both id2 and id3 is id1
    Set<TermId> queryTerms = Set.of(id2, id3);
    Set<TermId> expected = Set.of(id1, id2, id3);
    assertEquals(expected, getChildTerms(ontology, queryTerms));
  }

  /**
   * We are using a version of the function getChildTerms that does not return the query (parent)
   * term.
   */
  @Test
  public void testReturnChildrenWithoutOriginalTerm() {
    // id5 has 3 children: id2->id5, id3-> id5, id4->id5
    Set<TermId> expected = Set.of(id2, id3, id4);
    assertEquals(expected, getChildTerms(ontology, id5, false));
  }

  /**
   * getDescendents returns not only children but all descendents. id1 is a child of id3, which is a
   * child of id5, so id1 is a descendent but not a child of id5
   */
  @Test
  public void testGetDescendents() {
    Set<TermId> expected = Set.of(id1, id2, id3, id4, id5);
    assertEquals(expected, getDescendents(ontology, id5));
  }

  @Test
  public void testGetParentsId2() {
    // the only ancestor of id2 is id5: id2->id5
    Set<TermId> expected = Set.of(id2, id5);
    assertEquals(expected, getParentTerms(ontology, id2));
    // id2 is not an ancestor of id5
    assertNotEquals(expected, getParentTerms(ontology, id5));
    // instead, only id5 is an ancestor of id5
    expected = Set.of(id5);
    assertEquals(expected, getParentTerms(ontology, id5));
  }

  @Test
  public void testGetParentsId1() {
    // id1 has three parents. Since id5 is a parent of both id2 ans id1, id1 has three ancestors (four including id1)
    //id1->id2, id1->id3, id1 -> id4; id2->id5, id3-> id5,
    // id5 is not a parent of id1, though!
    Set<TermId> expected = Set.of(id1, id2, id3, id4);
    assertEquals(expected, getParentTerms(ontology, id1));
  }

  @Test
  public void testGetParentsOfSet() {
    //id3-> id5, id4->id5
    Set<TermId> queryTerms = Set.of(id3, id4);
    Set<TermId> expected = Set.of(id3, id4, id5);
    assertEquals(expected, getParentTerms(ontology, queryTerms));
  }

  @Test
  public void testGetAncestorsId1() {
    // id1 has id2, id3, id4m and id5 as ancestors
    Set<TermId> expected = Set.of(id1, id2, id3, id4, id5);
    assertEquals(expected, getAncestorTerms(ontology, id1));
  }

  @Test
  public void testGetAncestorsFromSubOntology() {
    // We first try with id5 as a new root term; the resulting subontology is identical to the original one.
    Set<TermId> expected1 = Set.of(id1, id2, id3, id4, id5);
    assertEquals(expected1, getAncestorTerms(ontology, id5, id1, true));

    Set<TermId> expected2 = Set.of(id2, id3, id4, id5);
    assertEquals(expected2, getAncestorTerms(ontology, id5, id1, false));

    // We then try with id2 as a new root term; the resulting subontology is the one with two terms: id1 and id2.
    Set<TermId> expected3 = Set.of(id1, id2);
    assertEquals(expected3, getAncestorTerms(ontology, id2, id1, true));

    Set<TermId> expected4 = Set.of(id2);
    assertEquals(expected4, getAncestorTerms(ontology, id2, id1, false));
  }

  @Test
  public void testRootHasNoParent() {
    // id5 is the root of our graph and does not have a parent term other than itself
    Set<TermId> expected = Set.of(id5);
    assertEquals(expected, getParentTerms(ontology, id5));
  }

  /**
   * We are using a version of the function getChildTerms that does not return the query (parent)
   * term.
   */
  @Test
  public void testReturnParentWithoutOriginalTerm() {
    // id1 has three parents. Since id5 is a parent of both id2 ans id1, id1 has three ancestors (four including id1)
    //id1->id2, id1->id3, id1 -> id4; id2->id5, id3-> id5,
    // id5 is not a parent of id1, though!
    Set<TermId> expected = Set.of(id2, id3, id4);
    assertEquals(expected, getParentTerms(ontology, id1, false));
    // The root has no parent, we expect the empty set
    expected = new HashSet<>();
    assertEquals(expected, getParentTerms(ontology, id5, false));
  }
}
