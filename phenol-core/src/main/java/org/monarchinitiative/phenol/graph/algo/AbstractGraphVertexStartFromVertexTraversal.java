package org.monarchinitiative.phenol.graph.algo;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;

/**
 * Provide default implementations for the <code>startFromForward()</code> and <code>
 * startFromReverse()</code> functions without {@link NeighborSelector} for traversal algorithms
 * that start at individual nodes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
abstract class AbstractGraphVertexStartFromVertexTraversal<
    V extends Comparable<V>, E extends IdLabeledEdge, G extends DefaultDirectedGraph<V, E>> {

  /**
   * Start traversal in <code>G</code> from <code>v</code> over <b>outgoing</b> edges using <code>
   * visitor</code> for notifying about reaching a vertex.
   *
   * @param g {@link DefaultDirectedGraph} to traverse.
   * @param v Vertex to start traversal from.
   * @param visitor {@link VertexVisitor} to use.
   */
  public void startFromForward(G g, V v, VertexVisitor<V, E> visitor) {
    final NeighborSelector<V, E> neighborSelector = new ForwardNeighborSelector<>();
    startFromImpl(g, v, visitor, neighborSelector);
  }

  /**
   * Start traversal in <code>G</code> from <code>v</code> over <b>incoming</b> edges using <code>
   * visitor</code> for notifying about reaching a vertex.
   *
   * @param g {@link DefaultDirectedGraph} to traverse.
   * @param v Vertex to start traversal from.
   * @param visitor {@link VertexVisitor} to use.
   */
  public void startFromReverse(G g, V v, VertexVisitor<V, E> visitor) {
    final NeighborSelector<V, E> neighborSelector = new ReverseNeighborSelector<>();
    startFromImpl(g, v, visitor, neighborSelector);
  }

  /**
   * Override for logic, using <code>selector</code> for chosing neighbors of <code>v</code>.
   *
   * @param g {@link DefaultDirectedGraph} to use for iteration.
   * @param v Vertex to start from.
   * @param visitor {@link VertexVisitor} to use for visiting vertices.
   * @param selector {@link NeighborSelector} for selecting forward/reverse vertices.
   */
  protected abstract void startFromImpl(
      G g, V v, VertexVisitor<V, E> visitor, NeighborSelector<V, E> selector);
}
