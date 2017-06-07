package de.charite.compbio.ontolib.graph.data;

import java.io.Serializable;

/**
 * Interface for an edge in a {@link DirectedGraph}, parametrized with vertex
 * type.
 *
 * @param <V>
 *          Vertex type, see {@link DirectedGraph} for requirements on this type
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface Edge<V> extends Serializable {

  /** @return source vertex (foot of directed edge) */
  V getSource();

  /** @return destination vertex (tip of directed edge) */
  V getDest();

  /**
   * @return numeric identifier of the edge, for mapping edge ID to edge
   *         properties
   */
  int getID();

}
