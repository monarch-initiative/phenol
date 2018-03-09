package org.monarchinitiative.phenol.graph.algo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.jgrapht.graph.DefaultDirectedGraph;

public class DepthFirstSearchTest extends GraphAlgoTestBase {

  @Test
  public void testSimpleDagBfsForward() {
    List<Integer> visited = new ArrayList<>();
    DepthFirstSearch<Integer, IdLabeledEdge> dfs = new DepthFirstSearch<>();
    dfs.startFromForward(
        simpleDag,
        1,
        new VertexVisitor<Integer, IdLabeledEdge>() {
          @Override
          public boolean visit(DefaultDirectedGraph<Integer, IdLabeledEdge> g, Integer v) {
            visited.add(v);
            return true;
          }
        });

    assertEquals("[1, 3, 4, 2]", visited.toString());
  }

  @Test
  public void testSimpleDagBfsReverse() {
    List<Integer> visited = new ArrayList<>();
    DepthFirstSearch<Integer, IdLabeledEdge> dfs = new DepthFirstSearch<>();
    dfs.startFromReverse(
        simpleDag,
        4,
        new VertexVisitor<Integer, IdLabeledEdge>() {
          @Override
          public boolean visit(DefaultDirectedGraph<Integer, IdLabeledEdge> g, Integer v) {
            visited.add(v);
            return true;
          }
        });

    assertEquals("[4, 3, 1, 2]", visited.toString());
  }

  @Test
  public void testSimpleLineBfsForward() {
    List<Integer> visited = new ArrayList<>();
    DepthFirstSearch<Integer, IdLabeledEdge> dfs = new DepthFirstSearch<>();
    dfs.startFromForward(
        simpleLine,
        1,
        new VertexVisitor<Integer, IdLabeledEdge>() {
          @Override
          public boolean visit(DefaultDirectedGraph<Integer, IdLabeledEdge> g, Integer v) {
            visited.add(v);
            return true;
          }
        });

    assertEquals("[1, 2, 3, 4, 5]", visited.toString());
  }

  @Test
  public void testSimpleLineBfsReverse() {
    List<Integer> visited = new ArrayList<>();
    DepthFirstSearch<Integer, IdLabeledEdge> dfs = new DepthFirstSearch<>();
    dfs.startFromReverse(
        simpleLine,
        5,
        new VertexVisitor<Integer, IdLabeledEdge>() {
          @Override
          public boolean visit(DefaultDirectedGraph<Integer, IdLabeledEdge> g, Integer v) {
            visited.add(v);
            return true;
          }
        });

    assertEquals("[5, 4, 3, 2, 1]", visited.toString());
  }
}
