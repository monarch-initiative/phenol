package org.monarchinitiative.phenol.graph;

import java.util.function.Function;

/**
 * {@linkplain OntologyGraph} is a directed acyclic graph that supports methods required for ontology-related tasks,
 * such as ontology traversals (i.e. getting parents or ancestors of a term {@link T}), and tests
 * (<em>subject</em> is parent of <em>object</em> etc.).
 * <p>
 * The graph has <em>one</em> {@code root} term.
 * <p>
 * All methods that take {@linkplain T} as an input throw {@link NodeNotPresentInGraphException} in case the node
 * is not a node of the graph.
 *
 * @param <T> type of the term/graph node.
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public interface OntologyGraph<T> extends Iterable<T> {

  /**
   * @return the root term of the ontology.
   */
  T root();

  /**
   * Get an {@linkplain Iterable} over <em>children</em> of the {@code source} node.
   *
   * @param source        node whose children we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Iterable}
   *                      or {@code false} otherwise.
   * @return an iterable as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  Iterable<T> getChildren(T source, boolean includeSource);

  /**
   * Get an {@linkplain Iterable} over <em>descendants</em> of the {@code source} node.
   *
   * @param source        node whose descendants we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Iterable}
   *                      or {@code false} otherwise.
   * @return an iterable as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  Iterable<T> getDescendants(T source, boolean includeSource);

  /**
   * Get an {@linkplain Iterable} over <em>parents</em> of the {@code source} node.
   *
   * @param source        node whose parents we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Iterable}
   *                      or {@code false} otherwise.
   * @return an iterable as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  Iterable<T> getParents(T source, boolean includeSource);

  /**
   * Get an {@linkplain Iterable} over <em>ancestors</em> of the {@code source} node.
   *
   * @param source        node whose ancestors we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Iterable}
   *                      or {@code false} otherwise.
   * @return an iterable as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  Iterable<T> getAncestors(T source, boolean includeSource);

  /**
   * Return <code>true</code> if the {@code source} is a leaf node - a node with no children.
   *
   * @param source a node of the ontology.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  default boolean isLeaf(T source) {
    return !getChildren(source, false)
      .iterator()
      .hasNext();
  }

  /**
   * Return <code>true</code> if the {@code subject} is a parent of the {@code object} and {@code false} otherwise
   * (including if the {@code subject} is not in the graph).
   *
   * @throws NodeNotPresentInGraphException if the {@code subject} or the {@code object} is not a graph node.
   */
  default boolean isParentOf(T subject, T object) {
    return runQuery(t -> getParents(t, false), subject, object);
  }

  /**
   * Return <code>true</code> if the {@code subject} is an ancestor of the {@code object} and {@code false} otherwise
   * (including if the {@code subject} is not in the graph)..
   *
   * @throws NodeNotPresentInGraphException if the {@code subject} or the {@code object} is not a graph node.
   */
  default boolean isAncestorOf(T subject, T object) {
    return runQuery(t -> getAncestors(t, false), subject, object);
  }

  /**
   * Return <code>true</code> if the {@code subject} is a child of the {@code object} and {@code false} otherwise
   * (including if the {@code subject} is not in the graph).
   *
   * @throws NodeNotPresentInGraphException if the {@code object} is not a graph node.
   */
  default boolean isChildOf(T subject, T object) {
    return runQuery(t -> getChildren(t, false), subject, object);
  }

  /**
   * Return <code>true</code> if the {@code subject} is a descendant of the {@code object} and {@code false} otherwise
   * (including if the {@code subject} is not in the graph).
   *
   * @throws NodeNotPresentInGraphException if the {@code subject} or the {@code object} is not a graph node.
   */
  default boolean isDescendantOf(T subject, T object) {
    return runQuery(t -> getDescendants(t, false), subject, object);
  }

  /**
   * Get the number of nodes in the graph.
   *
   * @return the number of nodes in the graph.
   */
  int size();

  private static <T> boolean runQuery(Function<T, Iterable<T>> func, T subject, T object) {
    for (T termId : func.apply(object)) {
      if (termId.equals(subject))
        return true;
    }
    return false;
  }
}
