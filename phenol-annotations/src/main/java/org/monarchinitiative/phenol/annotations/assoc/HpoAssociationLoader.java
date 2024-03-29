package org.monarchinitiative.phenol.annotations.assoc;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoader;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoaderOptions;

import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Top-level loader for creating {@link HpoAssociationData}.
 * <p>
 *   The {@link HpoAssociationData} is created from
 *   <ul>
 *     <li>Human Phenotype Ontology,</li>
 *     <li>OMIM and Orphanet's gene-to-disease association files,</li>
 *     <li>HPOA file, and</li>
 *     <li><em>Homo sapiens</em> gene info file.</li>
 *   </ul>
 */
public class HpoAssociationLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoAssociationLoader.class);

  private HpoAssociationLoader() {
  }


  /**
   * @deprecated to be removed in v3.0.0, use {@link HpoAssociationDataBuilder} instead.
   * @see HpoAssociationData#builder(Ontology)
   */
  @Deprecated(forRemoval = true)
  public static HpoAssociationData loadHpoAssociationData(Ontology hpo,
                                                          Path homoSapiensGeneInfo,
                                                          Path mim2geneMedgen,
                                                          Path orphaToGene,
                                                          Path phenotypeHpoa,
                                                          HpoDiseaseLoaderOptions options) throws IOException {
    HpoDiseaseLoader loader = HpoDiseaseLoader.of(hpo, options);
    HpoDiseases diseases = loader.load(phenotypeHpoa);

    return loadHpoAssociationData(hpo, homoSapiensGeneInfo, mim2geneMedgen, orphaToGene, diseases);
  }

  /**
   * @deprecated to be removed in v3.0.0, use {@link HpoAssociationDataBuilder} instead.
   */
  @Deprecated(forRemoval = true)
  public static HpoAssociationData loadHpoAssociationData(Ontology hpo,
                                                          Path homoSapiensGeneInfo,
                                                          Path mim2geneMedgen,
                                                          Path orphaToGene,
                                                          HpoDiseases diseases) throws IOException {
    GeneIdentifiers geneIdentifiers = HumanGeneInfoLoader.loadGeneIdentifiers(homoSapiensGeneInfo, GeneInfoGeneType.DEFAULT);

    DiseaseToGeneAssociations diseaseToGeneAssociations = DiseaseToGeneAssociationLoader.loadDiseaseToGeneAssociations(mim2geneMedgen, orphaToGene, geneIdentifiers);
    Map<TermId, Collection<GeneToAssociation>> diseaseToGeneMap = diseaseToGeneAssociations.diseaseIdToGeneAssociations();

    List<HpoGeneAnnotation> hpoGeneAnnotations = processDiseaseMapToHpoGeneAnnotations(diseases, hpo, diseaseToGeneMap);

    return HpoAssociationData.of(geneIdentifiers, hpoGeneAnnotations, diseaseToGeneAssociations);
  }


  private static List<HpoGeneAnnotation> processDiseaseMapToHpoGeneAnnotations(HpoDiseases diseases,
                                                                               MinimalOntology hpo,
                                                                               Map<TermId, Collection<GeneToAssociation>> diseaseToGeneMap) {
    Map<TermId, Collection<TermId>> phenotypeToDisease = new HashMap<>();
    for (HpoDisease disease : diseases) {
      for (HpoDiseaseAnnotation annotation : disease.annotations()) {
        TermId hpoId = annotation.id();
        phenotypeToDisease.computeIfAbsent(hpoId, k -> new HashSet<>())
          .add(disease.id());
      }
    }

    List<HpoGeneAnnotation> geneAnnotationListBuilder = new ArrayList<>();

    Set<TermId> mappedGenesTracker = new HashSet<>();
    for (TermId phenotype : phenotypeToDisease.keySet()) {
      Optional<Term> phenotypeTerm = hpo.termForTermId(phenotype);
      if (phenotypeTerm.isPresent()) {
        phenotypeToDisease.get(phenotype).stream()
          .flatMap(disease -> diseaseToGeneMap.getOrDefault(disease, List.of()).stream())
          .map(GeneToAssociation::geneIdentifier)
          .forEach((geneId) -> {
            try {
              if (!mappedGenesTracker.contains(geneId.id())) {
                int entrezId = Integer.parseInt(geneId.id().getId());
                String entrezGeneSymbol = geneId.symbol();

                HpoGeneAnnotation geneAnnotation = new HpoGeneAnnotation(entrezId, entrezGeneSymbol, phenotype, phenotypeTerm.get().getName());
                geneAnnotationListBuilder.add(geneAnnotation);
                mappedGenesTracker.add(geneId.id());
              }
            } catch (Exception e) {
              LOGGER.error("An exception found during creating HPO-gene annotations: " + e.getMessage() + " for gene: " + geneId.toString());
            }
          });
      }

      mappedGenesTracker.clear();
    }

    return List.copyOf(geneAnnotationListBuilder);
  }
}
