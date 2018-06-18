package org.monarchinitiative.phenol.formats;

import org.monarchinitiative.phenol.ontology.data.TermId;

public class Gene {
  /** A CURIE for the Gene, such as NCBIGene:3949 */
  private final TermId id;
  /** The gene symbol for the Gene, e.g., LDLR */
  private final String symbol;


  public Gene(TermId tid, String geneSymbol) {
    this.id=tid;
    this.symbol=geneSymbol;
  }

  public TermId getId() {
    return id;
  }

  public String getSymbol() {
    return symbol;
  }

  @Override
  public String toString() {
    return symbol +" [" + id.getIdWithPrefix() +"]";
  }


    /** Genes are equal if they have the same id and symbol */
    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (!(o instanceof Gene)) {
        return false;
      }
      Gene c = (Gene) o;
      return (c.id.equals(id) && c.symbol.equals(symbol));
    }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + id.hashCode();
    result = 31 * result + symbol.hashCode();
    return result;
  }


}
