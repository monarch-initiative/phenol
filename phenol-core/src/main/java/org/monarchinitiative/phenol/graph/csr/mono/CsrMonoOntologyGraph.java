package org.monarchinitiative.phenol.graph.csr.mono;

import org.monarchinitiative.phenol.graph.NodeNotPresentInGraphException;
import org.monarchinitiative.phenol.graph.OntologyGraph;
import org.monarchinitiative.phenol.utils.IterableIteratorWrapper;

import java.util.*;

/**
 * An {@link OntologyGraph} that only supports one edge type and supports efficient retrieval of parent or child nodes.
 * <p>
 * It maintains a pair of CSR-like structures, {@link StaticCsrArray}, one for getting the parents and the other
 * for children of a term. Both arrays are sorted to contain information for a node {@link T} under the same integer
 * index. We get an index from a mapping.
 *
 * @param <T> type of the term/graph node.
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class CsrMonoOntologyGraph<T> implements OntologyGraph<T> {

  private final T root;
  private final Map<T, Integer> nodesToIdx;
  private final StaticCsrArray<T> parents;
  private final StaticCsrArray<T> children;

  CsrMonoOntologyGraph(T root,
                       Map<T, Integer> nodesToIdx,
                       StaticCsrArray<T> parents,
                       StaticCsrArray<T> children) {
    this.root = Objects.requireNonNull(root);
    this.nodesToIdx = Objects.requireNonNull(nodesToIdx);
    this.parents = Objects.requireNonNull(parents);
    this.children = Objects.requireNonNull(children);
  }

  StaticCsrArray<T> getParentArray() {
    return parents;
  }

  StaticCsrArray<T> getChildArray() {
    return children;
  }

  private int getNodeIdx(T node) {
    Integer idx = nodesToIdx.get(node);
    if (idx == null)
      throw new NodeNotPresentInGraphException(String.format("Item not found in the graph: %s", node));
    return idx;
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
    int intentionallyUnused = getNodeIdx(source);

    return new IterableIteratorWrapper<>(() -> new TraversingIterator<>(source, src -> getChildren(src, includeSource)));
  }

  @Override
  public Iterable<T> getParents(T source, boolean includeSource) {
    return getImmediateNeighbors(parents, source, includeSource);
  }

  @Override
  public Iterable<T> getAncestors(T source, boolean includeSource) {
    // Check if `source` is in the graph.
    int intentionallyUnused = getNodeIdx(source);

    return new IterableIteratorWrapper<>(() -> new TraversingIterator<>(source, src -> getParents(src, includeSource)));
  }

  private Iterable<T> getImmediateNeighbors(StaticCsrArray<T> array,
                                            T source,
                                            boolean includeSource) {
    int index = getNodeIdx(source);

    Set<T> nodes = array.getOutgoingNodes(index);

    return includeSource
      ? new SetIncludingSource<>(source, nodes)
      : nodes;
  }

  @Override
  public int size() {
    return nodesToIdx.size();
  }

  @Override
  public Iterator<T> iterator() {
    return nodesToIdx.keySet().iterator();
  }

}
