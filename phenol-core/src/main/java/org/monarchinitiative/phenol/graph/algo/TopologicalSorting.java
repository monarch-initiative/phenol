package org.monarchinitiative.phenol.graph.algo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;

/**
 * Topological sorting for {@link DefaultDirectedGraph}s using the <b>visitor pattern</b>.
 *
 * @param <V> vertex type of graph, see {@link DefaultDirectedGraph} for requirements on vertex type
 * @param <E> edge type to use in the graph, also see {@link DefaultDirectedGraph} for details
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class TopologicalSorting<
        V extends Comparable<V>, E extends IdLabeledEdge, G extends DefaultDirectedGraph<V, E>>
    implements GraphVertexAllIteration<V, E, G> {

  @Override
  public void startForward(G g, VertexVisitor<V, E> visitor) {
    final NeighborSelector<V, E> neighborSelector = new ForwardNeighborSelector<>();
    startImpl(g, visitor, neighborSelector);
  }

  @Override
  public void startReverse(G g, VertexVisitor<V, E> visitor) {
    final NeighborSelector<V, E> neighborSelector = new ReverseNeighborSelector<>();
    startImpl(g, visitor, neighborSelector);
  }

  /**
   * Implementation of Tarjan's algorithm for topological sorting.
   *
   * @param g {@link DefaultDirectedGraph} to iterate
   * @param visitor {@link VertexVisitor} to use for notifying about reaching a vertex
   * @param selector {@link NeighborSelector} to use for selecting the next neighbor
   */
  private void startImpl(G g, VertexVisitor<V, E> visitor, NeighborSelector<V, E> selector) {
    final Set<V> tmpMarked = new HashSet<>();

    // Collect unmarked vertices
    final Set<V> unmarked = new HashSet<>(g.vertexSet());

    // Perform visiting
    while (!unmarked.isEmpty()) {
      final V v = unmarked.iterator().next();
      startFromImpl(g, unmarked, tmpMarked, v, visitor, selector);
    }
  }

  /**
   * Tarjan's <code>visit()</code>.
   *
   * @param g {@link DefaultDirectedGraph} to traverse
   * @param unmarked Unmarked vertices
   * @param tmpMarked Temporarily marked vertices
   * @param v Vertex to start from
   * @param selector {@link NeighborSelector} to select neighbors with
   */
  private void startFromImpl(
      G g,
      Set<V> unmarked,
      Set<V> tmpMarked,
      V v,
      VertexVisitor<V, E> visitor,
      NeighborSelector<V, E> selector) {
    if (tmpMarked.contains(v)) {
      throw new GraphNotDagException("Graph is not a DAG");
    }
    if (unmarked.contains(v)) {
      tmpMarked.add(v);
      Iterator<V> nextVertices = selector.nextFrom(g, v);
      while (nextVertices.hasNext()) {
        startFromImpl(g, unmarked, tmpMarked, nextVertices.next(), visitor, selector);
      }
      unmarked.remove(v);
      tmpMarked.remove(v);
      if (!visitor.visit(g, v)) {
        return;
      }
    }
  }
}
