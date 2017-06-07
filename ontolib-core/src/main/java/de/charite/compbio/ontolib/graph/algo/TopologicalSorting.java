package de.charite.compbio.ontolib.graph.algo;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;

public interface TopologicalSorting<V, E> {
    public void startForward(DirectedGraph<V, E> g, VertexVisitor<V, E> visitor);

    public void startReverse(DirectedGraph<V, E> g, VertexVisitor<V, E> visitor);
}
