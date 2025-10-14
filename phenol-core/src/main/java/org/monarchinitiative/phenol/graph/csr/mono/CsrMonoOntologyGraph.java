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
  public Set<T> getChildren(T source) {
    int index = getNodeIdx(source);
    return children.getOutgoingNodes(index);
  }

  @Override
  public Iterable<T> getChildren(T source, boolean includeSource) {
    // TODO - remove when the parent is removed
    return getImmediateNeighbors(children, source, includeSource);
  }

  @Override
  public Iterable<T> getDescendants(T source) {
    // Check if `source` is in the graph.
    int intentionallyUnused = getNodeIdx(source);

    return new IterableIteratorWrapper<>(() -> new TraversingIterator<>(source, this::getChildren));
  }

  @Override
  public Iterable<T> getDescendants(T source, boolean includeSource) {
    // TODO - remove when the parent is removed
    // Check if `source` is in the graph.
    int intentionallyUnused = getNodeIdx(source);

    return new IterableIteratorWrapper<>(() -> new TraversingIterator<>(source, src -> getChildren(src, includeSource)));
  }

  @Override
  public void extendWithDescendants(T source,
                                    boolean includeSource,
                                    Collection<T> collection) {
    // TODO: a candidate for better implementation
    OntologyGraph.super.extendWithDescendants(source, includeSource, collection);
  }

  @Override
  public Set<T> getParents(T source) {
    int index = getNodeIdx(source);
    return parents.getOutgoingNodes(index);
  }

  @Override
  public Iterable<T> getParents(T source, boolean includeSource) {
    // TODO - remove when the parent is removed
    return getImmediateNeighbors(parents, source, includeSource);
  }

  @Override
  public Iterable<T> getAncestors(T source) {
    // Check if `source` is in the graph.
    int intentionallyUnused = getNodeIdx(source);

    return new IterableIteratorWrapper<>(() -> new TraversingIterator<>(source, this::getParents));
  }

  @Override
  public Iterable<T> getAncestors(T source, boolean includeSource) {
    // TODO - remove when the parent is removed
    // Check if `source` is in the graph.
    int intentionallyUnused = getNodeIdx(source);

    return new IterableIteratorWrapper<>(() -> new TraversingIterator<>(source, src -> getParents(src, includeSource)));
  }

  @Override
  public void extendWithAncestors(T source,
                                  boolean includeSource,
                                  Collection<T> collection) {
    // TODO: a candidate for better implementation
    OntologyGraph.super.extendWithAncestors(source, includeSource, collection);
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
  public OntologyGraph<T> extractSubgraph(T subRoot) {
    if (subRoot.equals(root))
      return this; // No need to extract subgraph since the subgraph equals to the graph.

    // A subset of nodes for the subgraph.
    List<T> subNodes = new ArrayList<>();
    { // a scope to drop `seen`.
      Set<T> seen = new HashSet<>();
      fillNodeToIdxMap(subRoot, subNodes, seen);
    }

    Map<T, Integer> nodeToIdx = new HashMap<>();
    for (int i = 0; i < subNodes.size(); i++)
      nodeToIdx.put(subNodes.get(i), i);

    StaticCsrArray<T> subParents = prepareSubParents(subNodes, subRoot);
    StaticCsrArray<T> subChildren = prepareSubChildren(subNodes);

    return new CsrMonoOntologyGraph<>(subRoot, nodeToIdx, subParents, subChildren);
  }

  private void fillNodeToIdxMap(T node,
                                Collection<T> buffer,
                                Set<T> seen) {
    int nodeIdx = getNodeIdx(node);
    buffer.add(node);
    for (T child : children.getOutgoingNodes(nodeIdx))
      if (!seen.contains(child)) {
        fillNodeToIdxMap(child, buffer, seen);
        seen.add(child);
      }
  }

  private StaticCsrArray<T> prepareSubParents(List<T> nodes, T rootNode) {
    int[] indptrs = new int[nodes.size() + 1];
    List<T> items = new ArrayList<>();
    for (int i = 0; i < nodes.size(); i++) {
      T item = nodes.get(i);
      if (item.equals(rootNode))
        // We must filter out the parents of the root, otherwise the root wouldn't be the root, right?
        continue;

      int idx = getNodeIdx(item);
      items.addAll(parents.getOutgoingNodes(idx));
      indptrs[i + 1] = items.size();
    }

    return new StaticCsrArray<>(indptrs, items);
  }

  private StaticCsrArray<T> prepareSubChildren(List<T> nodes) {
    int[] indptrs = new int[nodes.size() + 1];
    List<T> items = new ArrayList<>();
    for (int i = 0; i < nodes.size(); i++) {
      T item = nodes.get(i);
      int idx = getNodeIdx(item);
      items.addAll(children.getOutgoingNodes(idx));
      indptrs[i + 1] = items.size();
    }

    return new StaticCsrArray<>(indptrs, items);
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
