package org.monarchinitiative.phenol.graph.csr.poly;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.graph.csr.ItemsNotSortedException;
import org.monarchinitiative.phenol.graph.csr.util.Util;
import org.monarchinitiative.phenol.utils.IterableIteratorWrapper;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
  private final List<T> nodes;
  private final StaticCsrArray<E> adjacencyMatrix;
  private final Comparator<T> comparator;
  private final Predicate<E> isParentOf;
  private final Predicate<E> isChildOf;

  CsrPolyOntologyGraph(T root,
                       List<T> nodes,
                       StaticCsrArray<E> adjacencyMatrix,
                       Comparator<T> comparator,
                       Predicate<E> isParentOf,
                       Predicate<E> isChildOf) {
    this.root = Objects.requireNonNull(root);
    this.nodes = checkSorted(Objects.requireNonNull(nodes), Objects.requireNonNull(comparator));
    this.adjacencyMatrix = Objects.requireNonNull(adjacencyMatrix);
    this.comparator = Objects.requireNonNull(comparator);
    this.isParentOf = Objects.requireNonNull(isParentOf);
    this.isChildOf = Objects.requireNonNull(isChildOf);
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
    Supplier<Iterator<Integer>> base = () -> getColsWithRelationship(idx, isChildOf, includeSource);
    return new IterableIteratorWrapper<>(() -> new InfallibleMappingIterator<>(base, this::getNodeForIndex));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterable<T> getDescendants(T source, boolean includeSource) {
    int idx = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);
    Supplier<Iterator<Integer>> base = () -> traverseGraph(idx, isChildOf, includeSource);
    return new IterableIteratorWrapper<>(() -> new InfallibleMappingIterator<>(base, this::getNodeForIndex));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterable<T> getParents(T source, boolean includeSource) {
    int idx = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);
    Supplier<Iterator<Integer>> base = () -> getColsWithRelationship(idx, isParentOf, includeSource);
    return new IterableIteratorWrapper<>(() -> new InfallibleMappingIterator<>(base, this::getNodeForIndex));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterable<T> getAncestors(T source, boolean includeSource) {
    int idx = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);
    Supplier<Iterator<Integer>> base = () -> traverseGraph(idx, isParentOf, includeSource);
    return new IterableIteratorWrapper<>(() -> new InfallibleMappingIterator<>(base, this::getNodeForIndex));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isLeaf(T source) {
    int idx = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);
    return !getNodeIndicesWithRelationship(idx, isChildOf).hasNext();
  }

  @Override
  public OntologyGraph<T> extractSubgraph(T subRoot) {
    if (subRoot.equals(root))
      return this; // No need to extract subgraph since the subgraph equals to the graph.

    int idx = Collections.binarySearch(nodes, subRoot, comparator);
    if (idx < 0)
      throw new PhenolRuntimeException(String.format("%s is not in the ontology", subRoot));

    List<T> subnodes = extractSubRootAndDescendantSubnodes(subRoot);
    StaticCsrArray<E> subArray = extractSubArray(subnodes);

    return new CsrPolyOntologyGraph<>(subRoot,
      subnodes,
      subArray,
      comparator,
      isParentOf,
      isChildOf);
  }

  private List<T> extractSubRootAndDescendantSubnodes(T subRoot) {
    List<T> nodes = new ArrayList<>();
    nodes.add(subRoot);
    getDescendants(subRoot, false)
      .forEach(nodes::add);
    nodes.sort(comparator);
    return List.copyOf(nodes);
  }

  private StaticCsrArray<E> extractSubArray(List<T> subnodes) {
    // Extract relevant columns of the CSR adjacency matrix.
    //
    // We know that `this.nodes` and `subnodes` are stored in the same order, since the `subnodes` are sorted
    // by the comparator. We also know that `subnodes` cannot be empty because we checked that `subRoot` is a graph node
    // and, consequently, must be present in `subnodes`. If `subRoot` is a leaf, then it is the only element
    // of the `subnodes` list.

    // Since we are removing columns, we will shift the columns to the left. Here we figure out the number of columns
    // to shift for each retained column.
    Map<Integer, Integer> shifts = calculateNodeShifts(nodes, subnodes);

    // We'll put the sub array data here.
    List<Integer> indptr = new ArrayList<>();
    indptr.add(0);
    List<Integer> indices = new ArrayList<>();
    List<E> data = new ArrayList<>();

    Iterator<T> iterator = subnodes.iterator();
    T subnode = iterator.next();

    for (int nodeIdx = 0; nodeIdx < nodes.size(); nodeIdx++) {
      T node = nodes.get(nodeIdx);

      if (node.equals(subnode)) { // We are keeping the node!
        // Get delimiters of the columns of interest - a slice of column indices in `indices()` array.
        int start = adjacencyMatrix.indptr()[nodeIdx];
        int end = adjacencyMatrix.indptr()[nodeIdx + 1];

        // Go over the columns, test if the column represents a node of interest, get the data,
        // and unset parent relationship if the current `subnode` is the new root.
        for (int j = start; j < end; j++) {
          int col = adjacencyMatrix.indices()[j];
          Integer shift = shifts.get(col);
          if (shift != null) { // `shift` is not null if `col` corresponds to a node of interest.
            indices.add(col - shift); // We must shift to compensate for the removed nodes.
            data.add(adjacencyMatrix.data().get(j));
          }
        }
        indptr.add(data.size());

        // Go to the next subnode. If there is no next subnode, then we're done!
        if (iterator.hasNext())
          subnode = iterator.next();
        else break;
      }
    }

    return new StaticCsrArray<>(Util.toIntArray(indptr), Util.toIntArray(indices), data);
  }

  private static <T> Map<Integer, Integer> calculateNodeShifts(List<T> nodes, List<T> subnodes) {
    Map<Integer, Integer> shifts = new HashMap<>(subnodes.size());
    int nSkippedCols = 0;
    ListIterator<T> iterator = subnodes.listIterator();
    T subnode = iterator.next();
    for (int i = 0; i < nodes.size(); i++) {
      T node = nodes.get(i);
      if (node.equals(subnode)) {
        shifts.put(i, nSkippedCols);
        if (iterator.hasNext()) {
          subnode = iterator.next();
        } else {
          break;
        }
      } else {
        nSkippedCols++;
      }
    }

    return shifts;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return nodes.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<T> iterator() {
    return nodes.iterator();
  }

  private static <T> List<T> checkSorted(List<T> a, Comparator<T> comparator) {
    if (a.isEmpty())
      return a;

    T previous = a.get(0);
    for (int i = 1; i < a.size(); i++) {
      T current = a.get(i);
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
    return nodes.get(idx);
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
