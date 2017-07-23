package com.github.phenomics.ontolib.graph.data;

import com.google.common.collect.ComparisonChain;

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
public class ImmutableEdge<V extends Comparable<V>> implements Edge<V> {

  /** Serial UId for serialization. */
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
  public static <V extends Comparable<V>> ImmutableEdge<V> construct(final V source, final V dest,
      final int id) {
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
  public final int getId() {
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dest == null) ? 0 : dest.hashCode());
    result = prime * result + id;
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    @SuppressWarnings("unchecked")
    ImmutableEdge<V> other = (ImmutableEdge<V>) obj;
    if (dest == null) {
      if (other.dest != null) {
        return false;
      }
    } else if (!dest.equals(other.dest)) {
      return false;
    }
    if (id != other.id) {
      return false;
    }
    if (source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!source.equals(other.source)) {
      return false;
    }
    return true;
  }

  /**
   * Factory for {@link ImmutableEdge}.
   *
   * @param <V> Vertex type to use
   *
   * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
   */
  public static class Factory<V extends Comparable<V>>
      implements
        Edge.Factory<V, ImmutableEdge<V>> {

    /** Id of next edge to add. */
    private int nextEdgeId;

    /**
     * Default constructor.
     */
    public Factory() {
      this.nextEdgeId = 1;
    }

    @Override
    public final ImmutableEdge<V> construct(final V u, final V v) {
      return ImmutableEdge.construct(u, v, nextEdgeId++);
    }

    @Override
    public final int getNextEdgeId() {
      return nextEdgeId;
    }

    @Override
    public final void setNextEdgeId(final int nextEdgeId) {
      this.nextEdgeId = nextEdgeId;
    }

  }

  @Override
  public int compareTo(Edge<V> o) {
    if (!(o instanceof ImmutableEdge)) {
      throw new RuntimeException("Cannot compare " + o + " to " + this);
    }
    ImmutableEdge<V> that = (ImmutableEdge<V>) o;

    return ComparisonChain.start().compare(this.source, that.source).compare(this.dest, that.dest)
        .result();
  }

}
