package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.formats.Gene;

public class GeneToAssociation {
  private final Gene gene;
  private final AssociationType association;

  public GeneToAssociation(Gene g, AssociationType at) {
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
