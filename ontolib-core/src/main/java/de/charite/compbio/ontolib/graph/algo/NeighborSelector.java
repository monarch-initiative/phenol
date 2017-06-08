package de.charite.compbio.ontolib.graph.algo;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;
import java.util.Iterator;

/**
 * Abstraction for going over forward/reverse edges for traversal algorithms.
 *
 * <p>
 * This is an implementation detail and should not be used in client code.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
interface NeighborSelector<V, E extends Edge<V>> {

  /**
   * Return {@link Iterator} of vertices to go from next from <code>v</code>.
   * 
   * @param g {@link DirectedGraph} that contains <code>v</code>
   * @param v Vertex to iterate neighbors of
   * @return {@link Iterator} of vertices to go to next from <code>v</code>.
   */
  public Iterator<V> nextFrom(DirectedGraph<V, E> g, V v);

}
