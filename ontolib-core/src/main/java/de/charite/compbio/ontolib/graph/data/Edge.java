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

  /**
   * Query for source vertex.
   *
   * @return source vertex (foot of directed edge)
   */
  V getSource();

  /**
   * Query for destination vertex.
   *
   * @return destination vertex (tip of directed edge)
   */
  V getDest();

  /**
   * Query for numeric edge ID.
   *
   * @return numeric identifier of the edge, for mapping edge Id to edge properties
   */
  int getId();

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
     * Get Id of next edge to construct.
     *
     * @return Integer value of next edge Id.
     */
    int getNextEdgeId();

    /**
     * Set Id of next edge to construct.
     *
     * @param nextEdgeId Integer value for next edge Id.
     */
    void setNextEdgeId(final int nextEdgeId);
  }

}
