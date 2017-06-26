package de.charite.compbio.ontolib.graph.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * Interface for a directed graph class with generic vertex and edge type.
 *
 * @param <V> vertex type; should behave as an discrete, integral type (properly implement
 *        <code>equals()</code> and <code>hash()</code>.
 * @param <E> edge type; should implement the {@link Edge} interface for the given <code>V</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface DirectedGraph<V extends Comparable<V>, E extends Edge<V>> extends Serializable {

  /**
   * Query for vertex containment.
   *
   * @param v Vertex to query for
   * @return <code>true</code> if the vertex is in the graph, <code>false</code> otherwise
   */
  boolean containsVertex(V v);

  /**
   * Count vertices.
   *
   * @return number of vertices in the graph
   */
  int countVertices();

  /**
   * Return vertices.
   *
   * @return {@link Collection} of vertices in the graph
   */
  Collection<V> getVertices();

  /**
   * Return vertex iterator.
   *
   * @return {@link Iterator} over all vertices in the graph
   */
  Iterator<V> vertexIterator();

  /**
   * Query for edge count.
   *
   * @return number of edges in the graph
   */
  int countEdges();

  /**
   * Query for existence of edge.
   *
   * @param s source vertex for edge
   * @param t target vertex for edge
   * @return <code>true</code> if there is an s-t-edge in the graph, <code>false</code> otherwise
   */
  boolean containsEdgeFromTo(V s, V t);

  /**
   * Return edges.
   *
   * @return {@link Collection} of all edges in the graph.
   */
  Collection<E> getEdges();

  /**
   * Return edge iterator.
   *
   * @return {@link Iterator} over all edges in the graph.
   */
  Iterator<E> edgeIterator();

  /**
   * Query for specific s-t edge.
   *
   * @param s source vertex for edge
   * @param t target vertex for edge
   * @return The s-t-edge from this graph or <code>null</code> if no such edge could be found.
   */
  E getEdge(V s, V t);

  /**
   * Query for in degree.
   *
   * @param v Vertex to query the out degree for
   * @return number of in edges towards <code>v</code>
   */
  int inDegree(V v);

  /**
   * Obtain in edge iterator.
   *
   * @param v Vertex to get iterator over in-edges for
   * @return {@link Iterator} of in edges towards <code>v</code>
   */
  Iterator<E> inEdgeIterator(V v);

  /**
   * Obtain iterator of vertices reachable through in-edges.
   *
   * @param v Vertex to enumerate neighboring vertices reachable over in edges from
   * @return {@link Iterator} of vertices reachable over in edges from <code>v</code>
   */
  Iterator<V> viaInEdgeIterator(V v);

  /**
   * Query for out degree.
   *
   * @param v Vertex to query the out degree for
   * @return number of out edges from <code>v</code>
   */
  int outDegree(V v);

  /**
   * Obtain iterator of vertices reachable through out-edges.
   *
   * @param v Vertex to get iterator over out-edges for
   * @return {@link Iterator} of out edges from <code>v</code>
   */
  Iterator<E> outEdgeIterator(V v);

  /**
   * Obtain iterator of vertices reachable through out-edges.
   *
   * @param v Vertex to enumerate neighboring vertices reachable over out edges from
   * @return {@link Iterator} of vertices reachable over out edges from <code>v</code>
   */
  Iterator<V> viaOutEdgeIterator(V v);

  /**
   * Build sub graph from collection of vertices.
   *
   * @param vertices to use for inducing sub graph
   * @return sub graph induced by the {@link Collection} of <code>vertices</code>.
   */
  DirectedGraph<V, E> subGraph(Collection<V> vertices);

}
