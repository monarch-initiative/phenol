package org.monarchinitiative.phenol.graph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.monarchinitiative.phenol.ontology.data.TermId;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class OntologyGraphTest extends BaseOntologyGraphTest {

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
    assertThat(GRAPH.isChildOf(subject, object), equalTo(expected));
  }

  @Test
  public void isChildOfUnknownSource() {
    TermId ok = TermId.of("HP:01");
    NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> GRAPH.isChildOf(ok, UNKNOWN));
    assertThat(e.getMessage(), equalTo("Term ID not found in the graph: HP:999"));

    assertThat(GRAPH.isChildOf(UNKNOWN, ok), equalTo(false));
  }

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
    assertThat(GRAPH.isDescendantOf(subject, object), equalTo(expected));
  }


  @Test
  public void isDescendantOfUnknownSource() {
    TermId ok = TermId.of("HP:01");
    NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> GRAPH.isDescendantOf(ok, UNKNOWN));
    assertThat(e.getMessage(), equalTo("Term ID not found in the graph: HP:999"));

    assertThat(GRAPH.isDescendantOf(UNKNOWN, ok), equalTo(false));
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
    assertThat(GRAPH.isParentOf(subject, object), equalTo(expected));
  }

  @Test
  public void isParentOfUnknownSource() {
    TermId ok = TermId.of("HP:01");
    NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> GRAPH.isParentOf(ok, UNKNOWN));
    assertThat(e.getMessage(), equalTo("Term ID not found in the graph: HP:999"));

    assertThat(GRAPH.isParentOf(UNKNOWN, ok), equalTo(false));
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
    assertThat(GRAPH.isAncestorOf(subject, object), equalTo(expected));
  }


  @Test
  public void isAncestorOfUnknownSource() {
    TermId ok = TermId.of("HP:01");
    NodeNotPresentInGraphException e = assertThrows(NodeNotPresentInGraphException.class, () -> GRAPH.isAncestorOf(ok, UNKNOWN));
    assertThat(e.getMessage(), equalTo("Term ID not found in the graph: HP:999"));

    assertThat(GRAPH.isAncestorOf(UNKNOWN, ok), equalTo(false));
  }

}
