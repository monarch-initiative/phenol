package org.monarchinitiative.phenol.ontology.data;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.util.GraphUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

/**
 * Base/prerequisite codes for testing ImmutableOntology.
 *
 * @author Unknowns
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class ImmutableOntologyTestBase {

  ImmutableSortedMap<String, String> metaInfo;

  ImmutableList<TermId> vertices;
  ImmutableList<IdLabeledEdge> edges;
  DefaultDirectedGraph<TermId, IdLabeledEdge> graph;

  TermId rootTermId;
  ImmutableMap<TermId, TestTerm> termMap;
  ImmutableMap<TermId, TestTerm> obsoleteTermMap;
  ImmutableMap<Integer, TestRelationship> relationMap;

  ImmutableOntology<TestTerm, TestRelationship> ontology;

  ImmutableTermId id1;
  ImmutableTermId id2;
  ImmutableTermId id3;
  ImmutableTermId id4;
  ImmutableTermId id5;

  @Before
  public void setUp() {
    metaInfo = ImmutableSortedMap.of();

    id1 = ImmutableTermId.constructWithPrefix("HP:0000001");
    id2 = ImmutableTermId.constructWithPrefix("HP:0000002");
    id3 = ImmutableTermId.constructWithPrefix("HP:0000003");
    id4 = ImmutableTermId.constructWithPrefix("HP:0000004");
    id5 = ImmutableTermId.constructWithPrefix("HP:0000005");
    vertices = ImmutableList.of(id1, id2, id3, id4, id5);

    DefaultDirectedGraph<TermId, IdLabeledEdge> graph =
        new DefaultDirectedGraph<TermId, IdLabeledEdge>(IdLabeledEdge.class);
    GraphUtil.addEdgeToGraph(graph, id1, id2, 1);
    GraphUtil.addEdgeToGraph(graph, id1, id3, 2);
    GraphUtil.addEdgeToGraph(graph, id1, id4, 3);
    GraphUtil.addEdgeToGraph(graph, id2, id5, 4);
    GraphUtil.addEdgeToGraph(graph, id3, id5, 5);
    GraphUtil.addEdgeToGraph(graph, id4, id5, 6);

    rootTermId = id5;

    ImmutableMap.Builder<TermId, TestTerm> termMapBuilder = ImmutableMap.builder();
    termMapBuilder.put(
        id1,
        new TestTerm(
            id1,
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
        id2,
        new TestTerm(
            id2,
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
        id3,
        new TestTerm(
            id3,
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
        id4,
        new TestTerm(
            id4,
            new ArrayList<>(),
            "term4",
            "some definition 4",
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        id5,
        new TestTerm(
            id5,
            new ArrayList<>(),
            "term5",
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

    ImmutableMap.Builder<Integer, TestRelationship> relationMapBuilder = ImmutableMap.builder();
    relationMapBuilder.put(1, new TestRelationship(id1, id2, 1));
    relationMapBuilder.put(2, new TestRelationship(id1, id3, 2));
    relationMapBuilder.put(3, new TestRelationship(id1, id4, 3));
    relationMapBuilder.put(4, new TestRelationship(id2, id5, 4));
    relationMapBuilder.put(5, new TestRelationship(id3, id5, 5));
    relationMapBuilder.put(6, new TestRelationship(id4, id5, 6));
    relationMap = relationMapBuilder.build();

    ontology =
        new ImmutableOntology<TestTerm, TestRelationship>(
            metaInfo,
            graph,
            rootTermId,
            termMap.keySet(),
            obsoleteTermMap.keySet(),
            termMap,
            relationMap);
  }
}
