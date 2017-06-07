package de.charite.compbio.ontolib.graph.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public interface DirectedGraph<V, E> extends Serializable {
    public boolean containsVertex(V v);

    public int countVertices();

    public Collection<V> getVertices();

    public Iterator<V> vertexIterator();

    public int countEdges();

    public boolean containsEdgeFromTo(V s, V t);

    public Collection<E> getEdge();

    public Iterator<E> edgeIterator();

    public E getEdge(V s, V t);

    public int inDegree(V v);

    public Iterator<E> inEdgeIterator(V v);

    public Iterator<V> viaInEdgeIterator(V v);

    public int outDegree(V v);

    public Iterator<E> outEdgeIterator(V v);

    public Iterator<V> viaOutEdgeIterator(V v);

    public DirectedGraph<V, E> subGraph(Collection<V> vertices);
}