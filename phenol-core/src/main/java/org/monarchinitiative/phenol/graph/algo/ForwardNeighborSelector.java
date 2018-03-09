package org.monarchinitiative.phenol.graph.algo;

import java.util.Iterator;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.util.GraphUtil;

/**
 * Implementation of {@link NeighborSelector} using out-edges.
 *
 * <p>This is an implementation detail and should not be used in client code.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class ForwardNeighborSelector<V extends Comparable<V>, E extends IdLabeledEdge>
    implements NeighborSelector<V, E> {

  @Override
  public Iterator<V> nextFrom(DefaultDirectedGraph<V, E> g, V v) {
    return GraphUtil.viaOutEdgeIterator(g, v);
  }
}
