package org.monarchinitiative.phenol.graph.csr;

import org.monarchinitiative.phenol.graph.NodeNotPresentInGraphException;
import org.monarchinitiative.phenol.graph.OntologyGraph;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The nodes array must be sorted
 *
 * @param <T>
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class CsrOntologyGraph<T> implements OntologyGraph<T> {

  private final T root;
  private final T[] nodes;
  private final Comparator<T> comparator;
  private final ImmutableCsrMatrix<Byte> adjacencyMatrix;

  public CsrOntologyGraph(T root,
                          T[] nodes,
                          Comparator<T> comparator,
                          ImmutableCsrMatrix<Byte> adjacencyMatrix) {
    this.root = Objects.requireNonNull(root);
    this.nodes = Objects.requireNonNull(nodes);
    this.comparator = Objects.requireNonNull(comparator);
    this.adjacencyMatrix = Objects.requireNonNull(adjacencyMatrix);
  }

  @Override
  public T root() {
    return root;
  }

  @Override
  public Iterator<T> getChildren(T source, boolean includeSource) {
    Iterator<Integer> base = getNodeIndicesWithRelationship(source, RelationshipPredicates.IS_PARENT, includeSource);
    return new InfallibleMappingIterator<>(base, this::getNodeForIndex);
  }

  @Override
  public Iterator<T> getDescendants(T source, boolean includeSource) {
    Iterator<Integer> base = traverseGraph(source, RelationshipPredicates.IS_PARENT, includeSource);
    return new InfallibleMappingIterator<>(base, this::getNodeForIndex);
  }

  @Override
  public Iterator<T> getParents(T source, boolean includeSource) {
    Iterator<Integer> base = getNodeIndicesWithRelationship(source, RelationshipPredicates.IS_CHILD, includeSource);
    return new InfallibleMappingIterator<>(base, this::getNodeForIndex);
  }

  @Override
  public Iterator<T> getAncestors(T source, boolean includeSource) {
    Iterator<Integer> base = traverseGraph(source, RelationshipPredicates.IS_CHILD, includeSource);
    return new InfallibleMappingIterator<>(base, this::getNodeForIndex);
  }

  @Override
  public Iterator<T> iterator() {
    return Stream.of(nodes).iterator();
  }

  private Iterator<Integer> getNodeIndicesWithRelationship(T source,
                                                           Predicate<Byte> relationship,
                                                           boolean includeSource) {
    int rowIdx = getRowIndexForNode(source);
    return getColsWithRelationship(rowIdx, relationship, includeSource);
  }

  private int getRowIndexForNode(T node) {
    int idx = Arrays.binarySearch(nodes, node, comparator);
    if (idx >= 0)
      return idx;
    else
      throw new NodeNotPresentInGraphException(String.format("Term ID not found in the graph: %s", node));
  }

  private Iterator<Integer> getColsWithRelationship(int source,
                                                    Predicate<Byte> relationship,
                                                    boolean includeSource) {
    Iterator<Integer> base = adjacencyMatrix.colIndicesOfVal(source, relationship);
    return new NodeIndexIterator(source, includeSource, base);
  }

  private T getNodeForIndex(int idx) {
    return nodes[idx];
  }

  private Iterator<Integer> traverseGraph(T source,
                                          Predicate<Byte> relationship,
                                          boolean includeSource) {
    return new TraversingIterator(source, relationship, includeSource);
  }

  /**
   * An iterator for traversing the ontology graph starting from the {@code source} up to the root or the leaf.
   */
  private class TraversingIterator implements Iterator<Integer> {

    private final Set<Integer> seen = new HashSet<>();
    private final Deque<Integer> buffer = new ArrayDeque<>();
    private final Predicate<Byte> relationship;

    private TraversingIterator(T source,
                              Predicate<Byte> relationship,
                              boolean includeSource) {
      this.relationship = relationship;
      Iterator<Integer> base = getNodeIndicesWithRelationship(source, relationship, includeSource);
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
