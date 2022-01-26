package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DiseaseToGeneAssociations extends Iterable<DiseaseToGeneAssociation> {

  static DiseaseToGeneAssociations of(List<DiseaseToGeneAssociation> diseaseToGeneAssociations) {
    return DiseaseToGeneAssociationsDefault.of(diseaseToGeneAssociations);
  }

  Stream<DiseaseToGeneAssociation> diseaseToGeneAssociations();


  /* *******************************************************************************************************************
   * Derived methods that use either the stream or iterator provided by DiseaseToGeneAssociations.
   */

  default Map<TermId, Collection<GeneIdentifier>> diseaseIdToGeneIds() {
    Map<TermId, Collection<GeneIdentifier>> diseaseIdToGeneIdsBuilder = new HashMap<>();

    for (DiseaseToGeneAssociation diseaseToGeneAssociation : this) {
      List<GeneIdentifier> geneIds = diseaseToGeneAssociation.associations().stream()
        .map(GeneToAssociation::geneIdentifier)
        .collect(Collectors.toUnmodifiableList());

      diseaseIdToGeneIdsBuilder.put(diseaseToGeneAssociation.diseaseId(), geneIds);
    }

    return Map.copyOf(diseaseIdToGeneIdsBuilder);
  }

  default Map<TermId, Collection<TermId>> geneIdToDiseaseIds() {
    Map<TermId, Collection<TermId>> geneIdToDiseaseIdsBuilder = new HashMap<>();

    // fill the builder
    for (DiseaseToGeneAssociation diseaseToGeneAssociation : this) {
      for (GeneToAssociation geneToAssociation : diseaseToGeneAssociation.associations()) {
        geneIdToDiseaseIdsBuilder.computeIfAbsent(geneToAssociation.geneIdentifier().id(), k -> new HashSet<>())
          .add(diseaseToGeneAssociation.diseaseId());
      }
    }

    // wrap the diseaseIds into unmodifiable list
    return geneIdToDiseaseIdsBuilder.entrySet().stream()
      .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> List.copyOf(e.getValue())));
  }

  default Map<TermId, Collection<GeneToAssociation>> diseaseIdToGeneAssociations() {
    return diseaseToGeneAssociations()
      .collect(Collectors.toUnmodifiableMap(DiseaseToGeneAssociation::diseaseId, DiseaseToGeneAssociation::associations));
  }
}
