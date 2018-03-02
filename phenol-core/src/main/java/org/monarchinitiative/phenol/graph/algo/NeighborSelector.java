package org.monarchinitiative.phenol.graph.algo;

import java.util.Iterator;

import org.monarchinitiative.phenol.graph.data.DirectedGraph;
import org.monarchinitiative.phenol.graph.data.Edge;

/**
 * Abstraction for going over forward/reverse edges for traversal algorithms.
 *
 * <p>
 * This is an implementation detail and should not be used in client code.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
interface NeighborSelector<V extends Comparable<V>, E extends Edge<V>> {

  /**
   * Return {@link Iterator} of vertices to go from next from <code>v</code>.
   *
   * @param g {@link DirectedGraph} that contains <code>v</code>
   * @param v Vertex to iterate neighbors of
   * @return {@link Iterator} of vertices to go to next from <code>v</code>.
   */
  public Iterator<V> nextFrom(DirectedGraph<V, E> g, V v);

}
