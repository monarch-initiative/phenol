package org.monarchinitiative.phenol.graph.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.exc.GraphNotSimpleException;
import org.monarchinitiative.phenol.graph.exc.VerticesAndEdgesIncompatibleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompatibilityChecker {
  private static final Logger LOGGER = LoggerFactory.getLogger(CompatibilityChecker.class);

  /**
   * Check compatibility of <code>vertices</code> and <code>edges</code>, i.e., there must not be a
   * vertex in <code>edges</code> that is not present in <code>vertices</code>.
   *
   * @throws  VerticesAndEdgesIncompatibleException in case of incompatibilities.
   */
  @SuppressWarnings("unchecked")
  public static <V extends Comparable<V>> void check(
      Collection<V> vertices, Collection<IdLabeledEdge> edges) {
    LOGGER.info(
        "Checking vertices ({}) and edges ({}) for compatibility...",
        new Object[] {vertices.size(), edges.size()});

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

    LOGGER.info("Vertices and edges are compatible!");

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
    LOGGER.info("Graph is simple!");
  }
}
