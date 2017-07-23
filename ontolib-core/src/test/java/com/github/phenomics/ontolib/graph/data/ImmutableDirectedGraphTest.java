package com.github.phenomics.ontolib.graph.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.phenomics.ontolib.graph.data.DirectedGraph;
import com.github.phenomics.ontolib.graph.data.ImmutableDirectedGraph;
import com.github.phenomics.ontolib.graph.data.ImmutableEdge;
import com.github.phenomics.ontolib.graph.data.ImmutableDirectedGraph.Builder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Before;
import org.junit.Test;

public class ImmutableDirectedGraphTest {

  private ImmutableList<Integer> vertices;
  private ImmutableList<ImmutableEdge<Integer>> edges;
  private ImmutableDirectedGraph<Integer, ImmutableEdge<Integer>> graph;

  @Before
  public void setUp() {
    vertices = ImmutableList.of(1, 2, 3, 4, 5);
    edges = ImmutableList.of(ImmutableEdge.construct(1, 2, 1), ImmutableEdge.construct(1, 3, 2),
        ImmutableEdge.construct(1, 4, 3), ImmutableEdge.construct(2, 5, 4),
        ImmutableEdge.construct(3, 5, 5), ImmutableEdge.construct(4, 5, 6));
    graph = ImmutableDirectedGraph.construct(edges);
  }

  @Test
  public void testBuilder() {
    Builder<Integer, ImmutableEdge<Integer>> builder =
        ImmutableDirectedGraph.builder(new ImmutableEdge.Factory<Integer>());
    for (int i = 1; i <= 5; ++i) {
      builder.addVertex(i);
    }
    for (int i = 2; i <= 4; ++i) {
      builder.addEdge(1, i);
      builder.addEdge(i, 5);
    }

    ImmutableDirectedGraph<Integer, ImmutableEdge<Integer>> builtGraph = builder.build(true);
    assertEquals("ImmutableDirectedGraph [edgeLists={1=ImmutableVertexEdgeList [inEdges=[], "
        + "outEdges=[ImmutableEdge [source=1, dest=2, id=1], ImmutableEdge "
        + "[source=1, dest=3, id=3], ImmutableEdge [source=1, dest=4, id=5]]], "
        + "2=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=1, dest=2, id=1]], "
        + "outEdges=[ImmutableEdge [source=2, dest=5, id=2]]], 3=ImmutableVertexEdgeList "
        + "[inEdges=[ImmutableEdge [source=1, dest=3, id=3]], outEdges=[ImmutableEdge "
        + "[source=3, dest=5, id=4]]], 4=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
        + "[source=1, dest=4, id=5]], outEdges=[ImmutableEdge [source=4, dest=5, id=6]]], "
        + "5=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=2, dest=5, id=2], "
        + "ImmutableEdge [source=3, dest=5, id=4], ImmutableEdge [source=4, dest=5, id=6]], "
        + "outEdges=[]]}, edgeCount=6]", builtGraph.toString());
  }

  @Test
  public void testConstructVerticesEdgesCheck() {
    ImmutableDirectedGraph<Integer, ImmutableEdge<Integer>> builtGraph =
        ImmutableDirectedGraph.construct(vertices, edges, true);
    assertEquals("ImmutableDirectedGraph [edgeLists={1=ImmutableVertexEdgeList [inEdges=[], "
        + "outEdges=[ImmutableEdge [source=1, dest=2, id=1], ImmutableEdge "
        + "[source=1, dest=3, id=2], ImmutableEdge [source=1, dest=4, id=3]]], "
        + "2=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=1, dest=2, id=1]], "
        + "outEdges=[ImmutableEdge [source=2, dest=5, id=4]]], 3=ImmutableVertexEdgeList "
        + "[inEdges=[ImmutableEdge [source=1, dest=3, id=2]], outEdges=[ImmutableEdge "
        + "[source=3, dest=5, id=5]]], 4=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
        + "[source=1, dest=4, id=3]], outEdges=[ImmutableEdge [source=4, dest=5, id=6]]], "
        + "5=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=2, dest=5, id=4], "
        + "ImmutableEdge [source=3, dest=5, id=5], ImmutableEdge [source=4, dest=5, id=6]], "
        + "outEdges=[]]}, edgeCount=6]", builtGraph.toString());
  }

  @Test
  public void testConstructEdgesCheck() {
    ImmutableDirectedGraph<Integer, ImmutableEdge<Integer>> builtGraph =
        ImmutableDirectedGraph.construct(edges, true);
    assertEquals("ImmutableDirectedGraph [edgeLists={1=ImmutableVertexEdgeList [inEdges=[], "
        + "outEdges=[ImmutableEdge [source=1, dest=2, id=1], ImmutableEdge "
        + "[source=1, dest=3, id=2], ImmutableEdge [source=1, dest=4, id=3]]], "
        + "2=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=1, dest=2, id=1]], "
        + "outEdges=[ImmutableEdge [source=2, dest=5, id=4]]], 3=ImmutableVertexEdgeList "
        + "[inEdges=[ImmutableEdge [source=1, dest=3, id=2]], outEdges=[ImmutableEdge "
        + "[source=3, dest=5, id=5]]], 4=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
        + "[source=1, dest=4, id=3]], outEdges=[ImmutableEdge [source=4, dest=5, id=6]]], "
        + "5=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=2, dest=5, id=4], "
        + "ImmutableEdge [source=3, dest=5, id=5], ImmutableEdge [source=4, dest=5, id=6]], "
        + "outEdges=[]]}, edgeCount=6]", builtGraph.toString());
  }

  @Test
  public void testConstructVerticesEdges() {
    ImmutableDirectedGraph<Integer, ImmutableEdge<Integer>> builtGraph =
        ImmutableDirectedGraph.construct(vertices, edges);
    assertEquals("ImmutableDirectedGraph [edgeLists={1=ImmutableVertexEdgeList [inEdges=[], "
        + "outEdges=[ImmutableEdge [source=1, dest=2, id=1], ImmutableEdge "
        + "[source=1, dest=3, id=2], ImmutableEdge [source=1, dest=4, id=3]]], "
        + "2=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=1, dest=2, id=1]], "
        + "outEdges=[ImmutableEdge [source=2, dest=5, id=4]]], 3=ImmutableVertexEdgeList "
        + "[inEdges=[ImmutableEdge [source=1, dest=3, id=2]], outEdges=[ImmutableEdge "
        + "[source=3, dest=5, id=5]]], 4=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
        + "[source=1, dest=4, id=3]], outEdges=[ImmutableEdge [source=4, dest=5, id=6]]], "
        + "5=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=2, dest=5, id=4], "
        + "ImmutableEdge [source=3, dest=5, id=5], ImmutableEdge [source=4, dest=5, id=6]], "
        + "outEdges=[]]}, edgeCount=6]", builtGraph.toString());
  }

  @Test
  public void testSimpleQueryInterface() {
    assertTrue(graph.containsVertex(1));
    assertFalse(graph.containsVertex(6));
    assertEquals(5, graph.countVertices());
    assertEquals("[1, 2, 3, 4, 5]", ImmutableSortedSet.copyOf(graph.getVertices()).toString());

    Iterator<Integer> vertexIt = graph.vertexIterator();
    Set<Integer> vertices = new TreeSet<>();
    while (vertexIt.hasNext()) {
      vertices.add(vertexIt.next());
    }
    assertEquals("[1, 2, 3, 4, 5]", vertices.toString());

    assertEquals(6, graph.countEdges());
    assertTrue(graph.containsEdgeFromTo(1, 2));
    assertFalse(graph.containsEdgeFromTo(1, 5));
    assertEquals(
        "[ImmutableEdge [source=1, dest=2, id=1], ImmutableEdge [source=1, dest=3, id=2], "
            + "ImmutableEdge [source=1, dest=4, id=3], ImmutableEdge [source=2, dest=5, id=4], "
            + "ImmutableEdge [source=3, dest=5, id=5], ImmutableEdge [source=4, dest=5, id=6]]",
        ImmutableSortedSet.copyOf(graph.getEdges()).toString());

    Iterator<ImmutableEdge<Integer>> edgeIt = graph.edgeIterator();
    Set<ImmutableEdge<Integer>> edges = new TreeSet<>();
    while (edgeIt.hasNext()) {
      edges.add(edgeIt.next());
    }
    assertEquals(
        "[ImmutableEdge [source=1, dest=2, id=1], ImmutableEdge [source=1, dest=3, id=2], "
            + "ImmutableEdge [source=1, dest=4, id=3], ImmutableEdge [source=2, dest=5, id=4], "
            + "ImmutableEdge [source=3, dest=5, id=5], ImmutableEdge [source=4, dest=5, id=6]]",
        edges.toString());

    assertEquals("ImmutableEdge [source=1, dest=3, id=2]", graph.getEdge(1, 3).toString());
    assertEquals(3, graph.inDegree(5));
    assertEquals(3, graph.outDegree(1));
  }

  @Test
  public void testToString() {
    assertEquals("ImmutableDirectedGraph [edgeLists={1=ImmutableVertexEdgeList "
        + "[inEdges=[], outEdges=[ImmutableEdge [source=1, dest=2, id=1], "
        + "ImmutableEdge [source=1, dest=3, id=2], ImmutableEdge [source=1, dest=4, id=3]]], "
        + "2=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=1, dest=2, id=1]], "
        + "outEdges=[ImmutableEdge [source=2, dest=5, id=4]]], 3=ImmutableVertexEdgeList "
        + "[inEdges=[ImmutableEdge [source=1, dest=3, id=2]], outEdges=[ImmutableEdge "
        + "[source=3, dest=5, id=5]]], 4=ImmutableVertexEdgeList [inEdges=[ImmutableEdge "
        + "[source=1, dest=4, id=3]], outEdges=[ImmutableEdge [source=4, dest=5, id=6]]], "
        + "5=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=2, dest=5, id=4], "
        + "ImmutableEdge [source=3, dest=5, id=5], ImmutableEdge [source=4, dest=5, id=6]], "
        + "outEdges=[]]}, edgeCount=6]", graph.toString());
  }

  @Test
  public void testOutEdgeIterator() {
    Iterator<ImmutableEdge<Integer>> it = graph.outEdgeIterator(1);
    List<ImmutableEdge<Integer>> edges = new ArrayList<>();
    while (it.hasNext()) {
      edges.add(it.next());
    }
    assertEquals("[ImmutableEdge [source=1, dest=2, id=1], ImmutableEdge [source=1, dest=3, id=2], "
        + "ImmutableEdge [source=1, dest=4, id=3]]", edges.toString());
  }

  @Test
  public void testInEdgeIterator() {
    Iterator<ImmutableEdge<Integer>> it = graph.inEdgeIterator(5);
    List<ImmutableEdge<Integer>> edges = new ArrayList<>();
    while (it.hasNext()) {
      edges.add(it.next());
    }
    assertEquals("[ImmutableEdge [source=2, dest=5, id=4], ImmutableEdge [source=3, dest=5, id=5], "
        + "ImmutableEdge [source=4, dest=5, id=6]]", edges.toString());
  }

  @Test
  public void testViaOutEdgeIterator() {
    Iterator<Integer> it = graph.viaOutEdgeIterator(1);
    List<Integer> vertices = new ArrayList<>();
    while (it.hasNext()) {
      vertices.add(it.next());
    }
    assertEquals("[2, 3, 4]", vertices.toString());
  }

  @Test
  public void testViaInEdgeIterator() {
    Iterator<Integer> it = graph.viaInEdgeIterator(5);
    List<Integer> vertices = new ArrayList<>();
    while (it.hasNext()) {
      vertices.add(it.next());
    }
    assertEquals("[2, 3, 4]", vertices.toString());
  }

  @Test
  public void testSubGraph() {
    DirectedGraph<Integer, ImmutableEdge<Integer>> subGraph =
        graph.subGraph(ImmutableList.of(1, 2, 5));
    assertEquals("ImmutableDirectedGraph [edgeLists={1=ImmutableVertexEdgeList [inEdges=[], "
        + "outEdges=[ImmutableEdge [source=1, dest=2, id=1]]], "
        + "2=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=1, dest=2, id=1]], "
        + "outEdges=[ImmutableEdge [source=2, dest=5, id=4]]], "
        + "5=ImmutableVertexEdgeList [inEdges=[ImmutableEdge [source=2, dest=5, id=4]], "
        + "outEdges=[]]}, edgeCount=2]", subGraph.toString());
  }

}
