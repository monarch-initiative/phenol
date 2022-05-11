package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;

import java.util.Objects;

class HpoAssociationDataDefault implements HpoAssociationData {

  private final GeneIdentifiers geneIdentifiers;
  private final HpoGeneAnnotations hpoToGeneAnnotations;
  private final DiseaseToGeneAssociations associations;

  HpoAssociationDataDefault(GeneIdentifiers geneIdentifiers,
                            HpoGeneAnnotations hpoGeneAnnotations,
                            DiseaseToGeneAssociations associations) {
    this.geneIdentifiers = Objects.requireNonNull(geneIdentifiers, "Gene identifiers must not be null");
    this.hpoToGeneAnnotations = Objects.requireNonNull(hpoGeneAnnotations, "Phenotype to gene must not be null");
    this.associations = Objects.requireNonNull(associations, "Associations must not be null");
  }

  @Override
  public GeneIdentifiers getGeneIdentifiers() {
    return geneIdentifiers;
  }

  @Override
  public HpoGeneAnnotations hpoToGeneAnnotations() {
    return hpoToGeneAnnotations;
  }

  @Override
  public DiseaseToGeneAssociations associations() {
    return associations;
  }

}
