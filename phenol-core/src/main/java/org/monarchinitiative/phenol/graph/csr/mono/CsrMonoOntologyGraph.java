package org.monarchinitiative.phenol.graph.csr.mono;

import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.graph.csr.util.Util;
import org.monarchinitiative.phenol.utils.IterableIteratorWrapper;

import java.util.*;

/**
 * An {@link OntologyGraph} that only supports one edge type and supports efficient retrieval of parent or child nodes.
 *
 * @param <T> type of the term/graph node.
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class CsrMonoOntologyGraph<T> implements OntologyGraph<T> {

  private final T root;
  private final T[] nodes;
  private final Comparator<T> comparator;
  private final StaticCsrArray<T> parents;
  private final StaticCsrArray<T> children;

  CsrMonoOntologyGraph(T root,
                       T[] nodes,
                       Comparator<T> comparator,
                       StaticCsrArray<T> parents,
                       StaticCsrArray<T> children) {
    this.root = Objects.requireNonNull(root);
    this.nodes = Objects.requireNonNull(nodes);
    this.comparator = Objects.requireNonNull(comparator);
    this.parents = Objects.requireNonNull(parents);
    this.children = Objects.requireNonNull(children);
  }

  StaticCsrArray<T> getParentArray() {
    return parents;
  }

  StaticCsrArray<T> getChildArray() {
    return children;
  }

  T[] getNodes() {
    return nodes;
  }

  @Override
  public T root() {
    return root;
  }

  @Override
  public Iterable<T> getChildren(T source, boolean includeSource) {
    return getImmediateNeighbors(children, source, includeSource);
  }

  @Override
  public Iterable<T> getDescendants(T source, boolean includeSource) {
    // Check if `source` is in the graph.
    int intentionallyUnused = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);

    return new IterableIteratorWrapper<>(() -> new TraversingIterator<>(source, src -> getChildren(src, includeSource)));
  }

  @Override
  public Iterable<T> getParents(T source, boolean includeSource) {
    return getImmediateNeighbors(parents, source, includeSource);
  }

  @Override
  public Iterable<T> getAncestors(T source, boolean includeSource) {
    // Check if `source` is in the graph.
    int intentionallyUnused = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);

    return new IterableIteratorWrapper<>(() -> new TraversingIterator<>(source, src -> getParents(src, includeSource)));
  }

  private Iterable<T> getImmediateNeighbors(StaticCsrArray<T> array,
                                            T source,
                                            boolean includeSource) {
    int index = Util.getIndexOfUsingBinarySearch(source, nodes, comparator);
    Set<T> nodes = array.getOutgoingNodes(index);

    return includeSource
      ? new SetIncludingSource<>(source, nodes)
      : nodes;
  }

  @Override
  public int size() {
    return nodes.length;
  }

  @Override
  public Iterator<T> iterator() {
    // TODO - can we do better here?
    return Arrays.stream(nodes).iterator();
  }

}
