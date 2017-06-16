package de.charite.compbio.ontolib.graph.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of an immutable (read-only) {@link DirectedGraph}.
 *
 * @param <V> Vertex type, see {@link DirectedGraph} for requirements on this type.
 * @param <E> Edge type, must be an {@link ImmutableEdge} of <code>V</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ImmutableDirectedGraph<V, E extends ImmutableEdge<V>>
    implements
      DirectedGraph<V, E> {

  /** {@link Logger} object to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ImmutableDirectedGraph.class);

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** Mapping from vertex to list of incoming and outgoing directed edges. */
  private ImmutableMap<V, ImmutableVertexEdgeList<V, E>> edgeLists;

  /** Number of edges in the graph. */
  private final int edgeCount;

  /**
   * Construct and return a {@link Builder} helper object.
   * 
   * @param <V> Vertex type, see {@link DirectedGraph} for requirements on this type.
   * @param <E> Edge type, must be an {@link ImmutableEdge} of <code>V</code>.
   *
   * @param edgeFactory The {@link Edge.Factory} to use for constructing edges.
   * @return Freshly constructed {@link Builder} object.
   */
  public static <V,
      E extends ImmutableEdge<V>> Builder<V, E> builder(final Edge.Factory<V, E> edgeFactory) {
    return new Builder<V, E>(edgeFactory);
  }

  /**
   * Construct a new {@link ImmutableDirectedGraph} from a collection of vertices and edges.
   *
   * @param <V> Vertex type, see {@link DirectedGraph} for requirements on this type.
   * @param <E> Edge type, must be an {@link ImmutableEdge} of <code>V</code>.
   *
   * @param vertices {@link Collection} of <code>Vertex</code> objects to use for construction
   * @param edges {@link Collection} of <code>Edge</code> objects to use for construction
   * @param checkCompatibility whether or not to check vertex and edge list to be compatible
   * @return the built {@link ImmutableDirectedGraph}
   */
  @SuppressWarnings("unchecked")
  public static <V, E extends ImmutableEdge<V>> ImmutableDirectedGraph<V, E> construct(
      final Collection<V> vertices, final Collection<E> edges, final boolean checkCompatibility) {
    // Check compatibility if asked for
    if (checkCompatibility) {
      checkCompatibility(vertices, edges);
    }
    // Create copy of immutable edges
    final List<E> edgesCopy = edges.stream().map(e -> (E) e.clone()).collect(Collectors.toList());
    return new ImmutableDirectedGraph<V, E>(vertices, edgesCopy);
  }

  /**
   * Construct a new {@link ImmutableDirectedGraph} from a collection edges.
   *
   * <p>
   * The edge list is automatically inferred from the edges' vertices.
   * </p>
   *
   * @param <V> Vertex type, see {@link DirectedGraph} for requirements on this type.
   * @param <E> Edge type, must be an {@link ImmutableEdge} of <code>V</code>.
   *
   * @param edges {@link Collection} of <code>Edge</code> objects to use for construction
   * @param checkCompatibility whether or not to check vertex and edge list to be compatible
   * @return the built {@link ImmutableDirectedGraph}
   */
  public static <V, E extends ImmutableEdge<V>> ImmutableDirectedGraph<V, E> construct(
      final Collection<E> edges, final boolean checkCompatibility) {
    // Collect the vertices in the same order as in edges
    List<V> vertices = new ArrayList<>();
    Set<V> vertexSet = new HashSet<>();
    for (E edge : edges) {
      if (!vertexSet.contains(edge.getSource())) {
        vertexSet.add(edge.getSource());
        vertices.add(edge.getSource());
      }
      if (!vertexSet.contains(edge.getDest())) {
        vertexSet.add(edge.getDest());
        vertices.add(edge.getDest());
      }
    }
    // Forward to implementation
    return construct(vertices, edges, checkCompatibility);
  }

  /**
   * Construct a new {@link ImmutableDirectedGraph} from a collection of vertices and edges.
   *
   * <p>
   * This is just a forward to <code>construct(vertices, edges, false);</code>
   * </p>
   *
   * @param <V> Vertex type, see {@link DirectedGraph} for requirements on this type.
   * @param <E> Edge type, must be an {@link ImmutableEdge} of <code>V</code>.
   *
   * @param vertices {@link Collection} of vertices to use for construction.
   * @param edges {@link Collection} of edges to use for construction.
   * @return Freshly built {@link ImmutableDirectedGraph}.
   */
  public static <V, E extends ImmutableEdge<V>> ImmutableDirectedGraph<V, E> construct(
      final Collection<V> vertices, final Collection<E> edges) {
    return construct(vertices, edges, false);
  }

  /**
   * Construct a new {@link ImmutableDirectedGraph} from a collection of edges.
   *
   * <p>
   * This is just a forward to <code>construct(edges, false);</code>
   * </p>
   *
   * @param <V> Vertex type, see {@link DirectedGraph} for requirements on this type.
   * @param <E> Edge type, must be an {@link ImmutableEdge} of <code>V</code>.
   *
   * @param edges {@link Collection} of edges to use for construction.
   * @return Freshly built {@link ImmutableDirectedGraph}.
   */
  public static <V, E extends ImmutableEdge<V>> ImmutableDirectedGraph<V, E> construct(
      final Collection<E> edges) {
    return construct(edges, false);
  }

  /**
   * This constructor is used internally for constructing via the static <code>create</code>
   * functions.
   *
   * @param vertices to use for constructing the graph with
   * @param edges to use for constructing the graph with.
   */
  private ImmutableDirectedGraph(final Collection<V> vertices, final Collection<E> edges) {
    // Construct mapping from vertex to builder
    Map<V, ImmutableVertexEdgeList.Builder<V, E>> builders =
        new HashMap<V, ImmutableVertexEdgeList.Builder<V, E>>();
    for (V v : vertices) {
      builders.put(v, ImmutableVertexEdgeList.<V, E>builder());
    }
    // Fill edge builders
    for (E e : edges) {
      builders.get(e.getSource()).addOutEdge(e);
      builders.get(e.getDest()).addInEdge(e);
    }
    // Fill ImmutableMap builder and construct
    com.google.common.collect.ImmutableMap.Builder<V, ImmutableVertexEdgeList<V, E>> builder =
        ImmutableMap.<V, ImmutableVertexEdgeList<V, E>>builder();
    for (Entry<V, ImmutableVertexEdgeList.Builder<V, E>> e : builders.entrySet()) {
      builder.put(e.getKey(), e.getValue().build());
    }
    this.edgeLists = builder.build();
    this.edgeCount = this.edgeLists.values().stream().mapToInt(x -> x.getInEdges().size()).sum();
  }

  /**
   * Check compatibility of <code>vertices</code> and <code>edges</code>, i.e., there must not be a
   * vertex in <code>edges</code> that is not present in <code>vertices</code>.
   *
   * @raises VerticesAndEdgesIncompatibleException in case of incompatibilities.
   */
  private static <Vertex> void checkCompatibility(Collection<Vertex> vertices,
      Collection<? extends Edge<Vertex>> edges) {
    LOGGER.info("Checking vertices ({}) and edges ({}) for compatibility...",
        new Object[] {vertices.size(), edges.size()});

    Set<Vertex> vertexSet = new HashSet<>(vertices);
    for (Edge<Vertex> edge : edges) {
      if (!vertexSet.contains(edge.getSource())) {
        throw new VerticesAndEdgesIncompatibleException("Unknown source edge in edge " + edge);
      }
      if (!vertexSet.contains(edge.getDest())) {
        throw new VerticesAndEdgesIncompatibleException("Unknown dest edge in edge " + edge);
      }
      if (edge.getSource() == edge.getDest()) {
        throw new VerticesAndEdgesIncompatibleException("Self-loop edge " + edge);
      }
    }

    LOGGER.info("Vertices and edges are compatible!");

    // TODO: ensure the graph is simple?
  }

  @Override
  public boolean containsVertex(V v) {
    return edgeLists.containsKey(v);
  }

  @Override
  public int countVertices() {
    return edgeLists.size();
  }

  @Override
  public Collection<V> getVertices() {
    return edgeLists.keySet();
  }

  @Override
  public Iterator<V> vertexIterator() {
    return edgeLists.keySet().iterator();
  }

  @Override
  public int countEdges() {
    return edgeCount;
  }

  @Override
  public boolean containsEdgeFromTo(V s, V t) {
    return getEdge(s, t) != null;
  }

  @Override
  public Collection<E> getEdges() {
    final List<E> result = new ArrayList<>();
    for (ImmutableVertexEdgeList<V, E> l : edgeLists.values()) {
      result.addAll(l.getOutEdges());
    }
    return result;
  }

  @Override
  public Iterator<E> edgeIterator() {
    return new Iterator<E>() {
      UnmodifiableIterator<ImmutableVertexEdgeList<V, E>> outerIt = edgeLists.values().iterator();
      private UnmodifiableIterator<E> innerIt = null;

      @Override
      public boolean hasNext() {
        if (innerIt != null && innerIt.hasNext()) {
          return true;
        } else {
          while (innerIt == null || !innerIt.hasNext()) {
            if (!outerIt.hasNext()) {
              return false;
            } else {
              innerIt = outerIt.next().getOutEdges().iterator();
              if (innerIt.hasNext()) {
                return true;
              }
            }
          }
          return innerIt.hasNext();
        }
      }

      @Override
      public E next() {
        return innerIt.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public E getEdge(V s, V t) {
    final ImmutableVertexEdgeList<V, E> edgeList = edgeLists.get(s);
    for (E e : edgeList.getOutEdges()) {
      if (e.getSource().equals(s) && e.getDest().equals(t)) {
        return e;
      }
    }
    return null;
  }

  @Override
  public int inDegree(V v) {
    return edgeLists.get(v).getInEdges().size();
  }

  @Override
  public Iterator<E> inEdgeIterator(V v) {
    return edgeLists.get(v).getInEdges().iterator();
  }

  @Override
  public Iterator<V> viaInEdgeIterator(V v) {
    final Iterator<? extends Edge<V>> iter = inEdgeIterator(v);

    return new Iterator<V>() {
      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @Override
      public V next() {
        return iter.next().getDest();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public int outDegree(V v) {
    return edgeLists.get(v).getOutEdges().size();
  }

  @Override
  public Iterator<E> outEdgeIterator(V v) {
    return edgeLists.get(v).getInEdges().iterator();
  }

  @Override
  public Iterator<V> viaOutEdgeIterator(V v) {
    final Iterator<? extends Edge<V>> iter = outEdgeIterator(v);

    return new Iterator<V>() {
      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @Override
      public V next() {
        return iter.next().getDest();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @SuppressWarnings("unchecked")
  @Override
  public DirectedGraph<V, E> subGraph(Collection<V> vertices) {
    Set<V> argVertexSet = ImmutableSet.copyOf(vertices);

    // Create subset of vertices and edges
    Set<V> vertexSubset = new HashSet<V>();
    Iterator<V> vIt = vertexIterator();
    while (vIt.hasNext()) {
      final V v = vIt.next();
      if (argVertexSet.contains(v)) {
        vertexSubset.add(v);
      }
    }
    Set<E> edgeSubset = new HashSet<E>();
    Iterator<E> eIt = edgeIterator();
    while (eIt.hasNext()) {
      E e = eIt.next();
      if (argVertexSet.contains(e.getSource()) && argVertexSet.contains(e.getDest())) {
        edgeSubset.add((E) e.clone());
      }
    }

    // Construct sub graph
    return construct(vertexSubset, edgeSubset);
  }

  @Override
  public String toString() {
    return "ImmutableDirectedGraph [edgeLists=" + ImmutableSortedMap.copyOf(edgeLists)
        + ", edgeCount=" + edgeCount + "]";
  }

  /**
   * Helper class for iteratively constructing immutable directed graphs.
   *
   * <p>
   * Note that when using the {@link #addEdge(Object, Object)} API then the edge identifiers are
   * taken using the edge factory that can be retrieved with {@link #getEdgeFactory()}. When mixing
   * this function call with calls to {@link #addEdge(ImmutableEdge)}, take good care to update the
   * next edge ID to give out via {@link ImmutableEdge.Factory#setNextEdgeID(int)}.
   * </p>
   *
   * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
   */
  public static class Builder<V, E extends ImmutableEdge<V>> {

    /** {@link Logger} object to use. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

    /** Vertices added to the builder so far. */
    private final List<V> vertices = new ArrayList<V>();

    /** Edges added to the builder so far. */
    private final List<E> edges = new ArrayList<E>();

    /** Edge factory to use. */
    private final Edge.Factory<V, E> edgeFactory;

    /**
     * Default constructor.
     *
     * @param edgeFactory {@link Edge.Factory} to use for constructing edges of type <code>E</code>.
     */
    public Builder(final Edge.Factory<V, E> edgeFactory) {
      this.edgeFactory = edgeFactory;
    }

    /**
     * Add <code>V</code> to add to the builder.
     *
     * @param v <code>V</code> to add to the builder.
     */
    public final void addVertex(final V v) {
      vertices.add(v);
    }

    /**
     * Add {@link Collection} of <code>V</code> objects to the builder.
     *
     * @param vs <code>V</code> objects to add to the builder.
     */
    public final void addVertices(final Collection<V> vs) {
      vertices.addAll(vs);
    }

    /**
     * Add {@link ImmutableEdge} to the builder.
     *
     * @param e {@link ImmutableEdge} to add to the builder.
     */
    public final void addEdge(final E e) {
      edges.add(e);
    }

    /**
     * Construct and add new {@link ImmutableEdge} between <code>source</code> and
     * <code>dest</code>, any label/weights are set to default values.
     * 
     * <p>
     * Note that
     * </p>
     *
     * @param source Source vertex for the directed edge
     * @param dest Destination vertex for the directed edge
     */
    public final void addEdge(final V source, final V dest) {
      edges.add(edgeFactory.construct(source, dest));
    }

    /**
     * @return Edge factory that is used for constructing objects.
     */
    public Edge.Factory<V, E> getEdgeFactory() {
      return edgeFactory;
    }

    /**
     * Build and return new {@link ImmutableDirectedGraph}.
     *
     * @param checkConsistency whether or not to check consistency of vertex and edge list.
     * @return Freshly built {@link ImmutableDirectedGraph}
     */
    public final ImmutableDirectedGraph<V, E> build(final boolean checkConsistency) {
      // Ensure that no edge ID is seen twice.
      LOGGER.info("Checking edge IDs...");
      final Set<Integer> seen = new HashSet<>();
      for (E e : edges) {
        if (seen.contains(e.getID())) {
          throw new RuntimeException("Duplicate edge ID " + e.getID() + " in edge list!");
        }
        seen.add(e.getID());
      }
      LOGGER.info("Edge IDs are sane.");

      LOGGER.info("Building ImmutableDirectedGraph...");
      final ImmutableDirectedGraph<V, E> result =
          ImmutableDirectedGraph.construct(vertices, edges, checkConsistency);
      LOGGER.info("ImmutableDirectedGraph was successfully built.");
      return result;
    }

    /**
     * Build and return new {@link ImmutableDirectedGraph}.
     *
     * <p>
     * This is a forward to {@link #build(boolean)}, with argument <code>false</code>.
     * </p>
     * 
     * @return Freshly built {@link ImmutableDirectedGraph}
     */
    public final ImmutableDirectedGraph<V, E> build() {
      return build(false);
    }

  }

}
