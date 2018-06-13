package org.monarchinitiative.phenol.formats;

import org.monarchinitiative.phenol.ontology.data.TermId;

public class Gene {

  private final TermId id;

  private final String symbol;


  public Gene(TermId tid, String geneSymbol) {
    this.id=tid;
    this.symbol=geneSymbol;
  }


  @Override
  public String toString() {
    return symbol +" [" + id.getIdWithPrefix() +"]";
  }

}
