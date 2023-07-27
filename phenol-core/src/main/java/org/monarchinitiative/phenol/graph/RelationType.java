package org.monarchinitiative.phenol.graph;

import java.util.Map;

/**
 * A description of the relation between two {@link OntologyGraph} nodes.
 * <p>
 * Note: the implementation must take extra care to correctly implement {@link #hashCode()} and {@link #equals(Object)}
 * to enable using {@linkplain RelationType} in {@linkplain Map}s.
 *
 * @see OntologyGraph
 * @see OntologyGraphEdge
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public interface RelationType {

  static RelationType of(String id, String label, boolean propagates) {
    return new RelationTypeDefault(id, label, propagates);
  }

  /**
   * Get a unique identifier of the relation type. No two {@link RelationType}s can have the same {@code id()}.
   *
   * @return a unique relation type identifier.
   */
  String id();

  /**
   * Get a human-friendly relation type label.
   *
   * @return a relation type label
   */
  String label();

  /**
   * Flag to indicate if the relation propagates under the annotation propagation rule.
   *
   * @return {@code true} if the relation propagates and {@code false} otherwise.
   */
  boolean propagates();

  int hashCode();

  boolean equals(Object o);
}
