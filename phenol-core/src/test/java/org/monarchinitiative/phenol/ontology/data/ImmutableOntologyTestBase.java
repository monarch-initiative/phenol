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

  private ImmutableSortedMap<String, String> metaInfo;

  private ImmutableList<TermId> vertices;
  private ImmutableList<IdLabeledEdge> edges;
  private DefaultDirectedGraph<TermId, IdLabeledEdge> graph;

  private TermId rootTermId;
  private ImmutableMap<TermId, Term> termMap;
  private ImmutableMap<TermId, Term> obsoleteTermMap;
  private ImmutableMap<Integer, Relationship> relationMap;

  protected ImmutableOntology ontology;

  protected TermId id1;
  protected TermId id2;
  protected TermId id3;
  protected TermId id4;
  protected TermId id5;

  @Before
  public void setUp() {
    metaInfo = ImmutableSortedMap.of();

    id1 = TermId.constructWithPrefix("HP:0000001");
    id2 = TermId.constructWithPrefix("HP:0000002");
    id3 = TermId.constructWithPrefix("HP:0000003");
    id4 = TermId.constructWithPrefix("HP:0000004");
    id5 = TermId.constructWithPrefix("HP:0000005");
    vertices = ImmutableList.of(id1, id2, id3, id4, id5);

    DefaultDirectedGraph<TermId, IdLabeledEdge> graph = new DefaultDirectedGraph<>(IdLabeledEdge.class);
    GraphUtil.addEdgeToGraph(graph, id1, id2, 1);
    GraphUtil.addEdgeToGraph(graph, id1, id3, 2);
    GraphUtil.addEdgeToGraph(graph, id1, id4, 3);
    GraphUtil.addEdgeToGraph(graph, id2, id5, 4);
    GraphUtil.addEdgeToGraph(graph, id3, id5, 5);
    GraphUtil.addEdgeToGraph(graph, id4, id5, 6);

    rootTermId = id5;

    ImmutableMap.Builder<TermId, Term> termMapBuilder = ImmutableMap.builder();
    termMapBuilder.put(
      id1,
      Term.builder()
        .id(id1)
        .name("term1")
        .definition("some definition 1")
        .build()
    );
    termMapBuilder.put(
      id2,
      Term.builder()
        .id(id2)
        .name("term2")
        .definition("some definition 2")
        .build()
    );
    termMapBuilder.put(
      id3,
      Term.builder()
        .id(id3)
        .name("term3")
        .definition("some definition 3")
        .build()
    );
    termMapBuilder.put(
      id4,
      Term.builder()
        .id(id4)
        .name("term4")
        .definition("some definition 4")
        .build()
    );
    termMapBuilder.put(
      id5,
      Term.builder()
        .id(id5)
        .name("term5")
        .definition("some definition 5")
        .build()
    );
    termMap = termMapBuilder.build();

    obsoleteTermMap = ImmutableMap.of();

    ImmutableMap.Builder<Integer, Relationship> relationMapBuilder = ImmutableMap.builder();
    relationMapBuilder.put(1, new Relationship(id1, id2, 1, RelationshipType.IS_A));
    relationMapBuilder.put(2, new Relationship(id1, id3, 2,RelationshipType.IS_A));
    relationMapBuilder.put(3, new Relationship(id1, id4, 3,RelationshipType.IS_A));
    relationMapBuilder.put(4, new Relationship(id2, id5, 4,RelationshipType.IS_A));
    relationMapBuilder.put(5, new Relationship(id3, id5, 5,RelationshipType.IS_A));
    relationMapBuilder.put(6, new Relationship(id4, id5, 6,RelationshipType.IS_A));
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
}
