package org.monarchinitiative.phenol.annotations.formats;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeneIdentifiers implements Iterable<GeneIdentifier> {
  // an interface candidate

  private final List<GeneIdentifier> geneIdentifiers;
  private volatile Map<TermId, String> geneIdToSymbol = null;
  private volatile Map<String, GeneIdentifier> symbolToGeneIdentifier = null;

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
    if (geneIdToSymbol == null) {
      synchronized (this) {
        if (geneIdToSymbol == null) {
          geneIdToSymbol = geneIdentifiers.stream()
            .collect(Collectors.toUnmodifiableMap(GeneIdentifier::id, GeneIdentifier::symbol));
        }
      }
    }
    return geneIdToSymbol;
  }

  public Map<String, GeneIdentifier> symbolToGeneIdentifier() {
    if (symbolToGeneIdentifier == null) {
      synchronized (this) {
        if (symbolToGeneIdentifier == null) {
          symbolToGeneIdentifier = geneIdentifiers.stream()
            .collect(Collectors.toUnmodifiableMap(GeneIdentifier::symbol, Function.identity()));
        }
      }
    }
    return symbolToGeneIdentifier;
  }

  @Override
  public Iterator<GeneIdentifier> iterator() {
    return geneIdentifiers.iterator();
  }

  public int size() {
    return geneIdentifiers.size();
  }
}
