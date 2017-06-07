package de.charite.compbio.ontolib.graph.algo;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;

public interface VertexVisitor<V, E> {
    public void visit(DirectedGraph<V, E> g, V v);
}