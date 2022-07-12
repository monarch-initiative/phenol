package org.monarchinitiative.phenol.io.utils;

import java.util.Collection;
import java.util.Map;

class Trie {

  private final TrieNode root;

  Trie(Collection<String> values) {
    this.root = new TrieNode();
    values.forEach(this::insert);
  }

  void insert(String word) {
    TrieNode node = root;

    for (int level = 0, length = word.length(); level < length; level++) {
      Map<Character, TrieNode> children = node.children();
      char c = word.charAt(level);

      TrieNode child = children.get(c);
      if (child != null) {
        // If there is already a child for current character of given word.
        node = child;
      } else {
        // Else create a child.
        TrieNode novel = new TrieNode();
        children.put(c, novel);
        node = novel;
      }
    }

    node.setLeaf(true);
  }

  String search(String value) {
    StringBuilder result = new StringBuilder();

    TrieNode node = root;

    int prevMatch = 0;
    for (int level = 0, length = value.length(); level < length; level++) {
      Map<Character, TrieNode> children = node.children();
      char c = value.charAt(level);

      TrieNode child = children.get(c);
      if (child != null) {
        result.append(c);
        node = child;

        if (node.isLeaf())
          prevMatch = level + 1;
      } else {
        break;
      }
    }

    return node.isLeaf()
      ? result.toString()
      : result.substring(0, prevMatch);
  }

}
