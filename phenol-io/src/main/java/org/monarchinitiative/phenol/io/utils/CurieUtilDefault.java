package org.monarchinitiative.phenol.io.utils;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default {@link CurieUtil} implementation backed by two maps.
 */
class CurieUtilDefault implements CurieUtil {

  private final Trie trie;
  private final Map<String, String> prefixToIri;
  private final Map<String, String> iriToPrefix;

  CurieUtilDefault(Map<String, String> prefixToIri) {
    this.prefixToIri = Objects.requireNonNull(prefixToIri);
    this.iriToPrefix = prefixToIri.entrySet().stream()
      .collect(Collectors.toUnmodifiableMap(Map.Entry::getValue, Map.Entry::getKey));
    this.trie = new Trie(prefixToIri.values());
  }

  @Override
  public boolean hasPrefix(String curiePrefix) {
    return prefixToIri.containsKey(curiePrefix);
  }

  @Override
  public Optional<TermId> getCurie(String iri) {
    String prefix = trie.search(iri);
    if (prefix.isBlank())
      return Optional.empty();

    String curiePrefix = iriToPrefix.get(prefix);
    return Optional.of(TermId.of(curiePrefix, iri.substring(prefix.length())));
  }

  @Override
  public Optional<String> getExpansion(String curiePrefix) {
    return Optional.ofNullable(prefixToIri.get(curiePrefix));
  }

}
