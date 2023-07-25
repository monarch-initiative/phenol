package org.monarchinitiative.phenol.graph.csr;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.graph.BaseOntologyGraphTest;
import org.monarchinitiative.phenol.graph.NodeNotPresentInGraphException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class CsrOntologyGraphTest extends BaseOntologyGraphTest {

  @Test
  public void root() {
    TermId root = GRAPH.root();
    assertThat(root.getValue(), equalTo("HP:1"));
  }

  @Nested
  public class TraversalTests {

    @ParameterizedTest
    @CsvSource({
      "HP:1, HP:01;HP:02;HP:03",
      "HP:02, HP:020;HP:021;HP:022",
      "HP:03, ''",
    })
    public void getChildren(TermId source, String payload) {
      Iterable<TermId> iterable = GRAPH.getChildren(source, false);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @ParameterizedTest
    @CsvSource({
      "HP:1, HP:1;HP:01;HP:02;HP:03",
      "HP:02, HP:02;HP:020;HP:021;HP:022",
      "HP:03, HP:03",
    })
    public void getChildrenIncludingTheSource(TermId source, String payload) {
      Iterable<TermId> iterable = GRAPH.getChildren(source, true);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @Test
    public void getChildrenUnknownSource() {
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> GRAPH.getChildren(UNKNOWN, false));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));
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
      Iterable<TermId> iterable = GRAPH.getDescendants(source, false);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @ParameterizedTest
    @CsvSource({
      "HP:1,    HP:1;HP:01;HP:010;HP:011;HP:0110; HP:02;HP:020;HP:021;HP:022; HP:03",
      "HP:01,   HP:01;HP:010;HP:011;HP:0110",
      "HP:010,  HP:010;HP:0110",
      "HP:011,  HP:011;HP:0110",
      "HP:0110, HP:0110",

      "HP:02,   HP:02;HP:020;HP:021;HP:022",
      "HP:020,  HP:020",
      "HP:021,  HP:021",
      "HP:022,  HP:022",
      "HP:03,   HP:03",
    })
    public void getDescendantsIncludingTheSource(TermId source, String payload) {
      Iterable<TermId> iterable = GRAPH.getDescendants(source, true);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @Test
    public void getDescendantsUnknownSource() {
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> GRAPH.getDescendants(UNKNOWN, false));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));
    }

    @ParameterizedTest
    @CsvSource({
      "HP:1,    ''",
      "HP:01,   HP:1",
      "HP:03,   HP:1",
      "HP:0110, HP:010;HP:011",
    })
    public void getParents(TermId source, String payload) {
      Iterable<TermId> iterable = GRAPH.getParents(source, false);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @ParameterizedTest
    @CsvSource({
      "HP:1,    HP:1",
      "HP:01,   HP:01;HP:1",
      "HP:03,   HP:03;HP:1",
      "HP:0110, HP:0110;HP:010;HP:011",
    })
    public void getParentsIncludingTheSource(TermId source, String payload) {
      Iterable<TermId> iterable = GRAPH.getParents(source, true);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @Test
    public void getParentsUnknownSource() {
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> GRAPH.getParents(UNKNOWN, false));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));
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
      Iterable<TermId> iterable = GRAPH.getAncestors(source, false);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @ParameterizedTest
    @CsvSource({
      "HP:1,    HP:1",
      "HP:01,   HP:01;HP:1",
      "HP:0110, HP:0110;HP:010;HP:011;HP:01;HP:1",
      "HP:022,  HP:022;HP:02;HP:1",
      "HP:03,   HP:03;HP:1",
    })
    public void getAncestorsIncludingTheSource(TermId source, String payload) {
      Iterable<TermId> iterable = GRAPH.getAncestors(source, true);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @Test
    public void getAncestorsUnknownSource() {
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> GRAPH.getAncestors(UNKNOWN, false));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));
    }

    private Set<TermId> parsePayload(String payload) {
      return payload.isBlank()
        ? new HashSet<>()
        : Arrays.stream(payload.split(";"))
        .map(String::trim)
        .map(TermId::of)
        .collect(Collectors.toCollection(HashSet::new));
    }

  }

  @ParameterizedTest
  @CsvSource({
    "HP:1,    false",

    "HP:01,   false",
    "HP:010,  false",
    "HP:011,  false",
    "HP:0110, true",

    "HP:02,   false",
    "HP:020,  true",
    "HP:021,  true",
    "HP:022,  true",

    "HP:03,   true",
  })
  public void isLeaf(TermId source, boolean expected) {
    assertThat(GRAPH.isLeaf(source), equalTo(expected));
  }

  @Test
  public void isLeaf_unknownSource() {
    NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> GRAPH.isLeaf(UNKNOWN));
    assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));
  }

  @Test
  public void iterator() {
    List<TermId> expected = Stream.of(
        "HP:01", "HP:010", "HP:011", "HP:0110",
        "HP:02", "HP:020", "HP:021", "HP:022",
        "HP:03", "HP:1")
      .map(TermId::of)
      .collect(Collectors.toList());

    iterableContainsTheExpectedItems(GRAPH, expected);
  }

  /**
   * Test that the {@code iterator} yields a sequence of unique elements from the {@code expected} collection.
   */
  private static <T> void iterableContainsTheExpectedItems(Iterable<T> iterable, Collection<T> expected) {
    List<T> list = new ArrayList<>();
    iterable.forEach(list::add);
    HashSet<T> set = new HashSet<>(list);
    assertThat("The iterator must yield no duplicates", set.size(), equalTo(list.size()));
    assertTrue(set.containsAll(expected));
    assertThat(set, hasSize(expected.size()));
  }
}
