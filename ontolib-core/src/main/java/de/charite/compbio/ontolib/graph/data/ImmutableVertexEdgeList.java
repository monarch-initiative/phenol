package de.charite.compbio.ontolib.graph.data;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation detail for storing in- and out-edge lists.
 *
 * <p>
 * This class is not part of the public API of OntoLib and can change without notices.
 * </p>
 *
 * @param <V> Vertex type, see {@link DirectedGraph} for requirements on this type.
 * @param <E> Edge type, must be an {@link ImmutableEdge} of <code>V</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class ImmutableVertexEdgeList<V, E extends ImmutableEdge<V>>
    implements Serializable {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /** List of in-edges. */
  private final ImmutableList<E> inEdges;

  /** List of out-edges. */
  private final ImmutableList<E> outEdges;

  /**
   * @param <V>
   *          Vertex type, see {@link DirectedGraph} for requirements on this
   *          type.
   * @param <E>
   *          Edge type, must be an {@link ImmutableEdge} of <code>V</code>.
   *
   * @return Newly constructed {@link Builder}
   */
  public static <V, E extends ImmutableEdge<V>> Builder<V, E> builder() {
    return new Builder<V, E>();
  }

  /**
   * Constructor.
   *
   * @param inEdges
   *          {@link Collection} of in-edges
   * @param outEdges
   *          {@link Collection} of out-edges
   */
  ImmutableVertexEdgeList(final Collection<E> inEdges,
      final Collection<E> outEdges) {
    this.inEdges = ImmutableList.copyOf(inEdges);
    this.outEdges = ImmutableList.copyOf(outEdges);
  }

  /**
   * Query for in edges.
   *
   * @return {@link ImmutableList} of in-edges
   */
  public ImmutableList<E> getInEdges() {
    return inEdges;
  }

  /**
   * Query for out edges.
   *
   * @return {@link ImmutableList} of out-edges
   */
  public ImmutableList<E> getOutEdges() {
    return outEdges;
  }

  /**
   * Internal helper for construction of VertexEdgeList objects.
   *
   * @param <V>
   *          Vertex type, see {@link DirectedGraph} for requirements on this
   *          type.
   * @param <E>
   *          Edge type, must be an {@link ImmutableEdge} of <code>V</code>.
   *
   * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
   */
  public static final class Builder<V, E extends ImmutableEdge<V>> {
    /** List of in-edges. */
    private final List<E> inEdges = new ArrayList<E>();

    /** List of out-edges. */
    private final List<E> outEdges = new ArrayList<E>();

    /**
     * Add in-edge to builder.
     *
     * @param edge
     *          {@link ImmutableEdge} to add as in-edge.
     */
    public void addInEdge(final E edge) {
      inEdges.add(edge);
    }

    /**
     * Add out-edge to builder.
     *
     * @param edge
     *          {@link ImmutableEdge} to add as out-edge.
     */
    public void addOutEdge(final E edge) {
      outEdges.add(edge);
    }

    /**
     * Build and return new {@link ImmutableVertexEdgeList}.
     *
     * @return Freshly built {@link ImmutableVertexEdgeList}
     */
    public ImmutableVertexEdgeList<V, E> build() {
      return new ImmutableVertexEdgeList<V, E>(inEdges, outEdges);
    }
  }

  @Override
  public String toString() {
    return "ImmutableVertexEdgeList [inEdges=" + inEdges + ", outEdges="
        + outEdges + "]";
  }

}
