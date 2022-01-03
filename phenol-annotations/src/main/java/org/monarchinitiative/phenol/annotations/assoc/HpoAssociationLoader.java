package org.monarchinitiative.phenol.annotations.assoc;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.annotations.io.hpo.DiseaseDatabase;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseAnnotationLoader;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
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


  public static HpoAssociationData loadHpoAssociationData(Ontology hpo,
                                                          Path homoSapiensGeneInfo,
                                                          Path mim2geneMedgen,
                                                          Path orphaToGene,
                                                          Path phenotypeHpoa,
                                                          Set<DiseaseDatabase> diseaseDatabases) throws IOException {
    GeneIdentifiers geneIdentifiers = GeneInfoLoader.loadGeneIdentifiers(homoSapiensGeneInfo);
    Map<TermId, String> geneIdToSymbol = geneIdentifiers.geneIdToSymbol();

    DiseaseToGeneAssociations diseaseToGeneAssociations = DiseaseToGeneAssociationLoader.loadDiseaseToGeneAssociations(mim2geneMedgen, orphaToGene, geneIdentifiers);
    Map<TermId, Collection<GeneToAssociation>> diseaseToGeneMap = diseaseToGeneAssociations.diseaseIdToGeneAssociations();

    HpoDiseases hpoDiseases = HpoDiseaseAnnotationLoader.loadHpoDiseases(phenotypeHpoa, hpo, diseaseDatabases);
    List<HpoGeneAnnotation> hpoGeneAnnotations = processDiseaseMapToHpoGeneAnnotations(hpo.getTermMap(), diseaseToGeneMap, geneIdToSymbol, hpoDiseases.diseaseById());

    Map<TermId, Collection<GeneIdentifier>> diseaseToGenes = diseaseToGeneAssociations.diseaseIdToGeneIds();
    Map<TermId, Collection<TermId>> geneToDiseases = diseaseToGeneAssociations.geneIdToDiseaseIds();

    return HpoAssociationData.of(geneIdentifiers.geneIdentifiers(), diseaseToGenes, geneToDiseases, hpoGeneAnnotations, diseaseToGeneAssociations);
  }


  private static List<HpoGeneAnnotation> processDiseaseMapToHpoGeneAnnotations(Map<TermId, Term> termIdToTerm,
                                                                               Map<TermId, Collection<GeneToAssociation>> diseaseToGeneMap,
                                                                               Map<TermId, String> geneIdToSymbolMap,
                                                                               Map<TermId, HpoDisease> diseaseMap) {
    Map<TermId, Collection<TermId>> phenotypeToDisease = new HashMap<>();
    for (Map.Entry<TermId, HpoDisease> entry : diseaseMap.entrySet()) {
      for (HpoAnnotation hpoAnnotation : entry.getValue().getPhenotypicAbnormalities()) {
        TermId hpoId = hpoAnnotation.getTermId();
        phenotypeToDisease.computeIfAbsent(hpoId, k -> new HashSet<>())
          .add(entry.getKey());
      }
    }

    List<HpoGeneAnnotation> geneAnnotationListBuilder = new ArrayList<>();

    Set<TermId> mappedGenesTracker = new HashSet<>();
    for (TermId phenotype : phenotypeToDisease.keySet()) {
      Term phenotypeTerm = termIdToTerm.get(phenotype);
      if (phenotypeTerm != null) {
        phenotypeToDisease.get(phenotype).stream()
          .flatMap(disease -> diseaseToGeneMap.getOrDefault(disease, List.of()).stream())
          .map(GeneToAssociation::geneIdentifier)
          .forEach((gene) -> {
            try {
              int entrezId = Integer.parseInt(gene.id().getId());
              if (!mappedGenesTracker.contains(gene.id())) {
                String entrezGeneSymbol = geneIdToSymbolMap.getOrDefault(gene.id(), "-");

                HpoGeneAnnotation geneAnnotation = new HpoGeneAnnotation(entrezId, entrezGeneSymbol, phenotype, phenotypeTerm.getName());
                geneAnnotationListBuilder.add(geneAnnotation);
                mappedGenesTracker.add(gene.id());
              }
            } catch (Exception e) {
              LOGGER.error("An exception found during creating HPO-gene annotations: " + e.getMessage() + " for gene: " + gene.toString());
            }
          });
      }

      mappedGenesTracker.clear();
    }

    return List.copyOf(geneAnnotationListBuilder);
  }
}
