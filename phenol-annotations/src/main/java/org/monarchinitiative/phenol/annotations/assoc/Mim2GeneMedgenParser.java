package org.monarchinitiative.phenol.annotations.assoc;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.annotations.formats.Gene;
import org.monarchinitiative.phenol.annotations.formats.hpo.AssociationType;
import org.monarchinitiative.phenol.annotations.formats.hpo.GeneToAssociation;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.Map;

/**
 * <p>Parse mim2gene_medgen, which is available at https://ftp.ncbi.nih.gov/gene/DATA/mim2gene_medgen. This file contains
 * links between NCBI Gene Ids and OMIM identifiers for Mendelian and polygenic diseases. The class offers two
 * static functions that return a Multimap whose key is a TermId for an OMIM id, e.g., OMIM:600123. The value of the
 * multimap is a collection of GeneToAssociation objects, which in turn specify genes (as TermIds and Gene Symbols)
 * and the association type (Mendelian or Polygenic).</p>
 * <p>The class requires data from the NCBI Gene Homo_sapiens.gene_info.gz to get the gene symbol for NCBI Gene ids
 * becuase the mim2gene_medgen only has the ids (for instance, mim2gene_medgen represents the fibrillin-1 gene
 * by its NCBI Gene id of 2200, and we extract the corresponding gene symbol FBN1 from Homo_sapiens.gene_info.gz).</p>
 * <p>The class can be contructed from paths to both file or one can alternatively pass Map containing the
 * id to symbol data from Homo_sapiens.gene_info.gz.</p>
 * <pre>{@code
 * String hsGeneInfoPath = "..."; // path to Homo_sapiens.gene_info.gz file
 * String mim2geneMedgenPath = "...";// path to mim2gene_medgen
 * // Option 1
 * Multimap<TermId, GeneToAssociation> idToAssociationMap
 *    = Mim2GeneMedgenParser.loadDiseaseToGeneAssociationMapOMIM(hsGeneInfoPath, mim2geneMedgenPath);
 * // Option 2
 * allGeneIdToSymbolMap = GeneInfoParser.loadGeneIdToSymbolMap(homoSapiensGeneInfoFile);
 * Multimap<TermId, GeneToAssociation> idToAssociationMap
 *    = Mim2GeneMedgenParser.loadDiseaseToGeneAssociationMapOMIM(allGeneIdToSymbolMap, mim2geneMedgenPath);
 *
 * }</pre>
 * @author Peter N Robinson
 */
public class Mim2GeneMedgenParser {
  private static final String ENTREZ_GENE_PREFIX = "NCBIGene";
  private static final String OMIM_PREFIX = "OMIM";


  public static Multimap<TermId, GeneToAssociation>
  loadDiseaseToGeneAssociationMapOMIM(String homoSapiensGeneInfoPath, String mim2geneMedgenPath) {
    Map<TermId, String> geneIdToSymbolMap = GeneInfoParser.loadGeneIdToSymbolMap(homoSapiensGeneInfoPath);
    return loadDiseaseToGeneAssociationMapOMIM(geneIdToSymbolMap, mim2geneMedgenPath);
  }


  /**
   * Parse the medgen file that contains disease to gene links.
   * https://ftp.ncbi.nih.gov/gene/DATA/mim2gene_medgen
   *
   * @param mim2geneMedgene Path to mim2gene_medgen file
   * @throws IOException if the mim2gene_medgen cannot be parsed.
   */
  public static Multimap<TermId, GeneToAssociation>
  loadDiseaseToGeneAssociationMapOMIM(Map<TermId, String> geneIdToSymbolMap, String mim2geneMedgene) {
    File mim2geneMedgenFile = new File(mim2geneMedgene);
    if (!mim2geneMedgenFile.exists()) {
      throw new PhenolRuntimeException("Could not find mim2gene_medgen file at " + mim2geneMedgene);
    }
    Multimap<TermId, GeneToAssociation> associationMap = ArrayListMultimap.create();
    try (BufferedReader br = new BufferedReader(new FileReader(mim2geneMedgenFile))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("#")) continue;
        String[] associations = line.split("\t");
        if (associations[2].equals("phenotype")) {
          String mimid = associations[0];
          TermId omimCurie = TermId.of(OMIM_PREFIX, mimid);
          String entrezGeneNumber = associations[1];
          TermId entrezId = TermId.of(ENTREZ_GENE_PREFIX, entrezGeneNumber);
          String symbol = geneIdToSymbolMap.get(entrezId);
          if ("-".equals(entrezGeneNumber)) {
            continue;
          }
          // rarely, mim2gene has a format error and the following prevents downstream errors with null pointer
          if (symbol == null) {
            symbol = "-";
          }
          TermId geneId = TermId.of(ENTREZ_GENE_PREFIX, entrezGeneNumber);
          Gene gene = new Gene(geneId, symbol);
          AssociationType associationType = AssociationType.MENDELIAN;
          if (associations[5].contains("susceptibility")) {
            associationType = AssociationType.POLYGENIC;
          }
          GeneToAssociation g2a = new GeneToAssociation(gene, associationType);
          if (!associationMap.containsEntry(omimCurie, g2a)) {
            associationMap.put(omimCurie, g2a);
          }
        }
      }
    } catch (IOException e) {
      String msg = String.format("Could not create gene association map: %s", e.getMessage());
      throw new PhenolRuntimeException(msg);
    }
    return associationMap;
  }


}
