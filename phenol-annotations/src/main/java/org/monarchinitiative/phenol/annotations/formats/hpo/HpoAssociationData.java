package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO - evaluate the utility of this container and investigate if it is possible to split the container to smaller parts.
// TODO - should this be a one-stop-shop for all association data? If yes, then we should add HpoDiseases.
public interface HpoAssociationData {

  /**
   * @deprecated to be removed in v2.0.0, use the other constructor instead.
   */
  @Deprecated(forRemoval = true)
  static HpoAssociationData of(List<GeneIdentifier> geneIdentifiers,
                               Map<TermId, Collection<GeneIdentifier>> diseaseToGenes,
                               Map<TermId, Collection<TermId>> geneToDiseases,
                               List<HpoGeneAnnotation> phenotypeToGene,
                               DiseaseToGeneAssociations associations) {
    return new HpoAssociationDataDefault(GeneIdentifiers.of(geneIdentifiers), diseaseToGenes, geneToDiseases, phenotypeToGene, associations);
  }

  static HpoAssociationData of(GeneIdentifiers geneIdentifiers,
                               Map<TermId, Collection<GeneIdentifier>> diseaseToGenes,
                               Map<TermId, Collection<TermId>> geneToDiseases,
                               List<HpoGeneAnnotation> phenotypeToGene,
                               DiseaseToGeneAssociations associations) {
    return new HpoAssociationDataDefault(geneIdentifiers, diseaseToGenes, geneToDiseases, phenotypeToGene, associations);
  }

  GeneIdentifiers getGeneIdentifiers();

  /**
   * @deprecated use {@link #getGeneIdentifiers()} instead (to be removed in v2.0.0).
   */
  @Deprecated(forRemoval = true)
  default List<GeneIdentifier> geneIdentifiers() {
    return getGeneIdentifiers().geneIdentifiers();
  }

  Map<TermId, Collection<GeneIdentifier>> diseaseToGenes();

  Map<TermId, Collection<TermId>> geneToDiseases();

  List<HpoGeneAnnotation> phenotypeToGene();

  DiseaseToGeneAssociations associations();


  /* *******************************************************************************************************************
   * Derived methods.
   */

  default Map<TermId, String> geneIdToSymbol() {
    return geneIdentifiers().stream()
      .collect(Collectors.toUnmodifiableMap(GeneIdentifier::id, GeneIdentifier::symbol));
  };

  default Map<TermId, DiseaseToGeneAssociation> associationsByDiseaseId() {
    return associations().diseaseToGeneAssociations()
      .collect(Collectors.toUnmodifiableMap(DiseaseToGeneAssociation::diseaseId, Function.identity()));
  }
}
