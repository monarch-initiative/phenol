package org.monarchinitiative.phenol.annotations.formats;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeneIdentifiers implements Iterable<GeneIdentifier> {
  // an interface candidate

  private final List<GeneIdentifier> geneIdentifiers;

  public static GeneIdentifiers of(List<GeneIdentifier> geneIdentifiers) {
    return new GeneIdentifiers(geneIdentifiers);
  }

  private GeneIdentifiers(List<GeneIdentifier> geneIdentifiers) {
    this.geneIdentifiers = Objects.requireNonNull(geneIdentifiers, "Gene identifiers must not be null");
  }

  public List<GeneIdentifier> geneIdentifiers() {
    return geneIdentifiers;
  }

  public Map<TermId, String> geneIdToSymbol() {
    return geneIdentifiers.stream()
      .collect(Collectors.toUnmodifiableMap(GeneIdentifier::id, GeneIdentifier::symbol));
  }

  public Map<String, GeneIdentifier> symbolToGeneIdentifier() {
    return geneIdentifiers.stream()
      .collect(Collectors.toUnmodifiableMap(GeneIdentifier::symbol, Function.identity()));
  }

  @Override
  public Iterator<GeneIdentifier> iterator() {
    return geneIdentifiers.iterator();
  }

}
