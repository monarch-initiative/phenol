package org.monarchinitiative.phenol.graph.csr.poly;

import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.graph.csr.ItemsNotSortedException;
import org.monarchinitiative.phenol.graph.csr.util.Util;
import org.monarchinitiative.phenol.utils.IterableIteratorWrapper;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An {@link OntologyGraph} backed by an adjacency matrix in CSR format.
 * <p>
 * Besides implementing {@linkplain OntologyGraph}, the class supports storing graphs with multiple edge types.
 * <p>
 * The traversals are implemented using iterators to prevent unnecessary allocation.
 * <p>
 * The array of nodes must be sorted and no duplicate elements must be present. An exception is thrown otherwise.
 *
 * @param <T> node type.
 * @param <E> data type for storing the relationships between graph nodes.
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class CsrPolyOntologyGraph<T, E> implements OntologyGraph<T> {

  private final T root;
  private final T[] nodes;
  private final StaticCsrArray<E> adjacencyMatrix;
  private final Comparator<T> comparator;
  private final Predicate<E> hierarchy;
  private final Predicate<E> hierarchyInverted;

  public CsrPolyOntologyGraph(T root,
                              T[] nodes,
                              StaticCsrArray<E> adjacencyMatrix,
                              Comparator<T> comparator,
                              Predicate<E> hierarchy,
                              Predicate<E> hierarchyInverted) {
    this.root = Objects.requireNonNull(root);
    this.nodes = checkSorted(Objects.requireNonNull(nodes), Objects.requireNonNull(comparator));
    this.adjacencyMatrix = Objects.requireNonNull(adjacencyMatrix);
    this.comparator = comparator;
    this.hierarchy = Objects.requireNonNull(hierarchy);
    this.hierarchyInverted = Objects.requireNonNull(hierarchyInverted);
  }

  StaticCsrArray<E> adjacencyMatrix() {
    return adjacencyMatrix;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T root() {
    return root;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterable<T> getChildren(T source, boolean includeSource) {
    int idx = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);
    Supplier<Iterator<Integer>> base = () -> getColsWithRelationship(idx, hierarchyInverted, includeSource);
    return new IterableIteratorWrapper<>(() -> new InfallibleMappingIterator<>(base, this::getNodeForIndex));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterable<T> getDescendants(T source, boolean includeSource) {
    int idx = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);
    Supplier<Iterator<Integer>> base = () -> traverseGraph(idx, hierarchyInverted, includeSource);
    return new IterableIteratorWrapper<>(() -> new InfallibleMappingIterator<>(base, this::getNodeForIndex));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterable<T> getParents(T source, boolean includeSource) {
    int idx = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);
    Supplier<Iterator<Integer>> base = () -> getColsWithRelationship(idx, hierarchy, includeSource);
    return new IterableIteratorWrapper<>(() -> new InfallibleMappingIterator<>(base, this::getNodeForIndex));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterable<T> getAncestors(T source, boolean includeSource) {
    int idx = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);
    Supplier<Iterator<Integer>> base = () -> traverseGraph(idx, hierarchy, includeSource);
    return new IterableIteratorWrapper<>(() -> new InfallibleMappingIterator<>(base, this::getNodeForIndex));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLeaf(T source) {
    int idx = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);
    return !getNodeIndicesWithRelationship(idx, hierarchyInverted).hasNext();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return nodes.length;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<T> iterator() {
    return Stream.of(nodes).iterator();
  }

  private static <T> T[] checkSorted(T[] a, Comparator<T> comparator) {
    if (a.length == 0)
      return a;

    T previous = a[0];
    for (int i = 1; i < a.length; i++) {
      T current = a[i];
      if (comparator.compare(previous, current) >= 0) {
        throw new ItemsNotSortedException(
          String.format("Unsorted sequence. Item #%d (%s) was less than #%d (%s)", i, current, i-1, previous)
        );
      }
    }

    return a;
  }

  private Iterator<Integer> getNodeIndicesWithRelationship(int sourceIdx, Predicate<E> relationship) {
    return getColsWithRelationship(sourceIdx, relationship, false);
  }

  private Iterator<Integer> getColsWithRelationship(int source,
                                                    Predicate<E> relationship,
                                                    boolean includeSource) {
    Iterator<Integer> base = adjacencyMatrix.colIndicesOfVal(source, relationship);
    return new NodeIndexIterator(source, includeSource, base);
  }

  private T getNodeForIndex(int idx) {
    return nodes[idx];
  }

  private Iterator<Integer> traverseGraph(int sourceIdx,
                                          Predicate<E> relationship,
                                          boolean includeSource) {
    return new TraversingIterator(sourceIdx, relationship, includeSource);
  }

  /**
   * An iterator for traversing the ontology graph starting from the {@code source} up to the root or the leaf.
   */
  private class TraversingIterator implements Iterator<Integer> {

    private final Set<Integer> seen = new HashSet<>();
    private final Deque<Integer> buffer = new ArrayDeque<>();
    private final Predicate<E> relationship;

    private TraversingIterator(int sourceIdx,
                               Predicate<E> relationship,
                               boolean includeSource) {
      this.relationship = relationship;
      Iterator<Integer> base = getColsWithRelationship(sourceIdx, relationship, includeSource);
      while (base.hasNext()) {
        int idx = base.next();
        seen.add(idx);
        buffer.add(idx);
      }
    }

    @Override
    public boolean hasNext() {
      return !buffer.isEmpty();
    }

    @Override
    public Integer next() {
      int current = buffer.pop();
      Iterator<Integer> iter = getColsWithRelationship(current, relationship, false);
      while (iter.hasNext()) {
        int idx = iter.next();
        if (!seen.contains(idx)) {
          seen.add(idx);
          buffer.add(idx);
        }
      }
      return current;
    }
  }

}
