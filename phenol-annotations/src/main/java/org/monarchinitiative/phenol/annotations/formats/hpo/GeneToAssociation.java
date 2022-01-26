package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;

import java.util.Objects;

/**
 * This class records the way in which a gene is associated with a disease according to {@link AssociationType},
 * i.e., one of MENDELIAN, DIGENIC, POLYGENIC, UNKNOWN.
 */
public class GeneToAssociation {
  private final GeneIdentifier geneIdentifier;
  private final AssociationType associationType;

  public static GeneToAssociation of(GeneIdentifier geneIdentifier, AssociationType at) {
    return new GeneToAssociation(geneIdentifier, at);
  }

  private GeneToAssociation(GeneIdentifier geneIdentifier, AssociationType associationType) {
    this.geneIdentifier = Objects.requireNonNull(geneIdentifier, "Gene identifier must not be null");
    this.associationType = Objects.requireNonNull(associationType, "Association type must not be null");
  }

  public GeneIdentifier geneIdentifier() {
    return geneIdentifier;
  }

  public AssociationType associationType() {
    return associationType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GeneToAssociation that = (GeneToAssociation) o;
    return Objects.equals(geneIdentifier, that.geneIdentifier) && associationType == that.associationType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(geneIdentifier, associationType);
  }

  @Override
  public String toString() {
    return geneIdentifier.symbol() + " [" + associationType + "]";
  }
}
