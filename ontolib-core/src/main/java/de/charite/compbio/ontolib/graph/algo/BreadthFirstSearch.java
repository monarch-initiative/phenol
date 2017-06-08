package de.charite.compbio.ontolib.graph.algo;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;

/**
 * Breadth-first-search for {@link DirectedGraph}s using the <b>visitor pattern</b>.
 *
 * @param <V> vertex type of graph, see {@link DirectedGraph} for requirements on vertex type
 * @param <E> edge type to use in the graph, also see {@link DirectedGraph} for details
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface BreadthFirstSearch<V, E extends Edge<V>> {

  /**
   * Start breadth-first-search using forward edges in Graph <code>g</code> starting from
   * <code>v</code>, using {@link VertexVisitor} <code>visitor</code>.
   *
   * @param g {@link DirectedGraph} to work on
   * @param v Vertex to start on
   * @param visitor {@link VertexVisitor} to use to signal currently visited vertex
   */
  void startFromForward(DirectedGraph<V, E> g, V v, VertexVisitor<V, E> visitor);

  /**
   * Start breath-first-search using reverse edges in Graph <code>g</code> starting from
   * <code>v</code>, using {@link VertexVisitor} <code>visitor</code>.
   *
   * @param g {@link DirectedGraph} to work on
   * @param v Vertex to start on
   * @param visitor {@link VertexVisitor} to use to signal currently visited vertex
   */
  void startFromReverse(DirectedGraph<V, E> g, V v, VertexVisitor<V, E> visitor);

}
