package de.charite.compbio.ontolib.graph.algo;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;

/**
 * Interface for iteration of {@link DirectedGraph} vertices using the Visitor pattern, starting
 * from specific vertex.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
interface GraphVertexAllIteration<V, E extends Edge<V>, G extends DirectedGraph<V, E>> {

  /**
   * Iterate all vertices in topological order (traversing edges in <b>forward direction</b>).
   *
   * <p>
   * {@link VertexVisitor#visit(DirectedGraph, Object)} will be called for vertices of the graph
   * starting from <code>v</code>
   * </p>
   *
   * @param g {@link DirectedGraph} to iterate over
   * @param visitor {@link VertexVisitor} to use for notifying about reaching a vertex
   */
  public void startForward(G g, VertexVisitor<V, E> visitor);

  /**
   * Iterate all vertices in reverse topological order (traversing edges in <b>reverse
   * direction</b>).
   *
   * <p>
   * {@link VertexVisitor#visit(DirectedGraph, Object)} will be called for vertices of the graph
   * starting from <code>v</code>
   * </p>
   *
   * @param g {@link DirectedGraph} to iterate over
   * @param visitor {@link VertexVisitor} to use for notifying about reaching a vertex
   */
  public void startReverse(G g, VertexVisitor<V, E> visitor);

}
