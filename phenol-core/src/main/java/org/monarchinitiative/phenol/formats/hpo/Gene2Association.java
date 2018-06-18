package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.formats.Gene;

public class Gene2Association {
  private final Gene gene;
  private final AssociationType association;

  public Gene2Association(Gene g, AssociationType at) {
    gene=g;
    association=at;
  }

  public Gene getGene() {
    return gene;
  }

  public AssociationType getAssociation() {
    return association;
  }

  @Override
  public String toString() {
    return gene.getSymbol() +" [" + association.toString() + "]";
  }
}
