package org.monarchinitiative.phenol.io.utils;

import java.util.HashMap;
import java.util.Map;

class TrieNode {

  private final Map<Character, TrieNode> children;
  private boolean isLeaf;

  TrieNode() {
    this.children = new HashMap<>();
  }

  public Map<Character, TrieNode> children() {
    return children;
  }

  public void setLeaf(boolean leaf) {
    isLeaf = leaf;
  }

  public boolean isLeaf() {
    return isLeaf;
  }
}
