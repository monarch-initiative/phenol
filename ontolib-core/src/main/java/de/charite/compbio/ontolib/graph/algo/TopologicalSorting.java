package de.charite.compbio.ontolib.graph.algo;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;

/**
 * Topological sorting for {@link DirectedGraph}s using the <b>visitor
 * pattern</b>.
 *
 * @param <V>
 *          vertex type of graph, see {@link DirectedGraph} for requirements on
 *          vertex type
 * @param <E>
 *          edge type to use in the graph, also see {@link DirectedGraph} for
 *          details
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TopologicalSorting<V, E extends Edge<V>> {

  /**
   * Visit vertices in topological order using forward edges (i.e., the vertex
   * at the source of an edge should be visited <b>before</b> the vertex at the
   * destination of an edge).
   *
   * @param g
   *          Graph to perform topological sorting on
   * @param visitor
   *          {@link VertexVisitor} to use for visiting the vertices
   */
  void startForward(DirectedGraph<V, E> g, VertexVisitor<V, E> visitor);

  /**
   * Visit vertices in topological order using reverseedges (i.e., the vertex at
   * the source of an edge should be visited <b>after</b> the vertex at the
   * destination of an edge).
   *
   * @param g
   *          Graph to perform topological sorting on
   * @param visitor
   *          {@link VertexVisitor} to use for visiting the vertices
   */
  void startReverse(DirectedGraph<V, E> g, VertexVisitor<V, E> visitor);

}
