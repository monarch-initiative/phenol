package com.github.phenomics.ontolib.graph.algo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import com.github.phenomics.ontolib.graph.algo.BreadthFirstSearch;
import com.github.phenomics.ontolib.graph.algo.VertexVisitor;
import com.github.phenomics.ontolib.graph.data.DirectedGraph;
import com.github.phenomics.ontolib.graph.data.ImmutableEdge;

public class BreadthFirstSearchTest extends GraphAlgoTestBase {

  @Test
  public void testSimpleDagBfsForward() {
    List<Integer> visited = new ArrayList<>();
    BreadthFirstSearch<Integer, ImmutableEdge<Integer>> bfs = new BreadthFirstSearch<>();
    bfs.startFromForward(simpleDag, 1, new VertexVisitor<Integer, ImmutableEdge<Integer>>() {
      @Override
      public boolean visit(DirectedGraph<Integer, ImmutableEdge<Integer>> g, Integer v) {
        visited.add(v);
        return true;
      }
    });

    assertEquals("[1, 2, 3, 4]", visited.toString());
  }

  @Test
  public void testSimpleDagBfsReverse() {
    List<Integer> visited = new ArrayList<>();
    BreadthFirstSearch<Integer, ImmutableEdge<Integer>> bfs = new BreadthFirstSearch<>();
    bfs.startFromReverse(simpleDag, 4, new VertexVisitor<Integer, ImmutableEdge<Integer>>() {
      @Override
      public boolean visit(DirectedGraph<Integer, ImmutableEdge<Integer>> g, Integer v) {
        visited.add(v);
        return true;
      }
    });

    assertEquals("[4, 2, 3, 1]", visited.toString());
  }

  @Test
  public void testSimpleLineBfsForward() {
    List<Integer> visited = new ArrayList<>();
    BreadthFirstSearch<Integer, ImmutableEdge<Integer>> bfs = new BreadthFirstSearch<>();
    bfs.startFromForward(simpleLine, 1, new VertexVisitor<Integer, ImmutableEdge<Integer>>() {
      @Override
      public boolean visit(DirectedGraph<Integer, ImmutableEdge<Integer>> g, Integer v) {
        visited.add(v);
        return true;
      }
    });

    assertEquals("[1, 2, 3, 4, 5]", visited.toString());
  }

  @Test
  public void testSimpleLineBfsReverse() {
    List<Integer> visited = new ArrayList<>();
    BreadthFirstSearch<Integer, ImmutableEdge<Integer>> bfs = new BreadthFirstSearch<>();
    bfs.startFromReverse(simpleLine, 5, new VertexVisitor<Integer, ImmutableEdge<Integer>>() {
      @Override
      public boolean visit(DirectedGraph<Integer, ImmutableEdge<Integer>> g, Integer v) {
        visited.add(v);
        return true;
      }
    });

    assertEquals("[5, 4, 3, 2, 1]", visited.toString());
  }

}
