package com.github.phenomics.ontolib.ontology.algo;

import com.github.phenomics.ontolib.graph.data.ImmutableDirectedGraph;
import com.github.phenomics.ontolib.graph.data.ImmutableEdge;
import com.github.phenomics.ontolib.ontology.data.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.github.phenomics.ontolib.ontology.algo.OntologyAlgorithm.*;
import static org.junit.Assert.*;

public class OntologyAlgorithmTest {

  private ImmutableSortedMap<String, String> metaInfo;
  private ImmutableList<TermId> vertices;
  private ImmutableList<ImmutableEdge<TermId>> edges;
  private ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph;

  private TermId rootTermId;
  private ImmutableMap<TermId, TestTerm> termMap;
  private ImmutableMap<TermId, TestTerm> obsoleteTermMap;
  private ImmutableMap<Integer, TestTermRelation> relationMap;
  private ImmutableOntology<TestTerm, TestTermRelation> ontology;

  private ImmutableTermId id1;
  private ImmutableTermId id2;
  private ImmutableTermId id3;
  private ImmutableTermId id4;
  private ImmutableTermId id5;

  @Before
  public void setUp() {
    metaInfo = ImmutableSortedMap.of();

    id1 = ImmutableTermId.constructWithPrefix("HP:0000001");
    id2 = ImmutableTermId.constructWithPrefix("HP:0000002");
    id3 = ImmutableTermId.constructWithPrefix("HP:0000003");
    id4 = ImmutableTermId.constructWithPrefix("HP:0000004");
    id5 = ImmutableTermId.constructWithPrefix("HP:0000005");
    vertices = ImmutableList.of(id1, id2, id3, id4, id5);
    edges =
      ImmutableList.of(ImmutableEdge.construct(id1, id2, 1), ImmutableEdge.construct(id1, id3, 2),
        ImmutableEdge.construct(id1, id4, 3), ImmutableEdge.construct(id2, id5, 4),
        ImmutableEdge.construct(id3, id5, 5), ImmutableEdge.construct(id4, id5, 6));
    graph = ImmutableDirectedGraph.construct(edges);

    rootTermId = id5;

    ImmutableMap.Builder<TermId, TestTerm> termMapBuilder = ImmutableMap.builder();
    termMapBuilder.put(id1, new TestTerm(id1, new ArrayList<>(), "term1", "some definition 1", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(id2, new TestTerm(id2, new ArrayList<>(), "term2", "some definition 2", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(id3, new TestTerm(id3, new ArrayList<>(), "term3", "some definition 3", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(id4, new TestTerm(id4, new ArrayList<>(), "term4", "some definition 4", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(id5, new TestTerm(id5, new ArrayList<>(), "term5", "some definition 5", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMap = termMapBuilder.build();

    obsoleteTermMap = ImmutableMap.of();

    ImmutableMap.Builder<Integer, TestTermRelation> relationMapBuilder = ImmutableMap.builder();
    relationMapBuilder.put(1, new TestTermRelation(id1, id2, 1));
    relationMapBuilder.put(2, new TestTermRelation(id1, id3, 2));
    relationMapBuilder.put(3, new TestTermRelation(id1, id4, 3));
    relationMapBuilder.put(4, new TestTermRelation(id2, id5, 4));
    relationMapBuilder.put(5, new TestTermRelation(id3, id5, 5));
    relationMapBuilder.put(6, new TestTermRelation(id4, id5, 6));
    relationMap = relationMapBuilder.build();

    ontology = new ImmutableOntology<>(metaInfo, graph, rootTermId,
      termMap.keySet(), obsoleteTermMap.keySet(), termMap, relationMap);
  }


  /** The example graph has id1->id2, id1->id3, id1->id4, id2->id5, id3-> id5, id4->id5 */
  @Test
  public void testPathExists() {
    assertTrue(existsPath(ontology,id1,id2));
    assertFalse(existsPath(ontology,id2,id1));
    assertTrue(existsPath(ontology,id1,id3));
    assertFalse(existsPath(ontology,id3,id1));
    assertTrue(existsPath(ontology,id1,id4));
    assertFalse(existsPath(ontology,id4,id1));
    assertTrue(existsPath(ontology,id1,id5));
    assertFalse(existsPath(ontology,id5,id1));
    assertTrue(existsPath(ontology,id2,id5));
    assertFalse(existsPath(ontology,id5,id2));
    assertTrue(existsPath(ontology,id4,id5));
    assertFalse(existsPath(ontology,id5,id4));
    // test that a term cannot have a path to itself.
    assertFalse(existsPath(ontology,id5,id5));
  }

  /** Test the default function, which includes the term itself in the set of returned terms */
  @Test
  public void testGetTermChildrenId4andId1() {
    // id4 has only one child term, id1: id1->id4
    Set<TermId> expected = ImmutableSet.of(id4,id1);
    assertEquals(expected, getChildTerms(ontology,id4));
    // id1 is a leaf term and thus has no children
    expected = ImmutableSet.of(id1);
    assertEquals(expected, getChildTerms(ontology,id1));
  }


  /** Test the default function, which includes the term itself in the set of returned terms */
  @Test
  public void testGetTermChildrenId5() {
    // id5 has 3 children: id2->id5, id3-> id5, id4->id5
    Set<TermId> expected = ImmutableSet.of(id2,id3,id4,id5);
    assertEquals(expected, getChildTerms(ontology,id5));
  }

  @Test
  public void testGetChildrenOfSet() {
    // the child of both id2 and id3 is id1
    Set<TermId> queryTerms = ImmutableSet.of(id2,id3);
    Set<TermId> expected = ImmutableSet.of(id1,id2,id3);
    assertEquals(expected,getChildTerms(ontology,queryTerms));
  }



  /** We are using a version of the function getChildTerms that does not return the query (parent) term. */
  @Test
  public void testReturnChildrenWithoutOriginalTerm() {
    // id5 has 3 children: id2->id5, id3-> id5, id4->id5
    Set<TermId> expected = ImmutableSet.of(id2,id3,id4);
    assertEquals(expected, getChildTerms(ontology,id5,false));
  }

  /** getDescendents returns not only children but all descendents.
   * id1 is a child of id3, which is a child of id5, so id1 is a descendent but not a child of id5*/
  @Test
  public void testGetDescendents() {
    Set<TermId> expected = ImmutableSet.of(id1,id2,id3,id4,id5);
    assertEquals(expected,getDescendents(ontology,id5));
  }

  @Test
  public void testGetParentsId2() {
    // the only ancestor of id2 is id5: id2->id5
    Set<TermId> expected = ImmutableSet.of(id2,id5);
    assertEquals(expected,getParentTerms(ontology,id2));
    // id2 is not an ancestor of id5
    assertNotEquals(expected,getParentTerms(ontology,id5));
    // instead, only id5 is an ancestor of id5
    expected=ImmutableSet.of(id5);
    assertEquals(expected,getParentTerms(ontology,id5));
  }

  @Test
  public void testGetParentsId1() {
    // id1 has three parents. Since id5 is a parent of both id2 ans id1, id1 has three ancestors (four including id1)
    //id1->id2, id1->id3, id1 -> id4; id2->id5, id3-> id5,
    // id5 is not a parent of id1, though!
    Set<TermId> expected = ImmutableSet.of(id1,id2,id3,id4);
    assertEquals(expected,getParentTerms(ontology,id1));
  }

  @Test
  public void testGetParentsOfSet() {
    //id3-> id5, id4->id5
    Set<TermId> queryTerms = ImmutableSet.of(id3,id4);
    Set<TermId> expected = ImmutableSet.of(id3,id4,id5);
    assertEquals(expected,getParentTerms(ontology,queryTerms));
  }

  @Test
  public void testGetAncestorsId1() {
    // id1 has id2, id3, id4m and id5 as ancestors
    Set<TermId> expected = ImmutableSet.of(id1,id2,id3,id4,id5);
    assertEquals(expected,getAncestorTerms(ontology,id1));
  }

  @Test
  public void testRootHasNoParent() {
    // id5 is the root of our graph and does not have a parent term other than itself
    Set<TermId> expected = ImmutableSet.of(id5);
    assertEquals(expected,getParentTerms(ontology,id5));
  }

  /** We are using a version of the function getChildTerms that does not return the query (parent) term. */
  @Test
  public void testReturnParentWithoutOriginalTerm() {
    // id1 has three parents. Since id5 is a parent of both id2 ans id1, id1 has three ancestors (four including id1)
    //id1->id2, id1->id3, id1 -> id4; id2->id5, id3-> id5,
    // id5 is not a parent of id1, though!
    Set<TermId> expected = ImmutableSet.of(id2,id3,id4);
    assertEquals(expected,getParentTerms(ontology,id1,false));
    // The root has no parent, we expect the empty set
    expected = new HashSet<>();
    assertEquals(expected,getParentTerms(ontology,id5,false));
  }


}
