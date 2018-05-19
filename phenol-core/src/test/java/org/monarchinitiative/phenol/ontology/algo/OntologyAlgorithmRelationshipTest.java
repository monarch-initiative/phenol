package org.monarchinitiative.phenol.ontology.algo;

import org.monarchinitiative.phenol.formats.generic.Relationship;
import org.monarchinitiative.phenol.formats.generic.RelationshipType;
import org.monarchinitiative.phenol.formats.generic.Term;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.util.GraphUtil;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.junit.Test;
import org.monarchinitiative.phenol.ontology.data.*;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class is designed to test the methods isSubclass, termsAreSiblings, termsAreRelated, and
 * termsAreUnrelated methods in the class {@link OntologyAlgorithm}. The graph derives from the node
 * root and each level is denoted in an hopefully obvious way (e.g., t1_1_1 is a subclass of t_1_1).
 */
public class OntologyAlgorithmRelationshipTest {

  private ImmutableSortedMap<String, String> metaInfo;
  private DefaultDirectedGraph<TermId, IdLabeledEdge> graph;

  private TermId rootTermId;
  private ImmutableMap<TermId, Term> termMap;
  private ImmutableMap<TermId, Term> obsoleteTermMap;
  private ImmutableMap<Integer, Relationship> relationMap;
  private ImmutableOntology ontology;

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

    graph = new DefaultDirectedGraph<>(IdLabeledEdge.class);
    GraphUtil.addEdgeToGraph(graph, t1, root, 1);
    GraphUtil.addEdgeToGraph(graph, t2, root, 2);
    GraphUtil.addEdgeToGraph(graph, t3, root, 11);
    GraphUtil.addEdgeToGraph(graph, t1_1, t1, 3);
    GraphUtil.addEdgeToGraph(graph, t1_2, t1, 4);
    GraphUtil.addEdgeToGraph(graph, t1_1_1, t1_1, 5);
    GraphUtil.addEdgeToGraph(graph, t1_1_2, t1_1, 6);
    GraphUtil.addEdgeToGraph(graph, t2_1, t2, 7);
    GraphUtil.addEdgeToGraph(graph, t2_2, t2, 8);
    GraphUtil.addEdgeToGraph(graph, t3_1, t3, 9);
    GraphUtil.addEdgeToGraph(graph, t3_2, t3, 10);

    rootTermId = root;

    ImmutableMap.Builder<TermId, Term> termMapBuilder = ImmutableMap.builder();
    termMapBuilder.put(
        t1,
        new Term(
            t1,
            new ArrayList<>(),
            "term1",
            "some definition 1",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        t2,
        new Term(
            t2,
            new ArrayList<>(),
            "term2",
            "some definition 2",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        t3,
        new Term(
            t3,
            new ArrayList<>(),
            "term3",
            "some definition 3",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        t1_1,
        new Term(
            t1_1,
            new ArrayList<>(),
            "term1_1",
            "some definition 4",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        t1_2,
        new Term(
            t1_2,
            new ArrayList<>(),
            "term1_2",
            "some definition 5",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));

    termMapBuilder.put(
        t1_1_1,
        new Term(
            t1_1_1,
            new ArrayList<>(),
            "term1_1_1",
            "some definition 5",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        t1_1_2,
        new Term(
            t1_1_2,
            new ArrayList<>(),
            "term1_1_2",
            "some definition 5",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        t2_1,
        new Term(
            t2_1,
            new ArrayList<>(),
            "term2_1",
            "some definition 5",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        t2_2,
        new Term(
            t2_2,
            new ArrayList<>(),
            "term2_2",
            "some definition 5",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        t3_1,
        new Term(
            t3_1,
            new ArrayList<>(),
            "term3_1",
            "some definition 5",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        t3_2,
        new Term(
            t3_2,
            new ArrayList<>(),
            "term3_2",
            "some definition 5",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));

    termMap = termMapBuilder.build();

    obsoleteTermMap = ImmutableMap.of();

    ImmutableMap.Builder<Integer, Relationship> relationMapBuilder = ImmutableMap.builder();
        relationMapBuilder.put(1, new Relationship(t1, t2, 1, RelationshipType.IS_A));
        relationMapBuilder.put(2, new Relationship(t1, t3, 2, RelationshipType.IS_A));
    relationMap = relationMapBuilder.build();

    ontology =
        new ImmutableOntology(
            metaInfo,
            graph,
            rootTermId,
            termMap.keySet(),
            obsoleteTermMap.keySet(),
            termMap,
            relationMap);
  }

  /** t_1_1 is a subclass of t_1 but not vice versa */
  @Test
  public void testIsSubclass() {
    assertTrue(OntologyAlgorithm.isSubclass(ontology, t1_1_1, t1_1));
    assertFalse(OntologyAlgorithm.isSubclass(ontology, t1_1, t1_1_1));
    assertTrue(OntologyAlgorithm.isSubclass(ontology, t1_1_2, t1_1));
    assertTrue(OntologyAlgorithm.isSubclass(ontology, t2, root));
  }

  /** t1_1_1 and t1_1_2 are siblings. */
  @Test
  public void testIsSibling() {
    assertTrue(OntologyAlgorithm.termsAreSiblings(ontology, t1_1_1, t1_1_2));
    assertFalse(OntologyAlgorithm.termsAreSiblings(ontology, t1_1_1, t1_1));
    assertTrue(OntologyAlgorithm.termsAreSiblings(ontology, t2, t3));
    assertFalse(OntologyAlgorithm.termsAreSiblings(ontology, t1, root));
  }

  /** t1_1_1 and t1_2 have t1 has a common ancestor (and thus are not just connected by the root) */
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
