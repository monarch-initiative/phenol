package org.monarchinitiative.phenol.graph.csr.util;

import org.monarchinitiative.phenol.graph.NodeNotPresentInGraphException;
import org.monarchinitiative.phenol.graph.OntologyGraphEdge;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

public class Util {
  private Util(){}

  public static <T> int getIndexOfUsingBinarySearch(T node, T[] nodes, Comparator<T> comparator) {
    int idx = Arrays.binarySearch(nodes, node, comparator);
    if (idx >= 0)
      return idx;
    else
      throw new NodeNotPresentInGraphException(String.format("Item not found in the graph: %s", node));
  }

  public static int[] toIntArray(List<Integer> indices) {
    int[] a = new int[indices.size()];
    for (int i = 0; i < indices.size(); i++)
      a[i] = indices.get(i);
    return a;
  }

  public static int[] checkSequenceOfNonNegativeInts(int[] a) {
    List<String> offenders = new ArrayList<>();
    for (int i = 0; i < a.length; i++) {
      if (a[i] < 0) {
        offenders.add(String.valueOf(i));
      }
    }
    if (!offenders.isEmpty()) {
      String msg = "Expected array of non-negative integers but the following indices were negative: " +
        String.join(", ", offenders);

      throw new IllegalArgumentException(msg);
    }

    return a;
  }

  /**
   * We build the CSR matrix row by row, and we need to know about all nodes that are adjacent with
   * (have a relationship with) the node represented by the row under the construction.
   * Here we prepare a mapping from the row index to a list of all adjacent edges.
   */
  public static Map<Integer, List<OntologyGraphEdge<TermId>>> findAdjacentEdges(TermId[] nodes,
                                                                                Collection<? extends OntologyGraphEdge<TermId>> edges) {
    Map<Integer, List<OntologyGraphEdge<TermId>>> data = new HashMap<>();

    TermId lastSub = null;
    int lastSubIdx = -1;

    for (OntologyGraphEdge<TermId> edge : edges) {
      int subIdx;
      TermId sub = edge.subject();
      if (sub == lastSub) {
        subIdx = lastSubIdx;
      } else {
        lastSub = sub;
        subIdx = getIndexOfUsingBinarySearch(sub, nodes, TermId::compareTo);
        lastSubIdx = subIdx;
      }

      TermId obj = edge.object();
      int objIdx = getIndexOfUsingBinarySearch(obj, nodes, TermId::compareTo);

      data.computeIfAbsent(subIdx, x -> new ArrayList<>()).add(edge);
      data.computeIfAbsent(objIdx, x -> new ArrayList<>()).add(edge);
    }

    return data;
  }
}
