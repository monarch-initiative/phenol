package org.monarchinitiative.phenol.annotations.assoc;

import org.monarchinitiative.phenol.annotations.formats.GeneIdentifier;
import org.monarchinitiative.phenol.annotations.formats.hpo.AssociationType;
import org.monarchinitiative.phenol.annotations.formats.hpo.DiseaseToGeneAssociation;
import org.monarchinitiative.phenol.annotations.formats.hpo.DiseaseToGeneAssociations;
import org.monarchinitiative.phenol.annotations.formats.hpo.GeneToAssociation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Parse <em>mim2gene_medgen</em>, which is available at <em>https://ftp.ncbi.nih.gov/gene/DATA/mim2gene_medgen</em>
 * into {@link DiseaseToGeneAssociations} container.
 * <p>
 * This file contains links between NCBI Gene Ids and OMIM identifiers for Mendelian and polygenic diseases.
 * The class offers a static function that return a {@link DiseaseToGeneAssociations} container with disease to gene
 * associations. The {@link GeneToAssociation} objects describe genes (as TermIds and Gene Symbols)
 * and the {@link AssociationType} (Mendelian or Polygenic).
 * </p>
 * <p>
 * The class requires data from the NCBI Gene <em>Homo_sapiens.gene_info.gz</em> to get the gene symbol for NCBI Gene ids
 * because the <em>mim2gene_medgen</em> only has the ids (for instance, mim2gene_medgen represents the fibrillin-1 gene
 * by its NCBI Gene id of 2200, and we extract the corresponding gene symbol FBN1 from <em>Homo_sapiens.gene_info.gz</em>).
 * </p>
 *
 * @author Daniel Danis
 * @author Peter N Robinson
 */
public class Mim2GeneMedgenLoader {

  private static final String ENTREZ_GENE_PREFIX = "NCBIGene";
  private static final String OMIM_PREFIX = "OMIM";


  /**
   * Parse the medgen file that contains disease to gene links.
   *
   * @param mim2geneMedgene   Path to mim2gene_medgen file
   * @param geneIdToSymbolMap map with mapping between NCBI Gene IDs and HGVS gene symbols
   * @throws IOException if the mim2gene_medgen cannot be parsed.
   */
  public static DiseaseToGeneAssociations loadDiseaseToGeneAssociations(Path mim2geneMedgene,
                                                                        Map<TermId, String> geneIdToSymbolMap) throws IOException {
    Map<TermId, Collection<GeneToAssociation>> associationMap = new HashMap<>();
    try (BufferedReader br = FileUtils.newBufferedReader(mim2geneMedgene)) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("#")) continue;
        String[] associations = line.split("\t");
        if (associations[2].equals("phenotype")) {
          String mimid = associations[0];
          TermId omimCurie = TermId.of(OMIM_PREFIX, mimid);
          String entrezGeneNumber = associations[1];
          if ("-".equals(entrezGeneNumber)) continue;

          TermId entrezId = TermId.of(ENTREZ_GENE_PREFIX, entrezGeneNumber);
          // rarely, mim2gene has a format error and the following prevents downstream errors with null pointer
          String symbol = geneIdToSymbolMap.getOrDefault(entrezId, "-");

          TermId geneId = TermId.of(ENTREZ_GENE_PREFIX, entrezGeneNumber);
          GeneIdentifier geneIdentifier = GeneIdentifier.of(geneId, symbol);
          AssociationType associationType = associations[5].contains("susceptibility")
            ? AssociationType.POLYGENIC
            : AssociationType.MENDELIAN;
          GeneToAssociation g2a = GeneToAssociation.of(geneIdentifier, associationType);
          associationMap.computeIfAbsent(omimCurie, k -> new HashSet<>()).add(g2a);
        }
      }

      // Make the inner lists unmodifiable.
      List<DiseaseToGeneAssociation> diseaseToGeneAssociations = new ArrayList<>(associationMap.size());
      associationMap.forEach((k, v) -> diseaseToGeneAssociations.add(DiseaseToGeneAssociation.of(k, List.copyOf(v))));

      return DiseaseToGeneAssociations.of(Collections.unmodifiableList(diseaseToGeneAssociations));
    }
  }


}
