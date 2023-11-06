package org.monarchinitiative.phenol.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * The test suite that checks that an implementation of {@link OntologyGraph} meets the specification.
 */
public abstract class OntologyGraphTest {

  private static final TermId UNKNOWN = TermId.of("HP:999");

  private OntologyGraph<TermId> graph;

  /**
   * Get an ontology graph that should be tested.
   */
  protected abstract OntologyGraph<TermId> getGraph();

  @BeforeEach
  public void setUp() {
    graph = getGraph();
  }

  @Test
  public void root() {
    TermId root = graph.root();
    assertThat(root.getValue(), equalTo("HP:1"));
  }

  @Nested
  public class ChildrenTraversal {

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
      "HP:1, HP:1;HP:01;HP:02;HP:03",
      "HP:02, HP:02;HP:020;HP:021;HP:022",
      "HP:03, HP:03",
    })
    public void getChildrenIncludingTheSource(TermId source, String payload) {
      Iterable<TermId> iterable = graph.getChildren(source, true);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @Test
    public void getChildrenUnknownSource() {
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> graph.getChildren(UNKNOWN, false));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));
    }

    @ParameterizedTest
    @CsvSource({
      // True examples
      "HP:01, HP:1, true",

      "HP:010, HP:01, true",
      "HP:011, HP:01, true",
      "HP:0110, HP:011, true",

      "HP:02, HP:1, true",
      "HP:020, HP:02, true",
      "HP:021, HP:02, true",
      "HP:022, HP:02, true",

      "HP:03, HP:1, true",

      // False examples
      "HP:1, HP:1, false",
      "HP:01, HP:02, false",
      "HP:020, HP:01, false",
      "HP:03, HP:0110, false",
    })
    public void isChildOf(TermId subject, TermId object, boolean expected) {
      assertThat(graph.isChildOf(subject, object), equalTo(expected));
    }

    @Test
    public void isChildOfUnknownSource() {
      TermId ok = TermId.of("HP:01");
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> graph.isChildOf(ok, UNKNOWN));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));

      assertThat(graph.isChildOf(UNKNOWN, ok), equalTo(false));
    }
  }

  @Nested
  public class DescendantTraversal {

    @ParameterizedTest
    @CsvSource({
      // True examples
      "HP:010, HP:1, true",
      "HP:011, HP:1, true",
      "HP:0110, HP:1, true",
      "HP:0110, HP:01, true",

      "HP:020, HP:1, true",
      "HP:021, HP:1, true",
      "HP:022, HP:1, true",

      // False examples
      "HP:1, HP:1, false",
      "HP:1, HP:01, false",
    })
    public void isDescendantOf(TermId subject, TermId object, boolean expected) {
      assertThat(graph.isDescendantOf(subject, object), equalTo(expected));
    }

    @Test
    public void isDescendantOfUnknownSource() {
      TermId ok = TermId.of("HP:01");
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> graph.isDescendantOf(ok, UNKNOWN));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));

      assertThat(graph.isDescendantOf(UNKNOWN, ok), equalTo(false));
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
      Iterable<TermId> iterable = graph.getDescendants(source, true);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @Test
    public void getDescendantsUnknownSource() {
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> graph.getDescendants(UNKNOWN, false));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));
    }

  }

  @Nested
  public class ParentsTraversal {

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
      "HP:1,    HP:1",
      "HP:01,   HP:01;HP:1",
      "HP:03,   HP:03;HP:1",
      "HP:0110, HP:0110;HP:010;HP:011",
    })
    public void getParentsIncludingTheSource(TermId source, String payload) {
      Iterable<TermId> iterable = graph.getParents(source, true);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @Test
    public void getParentsUnknownSource() {
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> graph.getParents(UNKNOWN, false));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));
    }

    @ParameterizedTest
    @CsvSource({
      // True examples
      "HP:1, HP:01, true",

      "HP:01, HP:010, true",
      "HP:01, HP:011, true",
      "HP:011, HP:0110, true",

      "HP:1, HP:02, true",
      "HP:02, HP:020, true",
      "HP:02, HP:021, true",
      "HP:02, HP:022, true",

      "HP:1, HP:03, true",

      // False examples
      "HP:1, HP:1, false",
      "HP:02, HP:01, false",
      "HP:01, HP:020, false",
      "HP:0110, HP:03, false",
    })
    public void isParentOf(TermId subject, TermId object, boolean expected) {
      assertThat(graph.isParentOf(subject, object), equalTo(expected));
    }

    @Test
    public void isParentOfUnknownSource() {
      TermId ok = TermId.of("HP:01");
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> graph.isParentOf(ok, UNKNOWN));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));

      assertThat(graph.isParentOf(UNKNOWN, ok), equalTo(false));
    }

  }

  @Nested
  public class AncestorTraversal {

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

    @ParameterizedTest
    @CsvSource({
      "HP:1,    HP:1",
      "HP:01,   HP:01;HP:1",
      "HP:0110, HP:0110;HP:010;HP:011;HP:01;HP:1",
      "HP:022,  HP:022;HP:02;HP:1",
      "HP:03,   HP:03;HP:1",
    })
    public void getAncestorsIncludingTheSource(TermId source, String payload) {
      Iterable<TermId> iterable = graph.getAncestors(source, true);
      Set<TermId> expected = parsePayload(payload);

      iterableContainsTheExpectedItems(iterable, expected);
    }

    @ParameterizedTest
    @CsvSource({
      // True examples
      "HP:1, HP:010, true",
      "HP:1, HP:011, true",
      "HP:1, HP:0110, true",
      "HP:01, HP:0110, true",

      "HP:1, HP:020, true",
      "HP:1, HP:021, true",
      "HP:1, HP:022, true",

      // False examples
      "HP:1, HP:1, false",
      "HP:01, HP:1, false",
    })
    public void isAncestorOf(TermId subject, TermId object, boolean expected) {
      assertThat(graph.isAncestorOf(subject, object), equalTo(expected));
    }

    @Test
    public void isAncestorOfUnknownSource() {
      TermId ok = TermId.of("HP:01");
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> graph.isAncestorOf(ok, UNKNOWN));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));

      assertThat(graph.isAncestorOf(UNKNOWN, ok), equalTo(false));
    }

    @Test
    public void getAncestorsUnknownSource() {
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> graph.getAncestors(UNKNOWN, false));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));
    }

  }

  @Nested
  public class TraversalTests {
    @ParameterizedTest
    @CsvSource({
      // True examples
      "HP:010, HP:1, true",
      "HP:011, HP:1, true",
      "HP:0110, HP:1, true",
      "HP:0110, HP:01, true",

      "HP:020, HP:1, true",
      "HP:021, HP:1, true",
      "HP:022, HP:1, true",

      // False examples
      "HP:1, HP:1, false",
      "HP:1, HP:01, false",
    })
    public void existsPath(TermId subject, TermId object, boolean expected) {
      assertThat(graph.existsPath(subject, object), equalTo(expected));
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
      assertThat(graph.isLeaf(source), equalTo(expected));
    }

    @Test
    public void isLeaf_unknownSource() {
      NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> graph.isLeaf(UNKNOWN));
      assertThat(e.getMessage(), equalTo("Item not found in the graph: HP:999"));
    }

  }

  @Nested
  public class SubOntologyClasses {

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
    public void extractSubgraph(TermId root, String expected) {
      OntologyGraph<TermId> subgraph = graph.extractSubgraph(root);

      assertThat(subgraph.root(), equalTo(root));
      Set<TermId> expectedIds = parsePayload(expected);
      iterableContainsTheExpectedItems(subgraph, expectedIds);
      assertThat(subgraph.size(), equalTo(expectedIds.size()));
      assertThat(subgraph.getParents(root, false), is(emptyIterableOf(TermId.class)));
    }

  }

  @Test
  public void iterator() {
    List<TermId> expected = Stream.of(
        "HP:01", "HP:010", "HP:011", "HP:0110",
        "HP:02", "HP:020", "HP:021", "HP:022",
        "HP:03", "HP:1")
      .map(TermId::of)
      .collect(Collectors.toList());

    iterableContainsTheExpectedItems(graph, expected);
  }

  @Test
  public void size() {
    assertThat(graph.size(), is(10));
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
  private static <T> void iterableContainsTheExpectedItems(Iterable<T> iterable, Collection<T> expected) {
    List<T> list = new ArrayList<>();
    iterable.forEach(list::add);
    HashSet<T> set = new HashSet<>(list);
    assertThat("The iterator must yield no duplicates", set.size(), equalTo(list.size()));
    assertTrue(set.containsAll(expected));
    assertThat(set, hasSize(expected.size()));

    // Now let's check one more time to ensure the iterable can be actually iterated over >1 times!
    list.clear();
    iterable.forEach(list::add);
    assertThat("The iterator must yield no duplicates", set.size(), equalTo(list.size()));
    assertTrue(set.containsAll(expected));
    assertThat(set, hasSize(expected.size()));
  }

}
