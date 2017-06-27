package de.charite.compbio.ontolib.ontology.data;

import java.util.ArrayList;

import org.junit.Before;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;

public class ImmutableOntologyTestBase {

  ImmutableSortedMap<String, String> metaInfo;

  ImmutableList<TermId> vertices;
  ImmutableList<ImmutableEdge<TermId>> edges;
  ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph;

  TermId rootTermId;
  ImmutableMap<TermId, TestTerm> termMap;
  ImmutableMap<TermId, TestTerm> obsoleteTermMap;
  ImmutableMap<Integer, TestTermRelation> relationMap;

  ImmutableOntology<TestTerm, TestTermRelation> ontology;

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
    edges =
        ImmutableList.of(ImmutableEdge.construct(id1, id2, 1), ImmutableEdge.construct(id1, id3, 2),
            ImmutableEdge.construct(id1, id4, 3), ImmutableEdge.construct(id2, id5, 4),
            ImmutableEdge.construct(id3, id5, 5), ImmutableEdge.construct(id4, id5, 6));
    graph = ImmutableDirectedGraph.construct(edges);

    rootTermId = id5;

    ImmutableMap.Builder<TermId, TestTerm> termMapBuilder = ImmutableMap.builder();
    termMapBuilder.put(id1, new TestTerm(id1, new ArrayList<>(), "term1", "some definition 1", null,
        new ArrayList<>(), new ArrayList<>(), false, null, null));
    termMapBuilder.put(id2, new TestTerm(id2, new ArrayList<>(), "term2", "some definition 2", null,
        new ArrayList<>(), new ArrayList<>(), false, null, null));
    termMapBuilder.put(id3, new TestTerm(id3, new ArrayList<>(), "term3", "some definition 3", null,
        new ArrayList<>(), new ArrayList<>(), false, null, null));
    termMapBuilder.put(id4, new TestTerm(id4, new ArrayList<>(), "term4", "some definition 4", null,
        new ArrayList<>(), new ArrayList<>(), false, null, null));
    termMapBuilder.put(id5, new TestTerm(id5, new ArrayList<>(), "term5", "some definition 5", null,
        new ArrayList<>(), new ArrayList<>(), false, null, null));
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

    ontology = new ImmutableOntology<TestTerm, TestTermRelation>(metaInfo, graph, rootTermId,
        termMap.keySet(), obsoleteTermMap.keySet(), termMap, obsoleteTermMap, relationMap);
  }

}
