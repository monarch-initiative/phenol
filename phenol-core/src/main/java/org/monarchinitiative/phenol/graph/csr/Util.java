package org.monarchinitiative.phenol.graph.csr;

import org.monarchinitiative.phenol.graph.NodeNotPresentInGraphException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class Util {
  private Util(){}

  static <T> int getIndexOfUsingBinarySearch(T node, T[] nodes, Comparator<T> comparator) {
    int idx = Arrays.binarySearch(nodes, node, comparator);
    if (idx >= 0)
      return idx;
    else
      throw new NodeNotPresentInGraphException(String.format("Item not found in the graph: %s", node));
  }

  static int[] toIntArray(List<Integer> indices) {
    int[] a = new int[indices.size()];
    for (int i = 0; i < indices.size(); i++)
      a[i] = indices.get(i);
    return a;
  }
}
