package org.monarchinitiative.phenol.graph.algo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.jgrapht.graph.DefaultDirectedGraph;

public class TopologicalSortingFirstSearchTest extends GraphAlgoTestBase {

  @Test
  public void testSimpleDagTopsortForward() {
    List<Integer> visited = new ArrayList<>();
    TopologicalSorting<Integer, IdLabeledEdge, DefaultDirectedGraph<Integer, IdLabeledEdge>>
        topSort = new TopologicalSorting<>();
    topSort.startForward(
        simpleDag,
        new VertexVisitor<Integer, IdLabeledEdge>() {
          @Override
          public boolean visit(DefaultDirectedGraph<Integer, IdLabeledEdge> g, Integer v) {
            visited.add(v);
            return true;
          }
        });

    assertEquals("[4, 2, 3, 1]", visited.toString());
  }

  @Test
  public void testSimpleDagTopsortReverse() {
    List<Integer> visited = new ArrayList<>();
    TopologicalSorting<Integer, IdLabeledEdge, DefaultDirectedGraph<Integer, IdLabeledEdge>>
        topSort = new TopologicalSorting<>();
    topSort.startReverse(
        simpleDag,
        new VertexVisitor<Integer, IdLabeledEdge>() {
          @Override
          public boolean visit(DefaultDirectedGraph<Integer, IdLabeledEdge> g, Integer v) {
            visited.add(v);
            return true;
          }
        });

    assertEquals("[1, 2, 3, 4]", visited.toString());
  }

  @Test
  public void testSimpleLineTopsortForward() {
    List<Integer> visited = new ArrayList<>();
    TopologicalSorting<Integer, IdLabeledEdge, DefaultDirectedGraph<Integer, IdLabeledEdge>>
        topSort = new TopologicalSorting<>();
    topSort.startForward(
        simpleLine,
        new VertexVisitor<Integer, IdLabeledEdge>() {
          @Override
          public boolean visit(DefaultDirectedGraph<Integer, IdLabeledEdge> g, Integer v) {
            visited.add(v);
            return true;
          }
        });

    assertEquals("[5, 4, 3, 2, 1]", visited.toString());
  }

  @Test
  public void testSimpleLineTopsortReverse() {
    List<Integer> visited = new ArrayList<>();
    TopologicalSorting<Integer, IdLabeledEdge, DefaultDirectedGraph<Integer, IdLabeledEdge>>
        topSort = new TopologicalSorting<>();
    topSort.startReverse(
        simpleLine,
        new VertexVisitor<Integer, IdLabeledEdge>() {
          @Override
          public boolean visit(DefaultDirectedGraph<Integer, IdLabeledEdge> g, Integer v) {
            visited.add(v);
            return true;
          }
        });

    assertEquals("[1, 2, 3, 4, 5]", visited.toString());
  }
}
