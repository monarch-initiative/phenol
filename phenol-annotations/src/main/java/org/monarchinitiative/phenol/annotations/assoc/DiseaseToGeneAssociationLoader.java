package org.monarchinitiative.phenol.annotations.assoc;


import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.GeneIdentifiers;
import org.monarchinitiative.phenol.annotations.formats.hpo.AssociationType;
import org.monarchinitiative.phenol.annotations.formats.hpo.DiseaseToGeneAssociation;
import org.monarchinitiative.phenol.annotations.formats.hpo.DiseaseToGeneAssociations;
import org.monarchinitiative.phenol.annotations.formats.hpo.GeneToAssociation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class parses files containing gene to disease associations:
 * <ol>
 *   <li>Homo_sapiens.gene_info.gz (has links between NCBIGene ids and gene symbols)</li>
 *   <li>mim2gene_medgen (has links between OMIM disease ids and genes/NCBIGene ids</li>
 *   <li>Optionally, Orphanet's gene file, en_product6.xml (has links between Orphanet diseases and genes)</li>
 * </ol>
 */
public class DiseaseToGeneAssociationLoader {

  private DiseaseToGeneAssociationLoader() {
  }

  /**
   * @param mim2geneMedgen  path to OMIM disease to gene associations file
   * @param geneIdentifiers gene identifiers container
   * @return a container with mappings between disease ID to {@link GeneToAssociation}.
   * <p>
   * For instance, MICROVASCULAR COMPLICATIONS OF DIABETES, SUSCEPTIBILITY TO, 1; is associated to the gene VEGF as POLYGENIC,
   * and MARFAN SYNDROME is associated to the gene FBN1 as MENDELIAN.
   */
  public static DiseaseToGeneAssociations loadDiseaseToGeneAssociations(Path mim2geneMedgen,
                                                                        GeneIdentifiers geneIdentifiers) throws IOException {
    return loadDiseaseToGeneAssociations(mim2geneMedgen, null, geneIdentifiers);
  }

  /**
   * @param mim2geneMedgen  path to OMIM disease to gene associations file
   * @param orphanet2Gene   path to Ophanet's gene file (ignored if <code>null</code>)
   * @param geneIdentifiers gene identifiers container
   * @return a container with mappings between disease ID to {@link GeneToAssociation}.
   * <p>
   * For instance, MICROVASCULAR COMPLICATIONS OF DIABETES, SUSCEPTIBILITY TO, 1; is associated to the gene VEGF as POLYGENIC,
   * and MARFAN SYNDROME is associated to the gene FBN1 as MENDELIAN.
   */
  public static DiseaseToGeneAssociations loadDiseaseToGeneAssociations(Path mim2geneMedgen,
                                                                        Path orphanet2Gene,
                                                                        GeneIdentifiers geneIdentifiers) throws IOException {
    DiseaseToGeneAssociations omimDiseaseToGeneAssociations = Mim2GeneMedgenLoader.loadDiseaseToGeneAssociations(mim2geneMedgen, geneIdentifiers.geneIdToSymbol());
    DiseaseToGeneAssociations orphaDiseaseToGeneAssociations;
    if (orphanet2Gene == null) {
      return omimDiseaseToGeneAssociations;
    } else {
      Map<String, Collection<TermId>> diseaseMimToGeneIds = prepareDiseaseMimToGeneIds(omimDiseaseToGeneAssociations);
      Map<TermId, Collection<GeneIdentifier>> orphaDiseaseToGeneIds = OrphaGeneToDiseaseParser.parseOrphaGeneXml(orphanet2Gene, diseaseMimToGeneIds, geneIdentifiers.symbolToGeneIdentifier());
      orphaDiseaseToGeneAssociations = postprocessOrphaMappings(orphaDiseaseToGeneIds);

      List<DiseaseToGeneAssociation> associations = Stream.concat(omimDiseaseToGeneAssociations.diseaseToGeneAssociations(), orphaDiseaseToGeneAssociations.diseaseToGeneAssociations())
        .collect(Collectors.toUnmodifiableList());

      return DiseaseToGeneAssociations.of(associations);
    }
  }

  private static Map<String, Collection<TermId>> prepareDiseaseMimToGeneIds(DiseaseToGeneAssociations mimDiseaseToGeneAssociations) {
    Map<String, Collection<TermId>> diseaseMimToGeneIds = new HashMap<>();

    for (DiseaseToGeneAssociation association : mimDiseaseToGeneAssociations) {
      String diseaseMim = association.diseaseId().getId();
      List<TermId> geneIds = association.associations().stream()
        .map(ga -> ga.geneIdentifier().id())
        .collect(Collectors.toUnmodifiableList());
      diseaseMimToGeneIds.put(diseaseMim, geneIds);
    }

    return diseaseMimToGeneIds;
  }

  private static DiseaseToGeneAssociations postprocessOrphaMappings(Map<TermId, Collection<GeneIdentifier>> orphaDiseaseToGeneIds) {
    List<DiseaseToGeneAssociation> orphaAssociations = new ArrayList<>(orphaDiseaseToGeneIds.size());

    for (Map.Entry<TermId, Collection<GeneIdentifier>> entry : orphaDiseaseToGeneIds.entrySet()) {
      TermId orphaDiseaseId = entry.getKey();
      List<GeneToAssociation> geneAssociations = entry.getValue().stream()
        .map(gi -> GeneToAssociation.of(gi, AssociationType.UNKNOWN))
        .collect(Collectors.toUnmodifiableList());

      orphaAssociations.add(DiseaseToGeneAssociation.of(orphaDiseaseId, geneAssociations));
    }

    return DiseaseToGeneAssociations.of(Collections.unmodifiableList(orphaAssociations));
  }

}
