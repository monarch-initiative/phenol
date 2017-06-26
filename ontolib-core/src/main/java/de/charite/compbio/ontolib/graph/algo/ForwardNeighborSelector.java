package de.charite.compbio.ontolib.graph.algo;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;
import java.util.Iterator;

/**
 * Implementation of {@link NeighborSelector} using out-edges.
 *
 * <p>
 * This is an implementation detail and should not be used in client code.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class ForwardNeighborSelector<V extends Comparable<V>, E extends Edge<V>>
    implements
      NeighborSelector<V, E> {

  @Override
  public Iterator<V> nextFrom(DirectedGraph<V, E> g, V v) {
    return g.viaOutEdgeIterator(v);
  }

}
