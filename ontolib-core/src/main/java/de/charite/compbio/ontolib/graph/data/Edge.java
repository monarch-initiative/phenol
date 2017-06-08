package de.charite.compbio.ontolib.graph.data;

import java.io.Serializable;

/**
 * Interface for an edge in a {@link DirectedGraph}, parametrized with vertex type.
 *
 * @param <V> Vertex type, see {@link DirectedGraph} for requirements on this type
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface Edge<V> extends Serializable, Cloneable {

  /** @return source vertex (foot of directed edge) */
  V getSource();

  /** @return destination vertex (tip of directed edge) */
  V getDest();

  /**
   * @return numeric identifier of the edge, for mapping edge ID to edge properties
   */
  int getID();

  /**
   * Interface for constructing edges of the correct type.
   *
   * @param <V> Vertex type, see {@link DirectedGraph} for requirements on this type
   * @param <E> Edge type, see {@link DirectedGraph} for requirements on this type
   *
   * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
   */
  public interface Factory<V, E extends Edge<V>> {
    /**
     * Construct from given source and target vertex, other edge attributes are set to the default
     * value.
     *
     * @param u source vertex
     * @param v target vertex
     * @return constructed {@link Edge}
     */
    E construct(V u, V v);
    
    /**
     * Get ID of next edge to construct.
     *
     * @return Integer value of next edge ID.
     */
    int getNextEdgeID();

    /**
     * Set ID of next edge to construct.
     *
     * @param nextEdgeID Integer value for next edge ID.
     */
    void setNextEdgeID(final int nextEdgeID);
  }

}
