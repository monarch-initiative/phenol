package de.charite.compbio.ontolib.graph.algo;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;

/**
 * Interface for implementation of the <b>visitor pattern</b> for graph vertices.
 *
 * @param <V> vertex type of graph, see {@link DirectedGraph} for requirements on vertex type
 * @param <E> edge type to use in the graph, also see {@link DirectedGraph} for details
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface VertexVisitor<V extends Comparable<V>, E extends Edge<V>> {

  /**
   * Algorithms using <code>VertexVisitor</code> will call this function for each vertex.
   *
   * @param g graph that the visitor is applied to
   * @param v currently visited vertex
   * @return <code>true</code> when to continue iteration and <code>false</code> otherwise
   */
  boolean visit(DirectedGraph<V, E> g, V v);

}
