package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// TODO - evaluate the utility of this container and investigate if it is possible to split the container to smaller parts.
// TODO - should this be a one-stop-shop for all association data? If yes, then we should add HpoDiseases.
public interface HpoAssociationData {

  static HpoAssociationData of(GeneIdentifiers geneIdentifiers,
                               HpoGeneAnnotations hpoGeneAnnotations,
                               DiseaseToGeneAssociations associations) {
    return new HpoAssociationDataDefault(geneIdentifiers, hpoGeneAnnotations, associations);
  }

  /**
   * Get a builder for creating {@link HpoAssociationData}.
   *
   * @param hpo Human phenotype ontology.
   * @return a builder for building {@link HpoAssociationData}.
   * @see HpoAssociationDataBuilder
   */
  static HpoAssociationDataBuilder builder(MinimalOntology hpo) {
    return new HpoAssociationDataBuilder(hpo);
  }

  GeneIdentifiers getGeneIdentifiers();

  HpoGeneAnnotations hpoToGeneAnnotations();

  DiseaseToGeneAssociations associations();

  /* *******************************************************************************************************************
   * Derived methods.
   */

  default Map<TermId, String> geneIdToSymbol() {
    return geneIdentifiers().stream()
      .collect(Collectors.toUnmodifiableMap(Identified::id, GeneIdentifier::symbol));
  };

  default Map<TermId, DiseaseToGeneAssociation> associationsByDiseaseId() {
    return associations().diseaseToGeneAssociations()
      .collect(Collectors.toUnmodifiableMap(DiseaseToGeneAssociation::diseaseId, Function.identity()));
  }

  /* *******************************************************************************************************************
   * Deprecated methods.
   */

  /**
   * @deprecated to be removed in v2.0.0, use the other constructor instead.
   */
  @Deprecated(forRemoval = true)
  static HpoAssociationData of(List<GeneIdentifier> geneIdentifiers,
                               Map<TermId, Collection<GeneIdentifier>> diseaseToGenes,
                               Map<TermId, Collection<TermId>> geneToDiseases,
                               List<HpoGeneAnnotation> hpoGeneAnnotations,
                               DiseaseToGeneAssociations associations) {
    return of(GeneIdentifiers.of(geneIdentifiers), hpoGeneAnnotations, associations);
  }

  /**
   * @deprecated to be removed in v2.0.0, use the other constructor instead.
   */
  @Deprecated(forRemoval = true)
  static HpoAssociationData of(GeneIdentifiers geneIdentifiers,
                               List<HpoGeneAnnotation> hpoGeneAnnotations,
                               DiseaseToGeneAssociations associations) {
    return of(geneIdentifiers, HpoGeneAnnotations.of(hpoGeneAnnotations), associations);
  }

  /**
   * @deprecated use {@link #getGeneIdentifiers()} instead (to be removed in v2.0.0).
   */
  @Deprecated(forRemoval = true)
  default List<GeneIdentifier> geneIdentifiers() {
    return getGeneIdentifiers().geneIdentifiers();
  }

  /**
   * @deprecated to be removed in v2.0.0, use {@link #associations()} instead.
   */
  @Deprecated(forRemoval = true)
  default Map<TermId, Collection<GeneIdentifier>> diseaseToGenes() {
    return associations().diseaseIdToGeneIds();
  }

  /**
   * @deprecated to be removed in v2.0.0, use {@link #associations()} instead.
   */
  @Deprecated(forRemoval = true)
  default Map<TermId, Collection<TermId>> geneToDiseases() {
    return associations().geneIdToDiseaseIds();
  }

  /**
   * @deprecated to be removed in v2.0.0, use {@link #hpoToGeneAnnotations()} instead.
   */
  @Deprecated(forRemoval = true)
  default List<HpoGeneAnnotation> phenotypeToGene() {
    return hpoToGeneAnnotations().stream().collect(Collectors.toList());
  }

}
