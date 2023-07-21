package org.monarchinitiative.phenol.graph.csr;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.graph.OntologyGraphEdge;
import org.monarchinitiative.phenol.graph.OntologyGraphEdges;
import org.monarchinitiative.phenol.ontology.data.RelationshipType;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CsrOntologyGraphBuilderTest {


  @Nested
  public class OneEdgeType {



    @Test
    public void build() {
      CsrOntologyGraphBuilder<Byte> builder = CsrOntologyGraphBuilder.builder(Byte.class);
      CsrOntologyGraph<TermId, Byte> graph = builder.build(OntologyGraphEdges.hierarchyEdges());

      assertThat(graph.root(), equalTo(TermId.of("HP:1")));

      // Check the adjacency matrix
      ImmutableCsrMatrix<Byte> adjacencyMatrix = graph.adjacencyMatrix();
      assertThat(adjacencyMatrix.indptr(), equalTo(new int[]{0, 3, 5, 7, 9, 13, 14, 15, 16, 17, 20}));
      assertThat(adjacencyMatrix.indices(), equalTo(new int[]{1, 2, 9, 0, 3, 0, 3, 1, 2, 5, 6, 7, 9, 4, 4, 4, 9, 0, 4, 8}));
      assertThat(adjacencyMatrix.data(), equalTo(new byte[]{1, 1, 2, 2, 1, 2, 1, 2, 2, 1, 1, 1, 2, 2, 2, 2, 2, 1, 1, 1}));
    }
  }

  @Nested
  public class MoreEdgeTypes {

    @Test
    public void build() {
      CsrOntologyGraphBuilder<Byte> builder = CsrOntologyGraphBuilder.builder(Byte.class);
      CsrOntologyGraph<TermId, Byte> graph = builder.build(OntologyGraphEdges.makeHierarchyAndPartOfEdges());

      assertThat(graph.root(), equalTo(TermId.of("HP:1")));

      // Check the adjacency matrix
      ImmutableCsrMatrix<Byte> adjacencyMatrix = graph.adjacencyMatrix();

      assertThat(adjacencyMatrix.indptr(), equalTo(new int[]{0, 3, 5, 8, 11, 15, 16, 17, 18, 19, 23, 24, 25, 26}));
      assertThat(adjacencyMatrix.indices(), equalTo(new int[]{
        1, 2, 9,
        0, 3,
        0, 3, 10,
        1, 2, 11,
        5, 6, 7, 9,
        4,
        4,
        4,
        9,
        0, 4, 8, 12,
        2,
        3,
        9}));
      assertThat(adjacencyMatrix.data(), equalTo(new byte[]{
        0b1, 0b1, 0b10,
        0b10, 0b1,
        0b10, 0b1, 0b100,
        0b10, 0b10, 0b100,
        0b1, 0b1, 0b1, 0b10,
        0b10,
        0b10,
        0b10,
        0b10,
        0b1, 0b1, 0b1, 0b100,
        0b1000,
        0b1000,
        0b1000}));
    }

  }

}
