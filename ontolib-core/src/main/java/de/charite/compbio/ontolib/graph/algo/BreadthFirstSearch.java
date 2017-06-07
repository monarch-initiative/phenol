package de.charite.compbio.ontolib.graph.algo;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;

public interface BreadthFirstSearch<V, E> {
    public void startFromForward(DirectedGraph<V, E> g, V v, VertexVisitor<V, E> visitor);

    public void startFromReverse(DirectedGraph<V, E> g, V v, VertexVisitor<V, E> visitor);
}
