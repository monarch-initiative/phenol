package org.monarchinitiative.phenol.graph.algo;

import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * Interface for implementation of the <b>visitor pattern</b> for graph vertices.
 *
 * @param <V> vertex type of graph, see {@link DefaultDirectedGraph} for requirements on vertex type
 * @param <E> edge type to use in the graph, also see {@link DefaultDirectedGraph} for details
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface VertexVisitor<V extends Comparable<V>, E> {

  /**
   * Algorithms using <code>VertexVisitor</code> will call this function for each vertex.
   *
   * @param g graph that the visitor is applied to
   * @param v currently visited vertex
   * @return <code>true</code> when to continue iteration and <code>false</code> otherwise
   */
  boolean visit(DefaultDirectedGraph<V, E> g, V v);
}
