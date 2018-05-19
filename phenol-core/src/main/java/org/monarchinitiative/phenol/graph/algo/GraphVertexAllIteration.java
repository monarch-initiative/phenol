package org.monarchinitiative.phenol.graph.algo;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;

/**
 * Interface for iteration of {@link DefaultDirectedGraph} vertices using the Visitor pattern,
 * starting from specific vertex.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
interface GraphVertexAllIteration<
    V extends Comparable<V>, E extends IdLabeledEdge, G extends DefaultDirectedGraph<V, E>> {

  /**
   * Iterate all vertices in topological order (traversing edges in <b>forward direction</b>).
   *
   * <p>{@code VertexVisitor.visit(DefaultDirectedGraph, Object)} will be called for vertices of the
   * graph starting from <code>v</code>
   *
   * @param g {@link DefaultDirectedGraph} to iterate over
   * @param visitor {@link VertexVisitor} to use for notifying about reaching a vertex
   */
  void startForward(G g, VertexVisitor<V, E> visitor);

  /**
   * Iterate all vertices in reverse topological order (traversing edges in <b>reverse
   * direction</b>).
   *
   * <p>{@code VertexVisitor.visit(DefaultDirectedGraph, Object)} will be called for vertices of the
   * graph starting from <code>v</code>
   *
   * @param g {@link DefaultDirectedGraph} to iterate over
   * @param visitor {@link VertexVisitor} to use for notifying about reaching a vertex
   */
  void startReverse(G g, VertexVisitor<V, E> visitor);
}
