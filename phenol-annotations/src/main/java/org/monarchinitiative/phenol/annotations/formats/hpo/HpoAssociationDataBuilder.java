package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.assoc.*;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.ontology.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * A builder for {@link HpoAssociationData}.
 * <p>
 * In principle, {@link HpoAssociationData} is assembled by providing the following three primary components:
 * <ul>
 *   <li>{@link #geneIdentifiers(GeneIdentifiers)},</li>
 *   <li>{@link #diseaseToGeneAssociations(DiseaseToGeneAssociations)}, and</li>
 *   <li>{@link #hpoGeneAnnotations(HpoGeneAnnotations)}.</li>
 * </ul>
 * However, the builder can create each component if paths or corresponding secondary components are provided.
 * <p>
 *   <b>Gene identifiers</b>: either {@link #homoSapiensGeneInfo(Path, Set)} or {@link #hgncCompleteSetArchive(Path)}.
 * <p>
 *   <b>Disease to gene associations</b>: {@link #mim2GeneMedgen(Path)} and {@link #orphaToGenePath(Path)} (optional).
 * <p>
 *   <b>HPO gene annotations</b>: {@link #hpoDiseases(AnnotatedItemContainer)}.
 * <p>
 * The builder throws {@link MissingFormatArgumentException} if any of the primary components is missing
 * and the secondary components are not provided.
 */
public class HpoAssociationDataBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(HpoAssociationDataBuilder.class);

  // Ontology
  private final Ontology hpo;

  // GeneIdentifierLoader
  private Path homoSapiensGeneInfoPath;
  private Set<GeneInfoGeneType> geneInfoTypes;
  private Path hgncCompleteSetArchivePath;
  private GeneIdentifiers geneIdentifiers;

  // DiseaseToGeneAssociations
  private Path mim2geneMedgenPath;
  private Path orphaToGenePath;
  private DiseaseToGeneAssociations diseaseToGeneAssociations;

  // HpoGeneAnnotations
  // TODO - this is actually a mandatory argument, hence it should be provided in constructor.
  private AnnotatedItemContainer<? extends AnnotatedItem> diseases;
  private HpoGeneAnnotations hpoGeneAnnotations;

  HpoAssociationDataBuilder(Ontology hpo) {
    // private no-op
    this.hpo = Objects.requireNonNull(hpo);
  }

  public HpoAssociationDataBuilder homoSapiensGeneInfo(Path homoSapiensGeneInfo, Set<GeneInfoGeneType> geneInfoTypes) {
    this.homoSapiensGeneInfoPath = homoSapiensGeneInfo;
    this.geneInfoTypes = geneInfoTypes;
    this.hgncCompleteSetArchivePath = null;
    return this;
  }

  public HpoAssociationDataBuilder hgncCompleteSetArchive(Path hgncCompleteSetArchivePath) {
    this.hgncCompleteSetArchivePath = hgncCompleteSetArchivePath;
    this.homoSapiensGeneInfoPath = null;
    this.geneInfoTypes = null;
    return this;
  }

  public HpoAssociationDataBuilder geneIdentifiers(GeneIdentifiers geneIdentifiers) {
    this.geneIdentifiers = geneIdentifiers;
    return this;
  }

  public HpoAssociationDataBuilder mim2GeneMedgen(Path mim2geneMedgenPath) {
    this.mim2geneMedgenPath = mim2geneMedgenPath;
    return this;
  }

  public HpoAssociationDataBuilder orphaToGenePath(Path orphaToGenePath) {
    this.orphaToGenePath = orphaToGenePath;
    return this;
  }

  public HpoAssociationDataBuilder diseaseToGeneAssociations(DiseaseToGeneAssociations diseaseToGeneAssociations) {
    this.diseaseToGeneAssociations = diseaseToGeneAssociations;
    return this;
  }

  public HpoAssociationDataBuilder hpoDiseases(AnnotatedItemContainer<? extends AnnotatedItem> diseases) {
    this.diseases = diseases;
    return this;
  }

  public HpoAssociationDataBuilder hpoGeneAnnotations(HpoGeneAnnotations hpoGeneAnnotations) {
    this.hpoGeneAnnotations = hpoGeneAnnotations;
    return this;
  }

  public HpoAssociationData build() throws MissingPhenolResourceException {
    // GeneIdentifiers
    if (geneIdentifiers == null) {
      LOGGER.debug("Gene identifiers are unset.");
      GeneIdentifierLoader loader;
      Path giPath;
      if (homoSapiensGeneInfoPath != null && geneInfoTypes != null) {
        // load Homo sapiens gene info
        LOGGER.debug("Loading gene identifiers from Homo sapiens gene info file at {}", homoSapiensGeneInfoPath.toAbsolutePath());
        loader = GeneIdentifierLoaders.forHumanGeneInfo(geneInfoTypes);
        giPath = homoSapiensGeneInfoPath;
      } else if (hgncCompleteSetArchivePath != null) {
        // load HGNC complete set archive
        LOGGER.debug("Loading gene identifiers from HGNC complete set archive file at {}", hgncCompleteSetArchivePath.toAbsolutePath());
        loader = GeneIdentifierLoaders.forHgncCompleteSetArchive();
        giPath = hgncCompleteSetArchivePath;
      } else
        throw new MissingPhenolResourceException("Gene identifiers are unset and neither Homo sapiens gene info path nor HGNC complete set archive path is set!");

      try {
        geneIdentifiers = loader.load(giPath);
      } catch (IOException e) {
        throw new MissingPhenolResourceException(e);
      }
    }

    // DiseaseToGeneAssociations
    if (diseaseToGeneAssociations == null) {
      LOGGER.debug("Disease to gene associations are unset.");
      if (mim2geneMedgenPath != null) {
        try {
          LOGGER.debug("Loading disease to gene associations from {}", mim2geneMedgenPath);
          diseaseToGeneAssociations = DiseaseToGeneAssociationLoader.loadDiseaseToGeneAssociations(mim2geneMedgenPath, orphaToGenePath, geneIdentifiers);
        } catch (IOException e) {
          throw new MissingPhenolResourceException(e);
        }
      } else {
        throw new MissingPhenolResourceException("Disease to gene associations and MIM 2 gene medgen paths are unset!");
      }
    }

    // HpoGeneAnnotations
    if (hpoGeneAnnotations == null) {
      LOGGER.debug("HPO gene annotations are unset.");
      if (diseases != null) {
        LOGGER.debug("Loading HPO gene annotations.");
        hpoGeneAnnotations = loadHpoGeneAnnotations(diseases, hpo, diseaseToGeneAssociations.diseaseIdToGeneAssociations());
      } else {
        throw new MissingPhenolResourceException("HPO diseases are unset!");
      }
    }

    return HpoAssociationData.of(geneIdentifiers, hpoGeneAnnotations, diseaseToGeneAssociations);
  }

  private static HpoGeneAnnotations loadHpoGeneAnnotations(AnnotatedItemContainer<? extends AnnotatedItem> diseases,
                                                           MinimalOntology hpo,
                                                           Map<TermId, Collection<GeneToAssociation>> diseaseToGeneMap) {
    Map<TermId, Collection<TermId>> phenotypeToDisease = new HashMap<>();
    for (AnnotatedItem disease : diseases) {
      for (Identified phenotype: disease.annotations()) {
        TermId hpoId = phenotype.id();
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

    return HpoGeneAnnotations.of(geneAnnotationListBuilder);
  }
}
