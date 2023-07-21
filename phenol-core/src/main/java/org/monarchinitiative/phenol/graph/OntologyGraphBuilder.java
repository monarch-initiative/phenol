package org.monarchinitiative.phenol.graph;

import java.util.Collection;

/**
 * {@linkplain OntologyGraphBuilder} builds the {@link OntologyGraph}.
 *
 * @param <T> type of the term/graph node.
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public interface OntologyGraphBuilder<T> {

  /**
   * Set the {@linkplain RelationType} to use as the main graph hierarchy.
   */
  OntologyGraphBuilder<T> hierarchyRelation(RelationType relationType);

  /**
   * Build the graph from the edges.
   */
  OntologyGraph<T> build(Collection<OntologyGraphEdge<T>> edges);

}
