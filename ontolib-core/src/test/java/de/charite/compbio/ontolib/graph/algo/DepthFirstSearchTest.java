package de.charite.compbio.ontolib.graph.algo;

import static org.junit.Assert.assertEquals;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class DepthFirstSearchTest extends GraphAlgoTestBase {

  @Test
  public void testSimpleDagBfsForward() {
    List<Integer> visited = new ArrayList<>();
    DepthFirstSearch<Integer, ImmutableEdge<Integer>> dfs = new DepthFirstSearch<>();
    dfs.startFromForward(simpleDag, 1, new VertexVisitor<Integer, ImmutableEdge<Integer>>() {
      @Override
      public boolean visit(DirectedGraph<Integer, ImmutableEdge<Integer>> g, Integer v) {
        visited.add(v);
        return true;
      }
    });

    assertEquals("[1, 3, 4, 2]", visited.toString());
  }

  @Test
  public void testSimpleDagBfsReverse() {
    List<Integer> visited = new ArrayList<>();
    DepthFirstSearch<Integer, ImmutableEdge<Integer>> dfs = new DepthFirstSearch<>();
    dfs.startFromReverse(simpleDag, 4, new VertexVisitor<Integer, ImmutableEdge<Integer>>() {
      @Override
      public boolean visit(DirectedGraph<Integer, ImmutableEdge<Integer>> g, Integer v) {
        visited.add(v);
        return true;
      }
    });

    assertEquals("[4, 3, 1, 2]", visited.toString());
  }

  @Test
  public void testSimpleLineBfsForward() {
    List<Integer> visited = new ArrayList<>();
    DepthFirstSearch<Integer, ImmutableEdge<Integer>> dfs = new DepthFirstSearch<>();
    dfs.startFromForward(simpleLine, 1, new VertexVisitor<Integer, ImmutableEdge<Integer>>() {
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
    DepthFirstSearch<Integer, ImmutableEdge<Integer>> dfs = new DepthFirstSearch<>();
    dfs.startFromReverse(simpleLine, 5, new VertexVisitor<Integer, ImmutableEdge<Integer>>() {
      @Override
      public boolean visit(DirectedGraph<Integer, ImmutableEdge<Integer>> g, Integer v) {
        visited.add(v);
        return true;
      }
    });

    assertEquals("[5, 4, 3, 2, 1]", visited.toString());
  }

}