package com.github.phenomics.ontolib.ontology.algo;

import com.github.phenomics.ontolib.graph.data.ImmutableDirectedGraph;
import com.github.phenomics.ontolib.graph.data.ImmutableEdge;
import com.github.phenomics.ontolib.ontology.data.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.github.phenomics.ontolib.ontology.algo.OntologyAlgorithm.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class is designed to test the methods isSubclass, termsAreSiblings, termsAreRelated, and termsAreUnrelated
 * methods in the class {@link OntologyAlgorithm}. The graph derives from the node root and each level is denoted
 * in an hopefully obvious way (e.g., t1_1_1 is a subclass of t_1_1).
 */
public class OntologyAlgorithmTermRelationTest {

  private ImmutableSortedMap<String, String> metaInfo;
  private ImmutableList<TermId> vertices;
  private ImmutableList<ImmutableEdge<TermId>> edges;
  private ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph;

  private TermId rootTermId;
  private ImmutableMap<TermId, TestTerm> termMap;
  private ImmutableMap<TermId, TestTerm> obsoleteTermMap;
  private ImmutableMap<Integer, TestTermRelation> relationMap;
  private ImmutableOntology<TestTerm, TestTermRelation> ontology;

  private ImmutableTermId root;
  private ImmutableTermId t1;
  private ImmutableTermId t2;
  private ImmutableTermId t3;
  private ImmutableTermId t1_1;
  private ImmutableTermId t1_2;
  private ImmutableTermId t1_1_1;
  private ImmutableTermId t1_1_2;
  private ImmutableTermId t2_1;
  private ImmutableTermId t2_2;
  private ImmutableTermId t3_1;
  private ImmutableTermId t3_2;
  private ImmutableTermId id5;


  @Before
  public void setUp() {
    metaInfo = ImmutableSortedMap.of();

    root = ImmutableTermId.constructWithPrefix("HP:0000001");
    t1 = ImmutableTermId.constructWithPrefix("HP:0000002");
    t2 = ImmutableTermId.constructWithPrefix("HP:0000003");
    t3 = ImmutableTermId.constructWithPrefix("HP:0000004");
    t1_1 = ImmutableTermId.constructWithPrefix("HP:0000005");
    t1_2 = ImmutableTermId.constructWithPrefix("HP:0000006");
    t1_1_1 = ImmutableTermId.constructWithPrefix("HP:0000007");
    t1_1_2 = ImmutableTermId.constructWithPrefix("HP:0000008");
    t2_1 = ImmutableTermId.constructWithPrefix("HP:0000009");
    t2_2 = ImmutableTermId.constructWithPrefix("HP:0000010");
    t3_1 = ImmutableTermId.constructWithPrefix("HP:0000011");
    t3_2 = ImmutableTermId.constructWithPrefix("HP:0000012");
    vertices = ImmutableList.of(root,t1,t2,t3,t1_1,t1_1_1,t1_1_2,t2_1,t2_2,t3_1,t3_2);
    ImmutableList.Builder<ImmutableEdge<TermId>> builder = new ImmutableList.Builder();
    builder.add(ImmutableEdge.construct(t1, root, 1));
    builder.add(ImmutableEdge.construct(t2, root, 2));
    builder.add(ImmutableEdge.construct(t3, root, 11));
    builder.add(ImmutableEdge.construct(t1_1, t1, 3));
    builder.add(ImmutableEdge.construct(t1_2, t1, 4));
    builder.add(ImmutableEdge.construct(t1_1_1, t1_1, 5));
    builder.add(ImmutableEdge.construct(t1_1_2, t1_1, 6));
    builder.add(ImmutableEdge.construct(t2_1, t2, 7));
    builder.add(ImmutableEdge.construct(t2_2, t2, 8));
    builder.add(ImmutableEdge.construct(t3_1, t3, 9));
    builder.add(ImmutableEdge.construct(t3_2, t3, 10));

    edges = builder.build();
    graph = ImmutableDirectedGraph.construct(edges);

    rootTermId = root;

    ImmutableMap.Builder<TermId, TestTerm> termMapBuilder = ImmutableMap.builder();
    termMapBuilder.put(t1, new TestTerm(t1, new ArrayList<>(), "term1", "some definition 1", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(t2, new TestTerm(t2, new ArrayList<>(), "term2", "some definition 2", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(t3, new TestTerm(t3, new ArrayList<>(), "term3", "some definition 3", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(t1_1, new TestTerm(t1_1, new ArrayList<>(), "term1_1", "some definition 4", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(t1_2, new TestTerm(t1_2, new ArrayList<>(), "term1_2", "some definition 5", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));

    termMapBuilder.put(t1_1_1, new TestTerm(t1_1_1, new ArrayList<>(), "term1_1_1", "some definition 5", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(t1_1_2, new TestTerm(t1_1_2, new ArrayList<>(), "term1_1_2", "some definition 5", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(t2_1, new TestTerm(t2_1, new ArrayList<>(), "term2_1", "some definition 5", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(t2_2, new TestTerm(t2_2, new ArrayList<>(), "term2_2", "some definition 5", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(t3_1, new TestTerm(t3_1, new ArrayList<>(), "term3_1", "some definition 5", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));
    termMapBuilder.put(t3_2, new TestTerm(t3_2, new ArrayList<>(), "term3_2", "some definition 5", null,
      new ArrayList<>(), new ArrayList<>(), false, null, null, new ArrayList<>()));


    termMap = termMapBuilder.build();

    obsoleteTermMap = ImmutableMap.of();

    ImmutableMap.Builder<Integer, TestTermRelation> relationMapBuilder = ImmutableMap.builder();
//    relationMapBuilder.put(1, new TestTermRelation(id1, id2, 1));
//    relationMapBuilder.put(2, new TestTermRelation(id1, id3, 2));
//    relationMapBuilder.put(3, new TestTermRelation(id1, id4, 3));
//    relationMapBuilder.put(4, new TestTermRelation(id2, id5, 4));
//    relationMapBuilder.put(5, new TestTermRelation(id3, id5, 5));
//    relationMapBuilder.put(6, new TestTermRelation(id4, id5, 6));
    relationMap = relationMapBuilder.build();

    ontology = new ImmutableOntology<>(metaInfo, graph, rootTermId,
      termMap.keySet(), obsoleteTermMap.keySet(), termMap, relationMap);
  }


  /** t_1_1 is a subclass of t_1 but not vice versa */
  @Test public void testIsSubclass() {
    assertTrue(isSubclass(ontology,t1_1_1,t1_1));
    assertFalse(isSubclass(ontology,t1_1,t1_1_1));
    assertTrue(isSubclass(ontology,t1_1_2,t1_1));
    assertTrue(isSubclass(ontology,t2,root));
  }

  /** t1_1_1 and t1_1_2 are siblings. */
  @Test
  public void testIsSibling() {
    assertTrue(termsAreSiblings(ontology,t1_1_1,t1_1_2));
    assertFalse(termsAreSiblings(ontology,t1_1_1,t1_1));
    assertTrue(termsAreSiblings(ontology,t2,t3));
    assertFalse(termsAreSiblings(ontology,t1,root));
  }

  /** t1_1_1 and t1_2 have t1 has a common ancestor (and thus are not just connected by the root) */
  @Test
  public void testTermsAreRelated() {
    assertTrue(termsAreRelated(ontology,t1_1_1,t1_2));
    assertFalse(termsAreRelated(ontology,t1_1_2,t3));
  }

  @Test
  public void testTermsAreNotRelated() {
    assertFalse(termsAreUnrelated(ontology,t1_1_1,t1_2));
    assertTrue(termsAreUnrelated(ontology,t1_1_2,t3));
  }




}
