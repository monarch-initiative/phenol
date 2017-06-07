package de.charite.compbio.ontolib.graph.data;

import java.io.Serializable;

public interface Edge<V> extends Serializable {
    public V getSource();
    public V getDest();
    public int getID();
}