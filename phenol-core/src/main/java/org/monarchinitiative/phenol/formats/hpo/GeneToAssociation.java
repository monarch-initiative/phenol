package org.monarchinitiative.phenol.formats.hpo;

import org.monarchinitiative.phenol.formats.Gene;

/**
 * This class records the way in which a gene is associated with a disease according to {@link AssociationType},
 * i.e., one of MENDELIAN, DIGENIC, POLYGENIC, UNKNOWN.
 */
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
