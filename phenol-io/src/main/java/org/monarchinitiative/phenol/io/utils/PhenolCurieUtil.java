package org.monarchinitiative.phenol.io.utils;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.*;

public class PhenolCurieUtil {


  private final Set<String> acceptedPrefixes;
  private final ImmutableBiMap<String, String> curieMap;
  private final Trie trie;

  private PhenolCurieUtil() {
    curieMap = ImmutableBiMap.of();
    this.acceptedPrefixes = defaultAcceptedPrefixes() ;
    trie = new Trie();
  }

  private PhenolCurieUtil(Map<String,String> otherMaps) {
    this.acceptedPrefixes = defaultAcceptedPrefixes();
    this.curieMap = ImmutableBiMap.copyOf(otherMaps);
    trie = new Trie();
    for (String key : otherMaps.values()) {
      this.trie.insert(key);
    }
  }


  public Optional<String> getCurie(String iri) {
    if (iri.startsWith("http://purl.obolibrary.org/obo/")) {
      String termId = iri.substring(31).replace('_', ':');
      return Optional.of(termId);
    } else {
      String prefix = trie.getMatchingPrefix(iri);
      if (prefix.equals("")) {
        return Optional.empty();
      } else {
        String curiePrefix = curieMap.inverse().get(prefix);
        return Optional.of(curiePrefix + ":" + iri.substring(prefix.length()));
      }
    }
  }



  public static PhenolCurieUtil defaultCurieUtil() {
    return new PhenolCurieUtil();
  }

  public static Set<String> defaultAcceptedPrefixes() {
    Set<String> prefixes = new HashSet<>();
    prefixes.add("HP");
    prefixes.add("GO");
    prefixes.add("MONDO");
    prefixes.add("MP");
    prefixes.add("ECTO");
    prefixes.add("NCIT");
    return ImmutableSet.copyOf(prefixes);
  }

  public static PhenolCurieUtil withDefaultsAnd(Map<String, String> map) {
    return new PhenolCurieUtil(ImmutableMap.copyOf(map));
  }


  public boolean containsKey(String prefix) {
    return acceptedPrefixes.contains(prefix) || curieMap.containsKey(prefix);
  }
}
