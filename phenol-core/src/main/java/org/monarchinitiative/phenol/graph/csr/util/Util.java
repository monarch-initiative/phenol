package org.monarchinitiative.phenol.graph.csr.util;

import org.monarchinitiative.phenol.graph.NodeNotPresentInGraphException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
}
