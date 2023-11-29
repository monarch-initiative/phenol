package org.monarchinitiative.phenol.annotations.formats;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Objects;

public class GeneIdentifier implements Identified {
  /** A CURIE for the gene, such as NCBIGene:3949 */
  private final TermId id;
  /** The gene symbol for the gene, e.g., LDLR */
  private final String symbol;

  public static GeneIdentifier of(TermId termId, String geneSymbol) {
    return new GeneIdentifier(termId, geneSymbol);
  }

  private GeneIdentifier(TermId tid, String geneSymbol) {
    this.id = Objects.requireNonNull(tid, "Term ID must not be null");
    this.symbol = Objects.requireNonNull(geneSymbol, "Gene symbol must not be null");
  }

  /** A CURIE for the gene, such as NCBIGene:3949 */
  @Override
  @JsonGetter
  public TermId id() {
    return id;
  }

  /**
   * @return the symbol for the gene, e.g., LDLR
   */
  @JsonGetter
  public String symbol() {
    return symbol;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GeneIdentifier geneIdentifier = (GeneIdentifier) o;
    return Objects.equals(id, geneIdentifier.id) && Objects.equals(symbol, geneIdentifier.symbol);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, symbol);
  }

  @Override
  public String toString() {
    return symbol +" [" + id.getValue() +"]";
  }

}
