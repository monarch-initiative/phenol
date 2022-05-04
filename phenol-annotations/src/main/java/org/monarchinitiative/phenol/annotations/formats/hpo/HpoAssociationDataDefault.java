package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class HpoAssociationDataDefault implements HpoAssociationData {

  private final GeneIdentifiers geneIdentifiers;
  private final Map<TermId, Collection<GeneIdentifier>> diseaseToGenes;
  private final Map<TermId, Collection<TermId>> geneToDiseases;
  private final List<HpoGeneAnnotation> phenotypeToGene;
  private final DiseaseToGeneAssociations associations;

  HpoAssociationDataDefault(GeneIdentifiers geneIdentifiers,
                            Map<TermId, Collection<GeneIdentifier>> diseaseToGenes,
                            Map<TermId, Collection<TermId>> geneToDiseases,
                            List<HpoGeneAnnotation> phenotypeToGene,
                            DiseaseToGeneAssociations associations) {
    this.geneIdentifiers = Objects.requireNonNull(geneIdentifiers, "Gene identifiers must not be null");
    this.diseaseToGenes = Objects.requireNonNull(diseaseToGenes, "Disease to genes must not be null");
    this.geneToDiseases = Objects.requireNonNull(geneToDiseases, "Gene to diseases must not be null");
    this.phenotypeToGene = Objects.requireNonNull(phenotypeToGene, "Phenotype to gene must not be null");
    this.associations = Objects.requireNonNull(associations, "Associations must not be null");
  }

  @Override
  public GeneIdentifiers getGeneIdentifiers() {
    return geneIdentifiers;
  }

  @Override
  public Map<TermId, Collection<GeneIdentifier>> diseaseToGenes() {
    return diseaseToGenes;
  }

  @Override
  public Map<TermId, Collection<TermId>> geneToDiseases() {
    return geneToDiseases;
  }

  @Override
  public List<HpoGeneAnnotation> phenotypeToGene() {
    return phenotypeToGene;
  }

  @Override
  public DiseaseToGeneAssociations associations() {
    return associations;
  }

}
