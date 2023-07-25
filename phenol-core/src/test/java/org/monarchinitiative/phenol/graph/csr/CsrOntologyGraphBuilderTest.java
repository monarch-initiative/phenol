package org.monarchinitiative.phenol.graph.csr;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.graph.OntologyGraphEdges;
import org.monarchinitiative.phenol.graph.RelationTypes;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsrOntologyGraphBuilderTest {

  private static final TermId ROOT = TermId.of("HP:1");

  @Nested
  public class OneEdgeType {

    @Test
    public void build() {
      CsrOntologyGraphBuilder<Byte> builder = CsrOntologyGraphBuilder.builder(Byte.class);
      CsrOntologyGraph<TermId, Byte> graph = builder.build(ROOT, OntologyGraphEdges.hierarchyEdges());

      assertThat(graph.root(), equalTo(ROOT));

      // Check the adjacency matrix
      ImmutableCsrMatrix<Byte> adjacencyMatrix = graph.adjacencyMatrix();
      assertThat(adjacencyMatrix.indptr(), equalTo(new int[]{0, 3, 5, 7, 9, 13, 14, 15, 16, 17, 20}));
      assertThat(adjacencyMatrix.indices(), equalTo(new int[]{1, 2, 9, 0, 3, 0, 3, 1, 2, 5, 6, 7, 9, 4, 4, 4, 9, 0, 4, 8}));
      assertThat(adjacencyMatrix.data(), equalTo(new byte[]{2, 2, 1, 1, 2, 1, 2, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 2, 2, 2}));
    }
  }

  @Nested
  public class MoreEdgeTypes {

    @Test
    public void build() {
      CsrOntologyGraphBuilder<Byte> builder = CsrOntologyGraphBuilder.builder(Byte.class);
      CsrOntologyGraph<TermId, Byte> graph = builder.build(ROOT, OntologyGraphEdges.makeHierarchyAndPartOfEdges());

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
        0b10, 0b10, 0b01,
        0b1, 0b10,
        0b01, 0b10, 0b1000,
        0b1, 0b1, 0b1000,
        0b10, 0b10, 0b10, 0b1,
        0b1,
        0b1,
        0b1,
        0b1,
        0b10, 0b10, 0b10, 0b1000,
        0b100,
        0b100,
        0b100}));
    }

  }

  @Nested
  public class TraversalTests {

    private final CsrOntologyGraph<TermId, Byte> graph = buildExampleGraph();

    @ParameterizedTest
    @CsvSource({
      "HP:1, HP:01;HP:02;HP:03",
      "HP:02, HP:020;HP:021;HP:022",
      "HP:03, ''",
    })
    public void getChildren(TermId source, String payload) {
      Iterable<TermId> iterable = graph.getChildren(source, false);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @ParameterizedTest
    @CsvSource({
      "HP:1,    HP:01;HP:010;HP:011;HP:0110; HP:02;HP:020;HP:021;HP:022; HP:03",
      "HP:01,   HP:010;HP:011;HP:0110",
      "HP:010,  HP:0110",
      "HP:011,  HP:0110",
      "HP:0110, ''",

      "HP:02,   HP:020;HP:021;HP:022",
      "HP:020,  ''",
      "HP:021,  ''",
      "HP:022,  ''",
      "HP:03,   ''",
    })
    public void getDescendants(TermId source, String payload) {
      Iterable<TermId> iterable = graph.getDescendants(source, false);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @ParameterizedTest
    @CsvSource({
      "HP:1,    ''",
      "HP:01,   HP:1",
      "HP:03,   HP:1",
      "HP:0110, HP:010;HP:011",
    })
    public void getParents(TermId source, String payload) {
      Iterable<TermId> iterable = graph.getParents(source, false);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @ParameterizedTest
    @CsvSource({
      "HP:1,    ''",
      "HP:01,   HP:1",
      "HP:0110, HP:010;HP:011;HP:01;HP:1",
      "HP:022,  HP:02;HP:1",
      "HP:03,   HP:1",
    })
    public void getAncestors(TermId source, String payload) {
      Iterable<TermId> iterable = graph.getAncestors(source, false);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    private CsrOntologyGraph<TermId, Byte> buildExampleGraph() {
      return CsrOntologyGraphBuilder.builder(Byte.class)
        .hierarchyRelation(RelationTypes.isA())
        .build(ROOT, OntologyGraphEdges.hierarchyEdges());
    }


    private Set<TermId> parsePayload(String payload) {
      return payload.isBlank()
        ? new HashSet<>()
        : Arrays.stream(payload.split(";"))
        .map(String::trim)
        .map(TermId::of)
        .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Test that the {@code iterator} yields a sequence of unique elements from the {@code expected} collection.
     */
    private <T> void iterableContainsTheExpectedItems(Iterable<T> iterable, Collection<T> expected) {
      List<T> list = new ArrayList<>();
      iterable.forEach(list::add);
      HashSet<T> set = new HashSet<>(list);
      assertThat("The iterator must yield no duplicates", set.size(), equalTo(list.size()));
      assertTrue(set.containsAll(expected));
      assertThat(set, hasSize(expected.size()));
    }
  }
}
