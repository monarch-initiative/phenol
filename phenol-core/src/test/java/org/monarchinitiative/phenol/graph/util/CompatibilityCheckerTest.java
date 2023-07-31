package org.monarchinitiative.phenol.graph.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.graph.OntologyGraphEdge;
import org.monarchinitiative.phenol.graph.OntologyGraphEdges;
import org.monarchinitiative.phenol.graph.RelationTypes;
import org.monarchinitiative.phenol.graph.exc.GraphNotConnectedException;
import org.monarchinitiative.phenol.graph.exc.GraphNotSimpleException;
import org.monarchinitiative.phenol.graph.exc.VerticesAndEdgesIncompatibleException;
import org.monarchinitiative.phenol.ontology.data.RelationshipType;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class CompatibilityCheckerTest {

  @Test
  public void checkCompatibility() {
    List<TermId> vertices = OntologyGraphEdges.VERTICES;
    List<OntologyGraphEdge<TermId>> edges = OntologyGraphEdges.hierarchyEdges();
    assertThat(CompatibilityChecker.checkCompatibility(vertices, edges), equalTo(true));
  }

  @Test
  public void emptyVerticesOrEdgesThrow() {
    VerticesAndEdgesIncompatibleException e = assertThrows(VerticesAndEdgesIncompatibleException.class, () -> CompatibilityChecker.checkCompatibility(List.of(), OntologyGraphEdges.hierarchyEdges()));
    assertThat(e.getMessage(), equalTo("Collection of vertices (0) or edges (10) must not be empty!"));
    e = assertThrows(VerticesAndEdgesIncompatibleException.class, () -> CompatibilityChecker.checkCompatibility(OntologyGraphEdges.VERTICES, List.of()));
    assertThat(e.getMessage(), equalTo("Collection of vertices (10) or edges (0) must not be empty!"));
  }

  @Test
  public void selfLoopThrows() {
    List<TermId> vertices = OntologyGraphEdges.VERTICES;
    List<OntologyGraphEdge<TermId>> edges = new ArrayList<>(OntologyGraphEdges.hierarchyEdges());
    edges.add(OntologyGraphEdge.of(OntologyGraphEdges.HP01, OntologyGraphEdges.HP01, RelationTypes.isA()));
    GraphNotSimpleException e = assertThrows(GraphNotSimpleException.class, () -> CompatibilityChecker.checkCompatibility(vertices, edges));
    assertThat(e.getMessage(), equalTo("Self-loop edge for HP:01"));
  }

  @Test
  public void multiEdgeThrows() {
    List<TermId> vertices = OntologyGraphEdges.VERTICES;
    List<OntologyGraphEdge<TermId>> edges = new ArrayList<>(OntologyGraphEdges.hierarchyEdges());
    edges.add(OntologyGraphEdge.of(OntologyGraphEdges.HP01, OntologyGraphEdges.HP1, RelationshipType.IS_A));
    GraphNotSimpleException e = assertThrows(GraphNotSimpleException.class, () -> CompatibilityChecker.checkCompatibility(vertices, edges));
    assertThat(e.getMessage(), equalTo("Seen edge twice: OntologyGraphEdgeDefault{subject=HP:01, object=HP:1, relationshipType=RelationshipType{id='is_a', label='is_a'}}"));
  }

  @Test
  public void unknownSubjectVertexThrows() {
    List<TermId> vertices = OntologyGraphEdges.VERTICES;
    List<OntologyGraphEdge<TermId>> edges = new ArrayList<>(OntologyGraphEdges.hierarchyEdges());
    edges.add(OntologyGraphEdge.of(TermId.of("HP:999"), OntologyGraphEdges.HP01, RelationshipType.IS_A));
    VerticesAndEdgesIncompatibleException e = assertThrows(VerticesAndEdgesIncompatibleException.class, () -> CompatibilityChecker.checkCompatibility(vertices, edges));
    assertThat(e.getMessage(), equalTo("Unknown subject HP:999 in edge OntologyGraphEdgeDefault{subject=HP:999, object=HP:01, relationshipType=RelationshipType{id='is_a', label='is_a'}}"));
  }

  @Test
  public void unknownObjectVertexThrows() {
    List<TermId> vertices = OntologyGraphEdges.VERTICES;
    List<OntologyGraphEdge<TermId>> edges = new ArrayList<>(OntologyGraphEdges.hierarchyEdges());
    edges.add(OntologyGraphEdge.of(OntologyGraphEdges.HP01, TermId.of("HP:999"), RelationshipType.IS_A));
    VerticesAndEdgesIncompatibleException e = assertThrows(VerticesAndEdgesIncompatibleException.class, () -> CompatibilityChecker.checkCompatibility(vertices, edges));
    assertThat(e.getMessage(), equalTo("Unknown object HP:999 in edge OntologyGraphEdgeDefault{subject=HP:01, object=HP:999, relationshipType=RelationshipType{id='is_a', label='is_a'}}"));
  }

  @Test
  public void checkCompatibility_disconnectedVertex() {
    List<TermId> vertices = new ArrayList<>(OntologyGraphEdges.VERTICES);
    vertices.add(TermId.of("HP:999"));
    vertices.add(TermId.of("HP:998"));
    List<OntologyGraphEdge<TermId>> edges = OntologyGraphEdges.hierarchyEdges();
    GraphNotConnectedException e = assertThrows(GraphNotConnectedException.class, () -> CompatibilityChecker.checkCompatibility(vertices, edges));
    assertThat(e.getMessage(), equalTo("Found 2 disconnected vertices: {HP:998, HP:999}"));
  }

  @Test
  public void checkCompatibility_twoConnectedComponents() {
    List<TermId> vertices = new ArrayList<>(OntologyGraphEdges.VERTICES);
    List<OntologyGraphEdge<TermId>> edges = new ArrayList<>(OntologyGraphEdges.hierarchyEdges());

    TermId HP998 = TermId.of("HP:998");
    TermId HP999 = TermId.of("HP:999");
    vertices.add(HP999);
    vertices.add(HP998);
    edges.add(OntologyGraphEdge.of(HP998, HP999, RelationTypes.isA()));

    GraphNotConnectedException e = assertThrows(GraphNotConnectedException.class,
      () -> CompatibilityChecker.checkCompatibility(vertices, edges));
    assertThat(e.getMessage(), equalTo("Found 2 disconnected vertices: {HP:998, HP:999}"));
  }

  @Test
  @Disabled("Deterministic cycle detection does not work at the moment")
  public void checkCompatibility_disconnectedCycle() {
    List<TermId> vertices = new ArrayList<>(OntologyGraphEdges.VERTICES);
    List<OntologyGraphEdge<TermId>> edges = new ArrayList<>(OntologyGraphEdges.hierarchyEdges());

    TermId HP997 = TermId.of("HP:997");
    TermId HP998 = TermId.of("HP:998");
    TermId HP999 = TermId.of("HP:999");
    vertices.add(HP997);
    vertices.add(HP998);
    vertices.add(HP999);
    // add a cycle
    edges.add(OntologyGraphEdge.of(HP997, HP998, RelationTypes.isA()));
    edges.add(OntologyGraphEdge.of(HP998, HP999, RelationTypes.isA()));
    edges.add(OntologyGraphEdge.of(HP999, HP997, RelationTypes.isA()));

    GraphNotConnectedException e = assertThrows(GraphNotConnectedException.class,
      () -> CompatibilityChecker.checkCompatibility(vertices, edges));
    assertThat(e.getMessage(), equalTo("Found 2 disconnected vertices: {HP:997, HP:998, HP:999}"));
  }
}
