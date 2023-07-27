package org.monarchinitiative.phenol.graph;

/**
 * {@linkplain OntologyGraphEdge} represents a relation between two {@link OntologyGraph} nodes.
 * <p>
 * The edge is directed and thanks to its directionality, the edge can be read as a triple
 * <em>subject</em>-<em>predicate</em>-<em>object</em>
 * where the {@link #relationType()} is used as a <em>predicate</em>.
 *
 * @param <T> type of the {@link OntologyGraph} node.
 * @see OntologyGraph
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public interface OntologyGraphEdge<T> {

  static <T> OntologyGraphEdge<T> of(T subject, T object, RelationType relationType) {
    return new OntologyGraphEdgeDefault<>(subject, object, relationType);
  }

  /**
   * Get the subject node.
   *
   * @return the subject.
   */
  T subject();

  /**
   * Get the relation between the nodes.
   *
   * @return the relation.
   */
  RelationType relationType();

  /**
   * Get the object node.
   *
   * @return the object.
   */
  T object();

}
