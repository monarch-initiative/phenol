package org.monarchinitiative.phenol.graph.util;

import java.util.Collection;
import java.util.Iterator;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;

/**
 * Legacy codes from ImmutableDirectedGraph in Ontolib.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class GraphUtil<V> {
  // This method should be used only for testing codes, not for loading graph data in loaders.
  public static <V> void addEdgeToGraph(
      DefaultDirectedGraph<V, IdLabeledEdge> g, V t1, V t2, int id) {
    g.addVertex(t1);
    g.addVertex(t2);
    IdLabeledEdge e = new IdLabeledEdge();//edgeFactory.createEdge(t1, t2);
    e.setId(id);
    g.addEdge(t1, t2, e);
  }

  public static <V> Iterator<V> viaInEdgeIterator(
      DefaultDirectedGraph<V, ? extends IdLabeledEdge> g, V v) {
    final Iterator<? extends IdLabeledEdge> iter = g.incomingEdgesOf(v).iterator();

    return new Iterator<V>() {
      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @SuppressWarnings("unchecked")
      @Override
      public V next() {
        return (V) iter.next().getSource();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  public static <V> Iterator<V> viaOutEdgeIterator(
      DefaultDirectedGraph<V, ? extends IdLabeledEdge> g, V v) {
    final Iterator<? extends IdLabeledEdge> iter = g.outgoingEdgesOf(v).iterator();

    return new Iterator<V>() {
      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @SuppressWarnings("unchecked")
      @Override
      public V next() {
        return (V) iter.next().getTarget();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @SuppressWarnings("unchecked")
  public static <V> DefaultDirectedGraph<V, IdLabeledEdge> subGraph(
      DefaultDirectedGraph<V, IdLabeledEdge> g, Collection<V> vertices) {
    DefaultDirectedGraph<V, IdLabeledEdge> newSubgraph =
      new DefaultDirectedGraph<>(IdLabeledEdge.class);

    for (IdLabeledEdge e : g.edgeSet() ) {
      if (vertices.contains(e.getSource()) && vertices.contains(e.getTarget())) {
        IdLabeledEdge termIdEdge = new IdLabeledEdge();
        termIdEdge.setId(e.getId());
        newSubgraph.addVertex((V) e.getSource());
        newSubgraph.addVertex((V) e.getTarget());
        newSubgraph.addEdge((V) e.getSource(), (V) e.getTarget(), termIdEdge);
      }
    }

    return newSubgraph;
  }
}
