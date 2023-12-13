package org.monarchinitiative.phenol.graph;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
   * Get a {@linkplain Set} with <em>children</em> of the {@code source} node.
   *
   * @param source node whose children we are interested in.
   * @return the set with child nodes.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  Set<T> getChildren(T source);

  /**
   * Get an {@linkplain Iterable} over <em>children</em> of the {@code source} node.
   *
   * @param source        node whose children we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Iterable}
   *                      or {@code false} otherwise.
   * @return an iterable as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   * @deprecated use {@link #getChildren(Object)} if you do not wish to see {@code source} in the iterable
   * or {@link #extendWithChildren(Object, boolean)} if you need the {@code source} in the result.
   */
  // REMOVE[3.0.0]
  @Deprecated(forRemoval = true, since = "2.0.5")
  default Iterable<T> getChildren(T source, boolean includeSource) {
    Set<T> children = new HashSet<>();
    extendWithChildren(source, includeSource, children);
    return children;
  }

  /**
   * Prepare a collection with <em>children</em> of the {@code source} node.
   * <p>
   * Note: consider using {@link #extendWithChildren(Object, boolean, Collection)} if you already have a collection.
   *
   * @param source        node whose children we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Collection}
   *                      or {@code false} otherwise.
   * @return the collection with the child nodes.
   */
  default Collection<? super T> extendWithChildren(T source, boolean includeSource) {
    Collection<? super T> collection = new ArrayList<>();
    extendWithChildren(source, includeSource, collection);
    return collection;
  }

  /**
   * Extend an existing collection with <em>children</em> of the {@code source} node.
   *
   * @param source        node whose children we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Collection}
   *                      or {@code false} otherwise.
   * @param collection    the existing collection.
   */
  default void extendWithChildren(T source,
                                  boolean includeSource,
                                  Collection<? super T> collection) {
    if (includeSource)
      collection.add(source);
    collection.addAll(getChildren(source));
  }

  /**
   * Get a {@linkplain Stream} over <em>children</em> of the {@code source} node.
   *
   * @param source        node whose children we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Stream}
   *                      or {@code false} otherwise.
   * @return a stream as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   * @deprecated use {@link #getChildrenStream(Object)} or {@link #extendWithChildren(Object, boolean)}
   * instead.
   */
  // REMOVE[3.0.0]
  @Deprecated(forRemoval = true, since = "2.0.5")
  default Stream<T> getChildrenStream(T source, boolean includeSource) {
    return getSequentialStream(getChildren(source, includeSource).spliterator());
  }

  /**
   * Get a {@linkplain Stream} over <em>children</em> of the {@code source} node.
   *
   * @param source node whose children we are interested in.
   * @return a stream.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  default Stream<T> getChildrenStream(T source) {
    return getChildren(source).stream();
  }

  /**
   * Get an {@linkplain Iterable} with <em>descendants</em> of the {@code source} node.
   *
   * @param source node whose descendants we are interested in.
   * @return an iterable as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  Iterable<T> getDescendants(T source);

  /**
   * Get an {@linkplain Iterable} over <em>descendants</em> of the {@code source} node.
   *
   * @param source        node whose descendants we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Iterable}
   *                      or {@code false} otherwise.
   * @return an iterable as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   * @deprecated use {@link #getDescendants(Object)} if you do <em>not</em> want to include the source term in the iterator
   * or {@link #extendWithDescendants(Object, boolean)} if you do.
   */
  // REMOVE[3.0.0]
  @Deprecated(forRemoval = true, since = "2.0.5")
  default Iterable<T> getDescendants(T source, boolean includeSource) {
    Set<T> descendants = new HashSet<>();
    extendWithDescendants(source, includeSource, descendants);
    return descendants;
  }

  /**
   * Get a collection with <em>descendants</em> of the {@code source} node.
   *
   * @param source        node whose descendants we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Collection}
   *                      or {@code false} otherwise.
   * @return a collection with the descendant nodes.
   */
  default Collection<? super T> extendWithDescendants(T source, boolean includeSource) {
    return extendWithDescendants(source, includeSource, ArrayList::new);
  }

  /**
   * Extend a new instance of {@link C} obtained from the {@code supplier} with <em>descendants</em>
   * of the {@code source} node.
   *
   * @param source        node whose descendants we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Collection}
   *                      or {@code false} otherwise.
   * @param supplier      the existing collection.
   * @param <C>           the desired collection type
   */
  default <C extends Collection<? super T>> C extendWithDescendants(T source,
                                                                    boolean includeSource,
                                                                    Supplier<C> supplier) {
    C collection = supplier.get();
    extendWithDescendants(source, includeSource, collection);
    return collection;
  }

  /**
   * Extend an existing collection with <em>descendants</em> of the {@code source} node.
   *
   * @param source        node whose descendants we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Collection}
   *                      or {@code false} otherwise.
   * @param collection    the existing collection.
   */
  default void extendWithDescendants(T source,
                                     boolean includeSource,
                                     Collection<? super T> collection) {
    if (includeSource)
      collection.add(source);
    getDescendants(source).forEach(collection::add);
  }

  /**
   * Get a {@linkplain Stream} over <em>descendants</em> of the {@code source} node.
   *
   * @param source        node whose descendants we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Stream}
   *                      or {@code false} otherwise.
   * @return a stream as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   * @deprecated use {@link #getDescendantsStream(Object)} if you do not wish to see the {@code source} in the stream
   * or {@link #extendWithDescendants(Object, boolean)} if you want to include the {@code source}.
   * However, consider using {@link #extendWithDescendants(Object, boolean, Collection)} instead.
   */
  // REMOVE[3.0.0]
  @Deprecated(forRemoval = true, since = "2.0.5")
  default Stream<T> getDescendantsStream(T source, boolean includeSource) {
    return getSequentialStream(getDescendants(source, includeSource).spliterator());
  }

  /**
   * Get a {@linkplain Stream} over <em>descendants</em> of the {@code source} node.
   *
   * @param source node whose descendants we are interested in.
   * @return a stream as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  default Stream<T> getDescendantsStream(T source) {
    return getSequentialStream(getDescendants(source).spliterator());
  }

  /**
   * Get a {@linkplain Set} with <em>descendants</em> of the {@code source} node.
   * <p>
   * Note: consider using {@link #getDescendants(Object)} if all you need is to iterate
   * or {@link #extendWithDescendants(Object, boolean, Collection)} if you wish to gather descendants
   * of several ontology nodes.
   *
   * @param source node whose descendants we are interested in.
   * @return a stream with descendants.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  default Set<T> getDescendantSet(T source) {
    return getDescendantsStream(source).collect(Collectors.toSet());
  }

  /**
   * Get an {@linkplain Iterable} over <em>parents</em> of the {@code source} node.
   *
   * @param source node whose parents we are interested in.
   * @return an iterable as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  Set<T> getParents(T source);

  /**
   * Get an {@linkplain Iterable} over <em>parents</em> of the {@code source} node.
   *
   * @param source        node whose parents we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Iterable}
   *                      or {@code false} otherwise.
   * @return an iterable as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   * @deprecated use {@link #getParents(Object)} if you do not wish to see {@code source} in the iterable
   * or {@link #extendWithParents(Object, boolean)} if you need the {@code source} in the result.
   */
  // REMOVE[3.0.0]
  @Deprecated(forRemoval = true, since = "2.0.5")
  default Iterable<T> getParents(T source, boolean includeSource) {
    Set<T> parents = new HashSet<>();
    extendWithParents(source, includeSource, parents);
    return parents;
  }

  /**
   * Prepare a collection with <em>parents</em> of the {@code source} node.
   * <p>
   * Note: consider using {@link #extendWithParents(Object, boolean, Collection)} if you already have a collection.
   *
   * @param source        node whose parents we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Collection}
   *                      or {@code false} otherwise.
   * @return the collection with the parent nodes.
   */
  default Collection<? super T> extendWithParents(T source, boolean includeSource) {
    Collection<? super T> collection = new ArrayList<>();
    extendWithParents(source, includeSource, collection);
    return collection;
  }

  /**
   * Extend an existing collection with <em>parents</em> of the {@code source} node.
   *
   * @param source        node whose parents we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Collection}
   *                      or {@code false} otherwise.
   * @param collection        the existing collection.
   */
  default void extendWithParents(T source,
                                 boolean includeSource,
                                 Collection<? super T> collection) {
    if (includeSource)
      collection.add(source);
    collection.addAll(getParents(source));
  }


  /**
   * Get a {@linkplain Stream} over <em>parents</em> of the {@code source} node.
   *
   * @param source        node whose parents we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Stream}
   *                      or {@code false} otherwise.
   * @return a stream as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   * @deprecated use {@link #getParentsStream(Object)} or {@link #extendWithParents(Object, boolean, Collection)}
   * instead.
   */
  // REMOVE[3.0.0]
  @Deprecated(forRemoval = true, since = "2.0.5")
  default Stream<T> getParentsStream(T source, boolean includeSource) {
    return getSequentialStream(getParents(source, includeSource).spliterator());
  }

  /**
   * Get a {@linkplain Stream} over <em>parents</em> of the {@code source} node.
   *
   * @param source node whose parents we are interested in.
   * @return a stream as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  default Stream<T> getParentsStream(T source) {
    return getParents(source).stream();
  }

  /**
   * Get an {@linkplain Iterable} over <em>ancestors</em> of the {@code source} node.
   *
   * @param source node whose ancestors we are interested in.
   * @return an iterable as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  Iterable<T> getAncestors(T source);

  /**
   * Get an {@linkplain Iterable} over <em>ancestors</em> of the {@code source} node.
   *
   * @param source        node whose ancestors we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Iterable}
   *                      or {@code false} otherwise.
   * @return an iterable as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   * @deprecated use {@link #getAncestors(Object)} if you do not want to include the source term in the iterator or
   * {@link #extendWithAncestors(Object, boolean, Collection)} if you do.
   */
  // REMOVE[3.0.0]
  @Deprecated(forRemoval = true, since = "2.0.5")
  Iterable<T> getAncestors(T source, boolean includeSource);

  /**
   * Get a collection with <em>ancestors</em> of the {@code source} node.
   *
   * @param source        node whose ancestors we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Collection}
   *                      or {@code false} otherwise.
   * @return a collection with the ancestors nodes.
   */
  default Collection<? super T> extendWithAncestors(T source, boolean includeSource) {
    return extendWithAncestors(source, includeSource, ArrayList::new);
  }

  /**
   * Extend a new instance of {@link C} obtained from the {@code supplier} with <em>ancestors</em>
   * of the {@code source} node.
   *
   * @param source        node whose ancestors we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Collection}
   *                      or {@code false} otherwise.
   * @param supplier      the existing collection.
   * @param <C>           the desired collection type
   */
  default <C extends Collection<? super T>> C extendWithAncestors(T source,
                                                                  boolean includeSource,
                                                                  Supplier<C> supplier) {
    C ancestors = supplier.get();
    extendWithAncestors(source, includeSource, ancestors);
    return ancestors;
  }

  /**
   * Extend an existing collection with <em>ancestors</em> of the {@code source} node.
   *
   * @param source        node whose ancestors we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Collection}
   *                      or {@code false} otherwise.
   * @param collection        the existing collection.
   */
  default void extendWithAncestors(T source,
                                   boolean includeSource,
                                   Collection<? super T> collection) {
    if (includeSource)
      collection.add(source);
    getAncestors(source).forEach(collection::add);
  }

  /**
   * Get a {@linkplain Stream} over <em>ancestors</em> of the {@code source} node.
   *
   * @param source        node whose ancestors we are interested in.
   * @param includeSource {@code true} if the {@code source} should be included in the {@linkplain Stream}
   *                      or {@code false} otherwise.
   * @return a stream as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   * @deprecated use {@link #getAncestorsStream(Object)} if you do not wish to see the {@code source} in the stream
   * or {@link #extendWithAncestors(Object, boolean)} if you want to include the {@code source}.
   * However, consider using {@link #extendWithAncestors(Object, boolean, Collection)} instead.
   */
  // REMOVE[3.0.0]
  @Deprecated(forRemoval = true, since = "2.0.5")
  default Stream<T> getAncestorsStream(T source, boolean includeSource) {
    return getSequentialStream(getAncestors(source, includeSource).spliterator());
  }

  /**
   * Get a {@linkplain Stream} over <em>ancestors</em> of the {@code source} node.
   *
   * @param source node whose ancestors we are interested in.
   * @return a stream as described above.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  default Stream<T> getAncestorsStream(T source) {
    return getSequentialStream(getAncestors(source).spliterator());
  }

  /**
   * Get a {@linkplain Set} with <em>ancestors</em> of the {@code source} node.
   * <p>
   * Note: consider using {@link #getAncestors(Object)} if all you need is to iterate
   * or {@link #extendWithAncestors(Object, boolean, Collection)} if you wish to gather the ancestors
   * of several ontology nodes.
   *
   * @param source node whose ancestors we are interested in.
   * @return a stream with ancestors.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  default Set<T> getAncestorSet(T source) {
    return getAncestorsStream(source).collect(Collectors.toSet());
  }

  /**
   * Return <code>true</code> if the {@code source} is a leaf node - a node with no children.
   *
   * @param source a node of the ontology.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  default boolean isLeaf(T source) {
    for (T ignored : getChildren(source)) {
      return false;
    }
    return true;
  }

  /**
   * Test if there is a path from {@code subject} to {@code object}. In other words, if {@code object} is an ancestor
   * of the {@code subject}.
   * <p>
   * Note: the function is equivalent to calling:
   * <pre>
   * getAncestorsStream(subject, false).anyMatch(t -> t.equals(object));
   * </pre>
   *
   * @return {@code true} if path exists from {@code subject} to {@code object} and {@code false} otherwise.
   * @throws NodeNotPresentInGraphException if the {@code source} is not a graph node.
   */
  default boolean existsPath(T subject, T object) {
    return getAncestorsStream(subject, false).anyMatch(t -> t.equals(object));
  }

  /**
   * Return <code>true</code> if the {@code subject} is a parent of the {@code object} and {@code false} otherwise
   * (including if the {@code subject} is not in the graph).
   *
   * @throws NodeNotPresentInGraphException if the {@code subject} or the {@code object} is not a graph node.
   */
  default boolean isParentOf(T subject, T object) {
    return runQuery(this::getParents, subject, object);
  }

  /**
   * Return <code>true</code> if the {@code subject} is an ancestor of the {@code object} and {@code false} otherwise
   * (including if the {@code subject} is not in the graph)..
   *
   * @throws NodeNotPresentInGraphException if the {@code subject} or the {@code object} is not a graph node.
   */
  default boolean isAncestorOf(T subject, T object) {
    return runQuery(this::getAncestors, subject, object);
  }

  /**
   * Return <code>true</code> if the {@code subject} is a child of the {@code object} and {@code false} otherwise
   * (including if the {@code subject} is not in the graph).
   *
   * @throws NodeNotPresentInGraphException if the {@code object} is not a graph node.
   */
  default boolean isChildOf(T subject, T object) {
    return runQuery(this::getChildren, subject, object);
  }

  /**
   * Return <code>true</code> if the {@code subject} is a descendant of the {@code object} and {@code false} otherwise
   * (including if the {@code subject} is not in the graph).
   *
   * @throws NodeNotPresentInGraphException if the {@code subject} or the {@code object} is not a graph node.
   */
  default boolean isDescendantOf(T subject, T object) {
    return runQuery(this::getDescendants, subject, object);
  }

  /**
   * Get the subgraph with {@code subRoot} as the new root node.
   */
  OntologyGraph<T> extractSubgraph(T subRoot);

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

  private static <T> Stream<T> getSequentialStream(Spliterator<T> spliterator) {
    return StreamSupport.stream(spliterator, false);
  }
}
