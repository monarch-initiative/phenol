package de.charite.compbio.ontolib.graph.data;

/**
 * Implementation of an immutable {@link Edge}.
 *
 * <p>
 * To be used as the edge type for {@link ImmutableDirectedGraph}.
 * </p>
 *
 * @param <V> Vertex type to use, see {@link DirectedGraph} for requirements on this type.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ImmutableEdge<V> implements Edge<V> {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** Source vertex of immutable directed edge. */
  private final V source;

  /** Destination vertex of immutable directed edge. */
  private final V dest;

  /** Numeric edge identifier, for use in maps. */
  private final int id;

  /**
   * Construct and return new {@link ImmutableEdge}.
   *
   * @param <V> Vertex type to use
   *
   * @param source Source vertex of edge.
   * @param dest Destination vertex of edge.
   * @param id Numeric edge identifier.
   * @return Freshly created {@link ImmutableEdge}.
   */
  public static <V> ImmutableEdge<V> construct(final V source, final V dest, final int id) {
    return new ImmutableEdge<V>(source, dest, id);
  }

  /**
   * Constructor.
   *
   * @param source Source vertex.
   * @param dest Destination vertex.
   * @param id Edge identifier, for use in maps.
   */
  public ImmutableEdge(final V source, final V dest, final int id) {
    this.source = source;
    this.dest = dest;
    this.id = id;
  }

  @Override
  public final V getSource() {
    return source;
  }

  @Override
  public final V getDest() {
    return dest;
  }

  @Override
  public final int getID() {
    return id;
  }

  @Override
  public final Object clone() {
    return new ImmutableEdge<V>(source, dest, id);
  }

  @Override
  public String toString() {
    return "ImmutableEdge [source=" + source + ", dest=" + dest + ", id=" + id + "]";
  }

  /**
   * Factory for {@link ImmutableEdge}.
   *
   * @param <V> Vertex type to use
   *
   * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
   */
  public static class Factory<V> implements Edge.Factory<V, ImmutableEdge<V>> {

    /** ID of next edge to add. */
    private int nextEdgeID;

    /**
     * Default constructor.
     */
    public Factory() {
      this.nextEdgeID = 1;
    }

    @Override
    public final ImmutableEdge<V> construct(final V u, final V v) {
      return ImmutableEdge.construct(u, v, ++nextEdgeID);
    }

    @Override
    public final int getNextEdgeID() {
      return nextEdgeID;
    }

    @Override
    public final void setNextEdgeID(final int nextEdgeID) {
      this.nextEdgeID = nextEdgeID;
    }

  }
}
