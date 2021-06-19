package org.monarchinitiative.phenol.io.utils;


import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Node in a Trie.
 * From Chris Mungall's CurieUtil library
 *
 */
class TrieNode {
  private final char value;
  private final Map<Character, TrieNode> children;
  private boolean isLeaf;

  public TrieNode(char ch) {
    value = ch;
    children = new HashMap<>();
    isLeaf = false;
  }

  public Map<Character, TrieNode> getChildren() {
    return children;
  }

  public char getValue() {
    return value;
  }

  public void setIsLeaf(boolean val) {
    isLeaf = val;
  }

  public boolean isLeaf() {
    return isLeaf;
  }
}
