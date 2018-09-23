package org.monarchinitiative.phenol.ontology.algo;

import com.google.common.collect.ImmutableList;
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

  private TermId root;
  private TermId t1;
  private TermId t2;
  private TermId t3;
  private TermId t1_1;
  private TermId t1_2;
  private TermId t1_1_1;
  private TermId t1_1_2;
  private TermId t2_1;
  private TermId t2_2;
  private TermId t3_1;
  private TermId t3_2;

  /** Convenience method for making fake terms */
  private Term makeTerm(TermId tid, String name, String definition ) {
    return new Term(
      tid,
      new ArrayList<>(),
      name,
      definition,
      ImmutableList.of(),
      null,
      new ArrayList<>(),
      new ArrayList<>(),
      false,
      null,
      null,
      new ArrayList<>());
  }



  @Before
  public void setUp() {
    metaInfo = ImmutableSortedMap.of();

    root = TermId.constructWithPrefix("HP:0000001");
    t1 = TermId.constructWithPrefix("HP:0000002");
    t2 = TermId.constructWithPrefix("HP:0000003");
    t3 = TermId.constructWithPrefix("HP:0000004");
    t1_1 = TermId.constructWithPrefix("HP:0000005");
    t1_2 = TermId.constructWithPrefix("HP:0000006");
    t1_1_1 = TermId.constructWithPrefix("HP:0000007");
    t1_1_2 = TermId.constructWithPrefix("HP:0000008");
    t2_1 = TermId.constructWithPrefix("HP:0000009");
    t2_2 = TermId.constructWithPrefix("HP:0000010");
    t3_1 = TermId.constructWithPrefix("HP:0000011");
    t3_2 = TermId.constructWithPrefix("HP:0000012");

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
    // need to add corresponding relationships!
    ImmutableMap.Builder<Integer, Relationship> relationMapBuilder = ImmutableMap.builder();
    int i=1;
    relationMapBuilder.put(i, new Relationship(root, t1, i, RelationshipType.IS_A));
    i++;
    relationMapBuilder.put(i, new Relationship(root, t2, i, RelationshipType.IS_A));
    i++;
    relationMapBuilder.put(i, new Relationship(root, t3, i, RelationshipType.IS_A));
    i++;
    relationMapBuilder.put(i, new Relationship(t1, t1_1, i, RelationshipType.IS_A));
    i++;
    relationMapBuilder.put(i, new Relationship(t1, t1_2, i, RelationshipType.IS_A));
    i++;
    relationMapBuilder.put(i, new Relationship(t1_1, t1_1_1, i, RelationshipType.IS_A));
    i++;
    relationMapBuilder.put(i, new Relationship(t1_1, t1_1_2, i, RelationshipType.IS_A));
    i++;
    relationMapBuilder.put(i, new Relationship(t2, t2_1, i, RelationshipType.IS_A));
    i++;
    relationMapBuilder.put(i, new Relationship(t2, t2_2, i, RelationshipType.IS_A));
    i++;
    relationMapBuilder.put(i, new Relationship(t3, t3_1, i, RelationshipType.IS_A));
    i++;
    relationMapBuilder.put(i, new Relationship(t3, t3_2, i, RelationshipType.IS_A));
    relationMap = relationMapBuilder.build();

    rootTermId = root;

    ImmutableMap.Builder<TermId, Term> termMapBuilder = ImmutableMap.builder();
    termMapBuilder.put(root,makeTerm(rootTermId,"root","root definition"));
    termMapBuilder.put(t1, makeTerm(t1,"term1","some definition 1"));
    termMapBuilder.put(t2, makeTerm(t2,"term2","some definition 2"));
    termMapBuilder.put(t3, makeTerm(t3,"term3","some definition 3"));
    termMapBuilder.put(t1_1, makeTerm(t1_1,"term1_1","some definition 1_1"));
    termMapBuilder.put(t1_2, makeTerm(t1_2,"term1_2","some definition 1_2"));
    termMapBuilder.put(t1_1_1, makeTerm(t1_1_1,"term1_1_1","some definition 1_1_1"));
    termMapBuilder.put(t1_1_2, makeTerm(t1_1_2,"term1_1_2","some definition 1_1_2"));
    termMapBuilder.put(t2_1, makeTerm(t2_1,"term2_1","some definition 2_1"));
    termMapBuilder.put(t2_2, makeTerm(t2_2,"term2_2","some definition 2_2"));
    termMapBuilder.put(t3_1, makeTerm(t3_1,"term3_1","some definition 3_1"));
    termMapBuilder.put(t3_2, makeTerm(t3_2,"term3_2","some definition 3_2"));

    termMap = termMapBuilder.build();

    obsoleteTermMap = ImmutableMap.of();



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
