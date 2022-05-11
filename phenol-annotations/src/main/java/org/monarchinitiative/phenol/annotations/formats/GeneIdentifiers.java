package org.monarchinitiative.phenol.annotations.formats;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GeneIdentifiers implements Iterable<GeneIdentifier> {
  // an interface candidate

  private final List<GeneIdentifier> geneIdentifiers;
  private volatile Map<TermId, String> geneIdToSymbol = null;
  private volatile Map<TermId, GeneIdentifier> geneIdToGeneIdentifier = null;
  private volatile Map<String, GeneIdentifier> symbolToGeneIdentifier = null;

  public static GeneIdentifiers of(List<GeneIdentifier> geneIdentifiers) {
    return new GeneIdentifiers(geneIdentifiers);
  }

  private GeneIdentifiers(List<GeneIdentifier> geneIdentifiers) {
    this.geneIdentifiers = Objects.requireNonNull(geneIdentifiers, "Gene identifiers must not be null");
  }

  /**
   * @deprecated to be removed in v2.0.0, use {@link #stream()} or {@link #iterator()}.
   */
  @Deprecated(forRemoval = true)
  public List<GeneIdentifier> geneIdentifiers() {
    return geneIdentifiers;
  }

  /**
   * Get {@link GeneIdentifier} associated with the given gene ID.
   *
   * @param geneId A CURIE for the gene ID, such as <em>NCBIGene:3949</em>.
   * @return optional with the gene identifier or empty optional if the CURIE is not present.
   * @throws NullPointerException if the {@code symbol} is <code>null</code>.
   */
  public Optional<GeneIdentifier> geneIdById(TermId geneId) {
    if (geneIdToGeneIdentifier == null) {
      synchronized (this) {
        if (geneIdToGeneIdentifier == null) {
          geneIdToGeneIdentifier = geneIdentifiers.stream()
            .collect(Collectors.toUnmodifiableMap(GeneIdentifier::id, Function.identity()));
        }
      }
    }
    return Optional.ofNullable(geneIdToGeneIdentifier.get(geneId));
  }

  /**
   * @deprecated to be removed in v2.0.0, use {@link #geneIdById(TermId)}.
   */
  @Deprecated(forRemoval = true)
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


  /**
   * Get {@link GeneIdentifier} associated with the given HGVS gene symbol.
   *
   * @param symbol gene symbol (e.g. <em>SURF1</em>).
   * @return optional with the gene identifier or empty optional if the symbol is not present.
   * @throws NullPointerException if the {@code symbol} is <code>null</code>.
   */
  public Optional<GeneIdentifier> geneIdBySymbol(String symbol) {
    if (symbolToGeneIdentifier == null) {
      synchronized (this) {
        if (symbolToGeneIdentifier == null) {
          symbolToGeneIdentifier = geneIdentifiers.stream()
            .collect(Collectors.toUnmodifiableMap(GeneIdentifier::symbol, Function.identity()));
        }
      }
    }
    return Optional.ofNullable(symbolToGeneIdentifier.get(symbol));
  }

  /**
   * @deprecated to be removed in v2.0.0, use {@link #geneIdBySymbol(String)}.
   */
  @Deprecated(forRemoval = true)
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

  public Stream<GeneIdentifier> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  public int size() {
    return geneIdentifiers.size();
  }
}
