package org.monarchinitiative.phenol.graph.util;

import java.util.*;
import java.util.stream.Collectors;

import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.OntologyGraphEdge;
import org.monarchinitiative.phenol.graph.RelationType;
import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.graph.exc.GraphNotConnectedException;
import org.monarchinitiative.phenol.graph.exc.GraphNotSimpleException;
import org.monarchinitiative.phenol.graph.exc.VerticesAndEdgesIncompatibleException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompatibilityChecker {
  private static final Logger LOGGER = LoggerFactory.getLogger(CompatibilityChecker.class);

  private CompatibilityChecker() {
  }

  /**
   * Check if the {@code vertices} and the {@code edges} can be used to build an {@link OntologyGraph}.
   * <p>
   * The {@code vertices} and {@code edges} must meet the following requirements:
   * <ul>
   *   <li>
   *     All subjects ({@linkplain OntologyGraphEdge#subject()}) and objects ({@linkplain OntologyGraphEdge#object()})
   *     must be present in {@code vertices}.
   *   </li>
   *   <li>
   *     No self-loops are allowed ({@linkplain OntologyGraphEdge#subject()} and
   *     {@linkplain OntologyGraphEdge#object()}) must not be the same vertex).
   *   </li>
   *   <li>
   *     No multi-edges are allowed - there must not be >1 edge of the same {@linkplain RelationType}
   *     between a pair of nodes.
   *   </li>
   *   <li>
   *     The graph must consist of <em>one</em> component - no disconnected graphs are allowed.
   *    </li>
   * </ul>
   * <p>
   *
   * @param vertices the vertices to check.
   * @param edges the edges to check.
   * @return {@code true} if the {@code vertices} and {@code edges} are compatible. The function never returns
   * {@code false}, an exception is thrown instead.
   * @throws VerticesAndEdgesIncompatibleException if vertices or edges are empty, if a subject/object vertex is missing in
   * {@code vertices} the first two conditions are unmet.
   * @throws GraphNotSimpleException if a multi-edge or a self-loop is found.
   * @throws GraphNotConnectedException if the vertices and edges represent a disconnected graph.
   */
  public static <V extends TermId> boolean checkCompatibility(Collection<V> vertices,
                                                              Collection<? extends OntologyGraphEdge<V>> edges) {
    LOGGER.debug("Checking vertices ({}) and edges ({}) for compatibility...", vertices.size(), edges.size());
    if (vertices.isEmpty() || edges.isEmpty())
      throw new VerticesAndEdgesIncompatibleException(
        String.format("Collection of vertices (%d) or edges (%d) must not be empty!", vertices.size(), edges.size())
      );

    checkVertexEdgeCompatibility(vertices, edges);
    LOGGER.debug("Vertices and edges are compatible!");

    checkIfGraphIsSimple(vertices, edges);
    LOGGER.debug("Graph is simple!");

    checkIfGraphIsConnected(vertices, edges);
    LOGGER.debug("Graph is connected!");
    return true;
  }

  private static <V extends TermId> void checkVertexEdgeCompatibility(Collection<V> vertices,
                                                                      Collection<? extends OntologyGraphEdge<V>> edges) {
    Set<V> vertexSet = new HashSet<>(vertices);
    for (OntologyGraphEdge<V> edge : edges) {
      if (!vertexSet.contains(edge.subject()))
        throw new VerticesAndEdgesIncompatibleException(String.format("Unknown subject %s in edge %s", edge.subject(), edge));
      if (!vertexSet.contains(edge.object()))
        throw new VerticesAndEdgesIncompatibleException(String.format("Unknown object %s in edge %s", edge.object(), edge));
    }
  }

  private static <V extends TermId> void checkIfGraphIsSimple(Collection<V> vertices,
                                                              Collection<? extends OntologyGraphEdge<V>> edges) {
    Map<V, Set<V>> seen = new HashMap<>(vertices.size());
    for (OntologyGraphEdge<V> edge : edges) {
      if (edge.subject().equals(edge.object()))
        throw new GraphNotSimpleException("Self-loop edge for " + edge.subject());

      Set<V> objects = seen.computeIfAbsent(edge.subject(), w -> new HashSet<>());
      if (objects.contains(edge.object()))
        throw new GraphNotSimpleException("Seen edge twice: " + edge);

      objects.add(edge.object());
    }
  }

  private static <V extends TermId> void checkIfGraphIsConnected(Collection<V> vertices,
                                                                 Collection<? extends OntologyGraphEdge<V>> edges) {
    // I) First, find the root
    V root;
    {
      // Create map for following is_a relationships up to the root
      Map<V, List<OntologyGraphEdge<V>>> edgeMap = new HashMap<>(vertices.size());
      for (OntologyGraphEdge<V> edge : edges)
          edgeMap.computeIfAbsent(edge.subject(), whatever -> new ArrayList<>()).add(edge);

      V current = edges.iterator().next().subject();
      while (true) {
        List<OntologyGraphEdge<V>> outgoing = edgeMap.getOrDefault(current, List.of());
        if (outgoing.isEmpty())
          break;
        else
          current = outgoing.get(0).object();
      }
      root = current;
    }

    // II) Next, traverse from the root to the leaves, keeping track of the seen vertices.
    Set<V> seen = new HashSet<>(vertices.size());
    {
      Map<V, List<OntologyGraphEdge<V>>> edgeMap = new HashMap<>(vertices.size());
      for (OntologyGraphEdge<V> edge : edges)
        edgeMap.computeIfAbsent(edge.object(), whatever -> new ArrayList<>()).add(edge);

      // Initialize before looping/traversal.
      Queue<OntologyGraphEdge<V>> buffer = new ArrayDeque<>(edgeMap.getOrDefault(root, List.of()));
      seen.add(root);

      // Traverse
      while (!buffer.isEmpty()) {
        OntologyGraphEdge<V> edge = buffer.poll();
        V current = edge.subject();
        seen.add(current);
        buffer.addAll(edgeMap.getOrDefault(current, List.of()));
      }
    }

    // III) Last, check and report unseen/unvisited vertices.
    Set<V> vertexSet = new HashSet<>(vertices);
    vertexSet.removeAll(seen);
    if (!vertexSet.isEmpty()) {
      String verticesSummary = vertexSet.stream()
        .limit(10)
        .map(V::getValue)
        .sorted()
        .collect(Collectors.joining(", ", "{", "}"));
      String msg = vertexSet.size() > 10
        ? "Found %s disconnected vertices. The first 10 vertices: %s"
        : "Found %s disconnected vertices: %s";
      String message = String.format(msg, vertexSet.size(), verticesSummary);
      throw new GraphNotConnectedException(message);
    }
  }

  /**
   * Check compatibility of <code>vertices</code> and <code>edges</code>, i.e., there must not be a
   * vertex in <code>edges</code> that is not present in <code>vertices</code>.
   *
   * @throws  VerticesAndEdgesIncompatibleException in case of incompatibilities.
   */
  @SuppressWarnings("unchecked")
  public static <V extends Comparable<V>> void check(Collection<V> vertices, Collection<IdLabeledEdge> edges) {
    LOGGER.debug("Checking vertices ({}) and edges ({}) for compatibility...", vertices.size(), edges.size());

    Set<V> vertexSet = new HashSet<>(vertices);
    for (IdLabeledEdge edge : edges) {
      if (!vertexSet.contains(edge.getSource())) {
        throw new VerticesAndEdgesIncompatibleException("Unknown source edge in edge " + edge);
      }
      if (!vertexSet.contains(edge.getTarget())) {
        throw new VerticesAndEdgesIncompatibleException("Unknown dest edge in edge " + edge);
      }
      if (edge.getSource() == edge.getTarget()) {
        throw new VerticesAndEdgesIncompatibleException("Self-loop edge " + edge);
      }
    }

    LOGGER.debug("Vertices and edges are compatible!");

    final Map<V, Set<V>> seen = new HashMap<>();
    for (IdLabeledEdge edge : edges) {
      if (!seen.containsKey(edge.getSource())) {
        seen.put((V) edge.getSource(), new HashSet<>());
      } else {
        if (seen.get(edge.getSource()).contains(edge.getTarget())) {
          throw new GraphNotSimpleException("Seen edge twice: " + edge);
        }
      }
      seen.get(edge.getSource()).add((V) edge.getTarget());
    }
    LOGGER.debug("Graph is simple!");
  }
}
