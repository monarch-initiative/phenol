package org.monarchinitiative.phenol.annotations.assoc;


import com.google.common.collect.*;
import org.monarchinitiative.phenol.annotations.formats.Gene;
import org.monarchinitiative.phenol.annotations.formats.hpo.AssociationType;
import org.monarchinitiative.phenol.annotations.formats.hpo.GeneToAssociation;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * This class parses
 * <ol>
 *   <li>Homo_sapiens.gene_info.gz (has links between NCBIGene ids and gene symbols)</li>
 *   <li>mim2gene_medgen (has links between OMIM disease ids and genes/NCBIGene ids</li>
 *   <li>Optionally, Orphanet's gene file, en_product6.xml (has links between Orphanet diseases and genes)</li>
 * </ol>
 */
public class Gene2DiseaseAssociationParser {
  private static final String ENTREZ_GENE_PREFIX = "NCBIGene";
  private static final String OMIM_PREFIX = "OMIM";

  /**
   * Key--an EntrezGene id; value--the corresponding symbol.
   */
  private Map<TermId, String> allGeneIdToSymbolMap;
  /**
   * Key--an EntrezGene id; value--the corresponding symbol.
   */
  private ImmutableMap<TermId, String> geneIdToSymbolMap;
  /**
   * Key: an OMIM curie (e.g., OMIM:600100); value--corresponding GeneToAssociation object). For instance,
   * MICROVASCULAR COMPLICATIONS OF DIABETES, SUSCEPTIBILITY TO, 1; is associated to the gene VEGF as POLYGENIC,
   * and MARFAN SYNDROME is associated to the gene FBN1 as MENDELIAN.
   */
  private Multimap<TermId, GeneToAssociation> associationMap;

  /**
   * This constructor should be chosen to get data about disease links from mim2gene_medgen and
   * Homo sapiens gene info but not from Orphanet.
   *
   * @param homoSapiensGeneInfo Path to Homo_sapiens.gene_info.gz
   * @param mim2geneMedgen      Path to mim2gene_medgen
   */
  Gene2DiseaseAssociationParser(String homoSapiensGeneInfo, String mim2geneMedgen) {
    this(new File(homoSapiensGeneInfo), new File(mim2geneMedgen));
  }

  Gene2DiseaseAssociationParser(File homoSapiensGeneInfoFile, File mim2geneMedgenFile) {
    parseMim2geneAndGeneInfo(homoSapiensGeneInfoFile, mim2geneMedgenFile);
  }

  /**
   * This constructor should be chosen to get data about disease links from mim2gene_medgen and
   * Homo sapiens gene info and Orphanet.
   *
   * @param homoSapiensGeneInfo Path to Homo_sapiens.gene_info.gz
   * @param mim2geneMedgen      Path to mim2gene_medgen
   * @param orphanet2Gene       Path to Orphanet's gene file, en_product6.xml.
   */
  Gene2DiseaseAssociationParser(String homoSapiensGeneInfo, String mim2geneMedgen, String orphanet2Gene) {
    this(new File(homoSapiensGeneInfo), new File(mim2geneMedgen), new File(orphanet2Gene));
  }

  Gene2DiseaseAssociationParser(File homoSapiensGeneInfoFile, File mim2geneMedgenFile, File orphanet2GeneFile) {
    parseMim2geneAndGeneInfo(homoSapiensGeneInfoFile, mim2geneMedgenFile);
    if (!orphanet2GeneFile.exists()) {
      throw new PhenolRuntimeException("Cannot find Orphanet en_product6.xml file");
    }
    parseOrphaToGene(orphanet2GeneFile, mim2geneMedgenFile);
  }

  /**
   * Parse the Homo_sapiens.gene_info.gz and mim2gene_medgen files (needed by both constructors).
   *
   * @param homoSapiensGeneInfoFile
   * @param mim2geneMedgenFile
   */
  private void parseMim2geneAndGeneInfo(File homoSapiensGeneInfoFile, File mim2geneMedgenFile) {
    if (!homoSapiensGeneInfoFile.exists()) {
      throw new PhenolRuntimeException("Cannot find Homo_sapiens.gene_info.gz file");
    }
    if (!mim2geneMedgenFile.exists()) {
      throw new PhenolRuntimeException("Cannot find mim2gene_medgen file");
    }
    this.allGeneIdToSymbolMap = GeneInfoParser.loadGeneIdToSymbolMap(homoSapiensGeneInfoFile);
    try {
      parseMim2GeneMedgen(mim2geneMedgenFile);
    } catch (IOException e) {
      throw new PhenolRuntimeException("Could not parse mim2gene_medgen: " +
        mim2geneMedgenFile.getAbsolutePath());
    }
  }


  public Multimap<TermId, GeneToAssociation> getAssociationMap() {
    ImmutableMultimap.Builder<TermId, GeneToAssociation> associationBuilder = new ImmutableMultimap.Builder<>();
    associationBuilder.putAll(associationMap);
    return associationBuilder.build();
  }

  public Map<TermId, String> getGeneIdToSymbolMap() {
    return this.geneIdToSymbolMap;
  }


  private void parseOrphaToGene(File orphaToGeneFile, File mim2geneMedgenFile) {
    OrphaGeneToDiseaseParser parser = new OrphaGeneToDiseaseParser(orphaToGeneFile, mim2geneMedgenFile);
    Multimap<TermId, Gene> orphaToGene = parser.getOrphaDiseaseToGeneSymbolMap();
    int size_before = associationMap.size();
    for (Map.Entry<TermId, Gene> entry : orphaToGene.entries()) {
      TermId orpha = entry.getKey();
      Gene gene = entry.getValue();
      GeneToAssociation g2a = new GeneToAssociation(gene, AssociationType.UNKNOWN);
      // add to multimap
      associationMap.put(orpha, g2a);
    }
    int size_after = associationMap.size();
    int added = size_after - size_before;
    System.out.printf("Added %d Orphanet entries to association map (total size: %d).\n", added, size_after);
  }


  /**
   * Parse the medgen file that contains disease to gene links.
   * https://ftp.ncbi.nih.gov/gene/DATA/mim2gene_medgen
   * @param mim2geneMedgenFile Path to mim2gene_medgen file
   * @throws IOException if the mim2gene_medgen cannot be parsed.
   */
  public void parseMim2GeneMedgen(File mim2geneMedgenFile) throws IOException {
    Multimap<TermId, GeneToAssociation> associationMap = ArrayListMultimap.create();
    Map<TermId, String> geneMap = new HashMap<>();
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
          String symbol = allGeneIdToSymbolMap.get(entrezId);
          if (!"-".equals(entrezGeneNumber)) {
            if (symbol == null) {
              symbol = "-";
            } else {
              if (!geneMap.containsKey(entrezId)) {
                geneMap.put(entrezId, symbol);
              }
            }
            TermId geneId = TermId.of(ENTREZ_GENE_PREFIX, entrezGeneNumber);
            Gene gene = new Gene(geneId, symbol);
            if (associations[5].contains("susceptibility")) {
              GeneToAssociation g2a = new GeneToAssociation(gene, AssociationType.POLYGENIC);
              if (!associationMap.containsEntry(omimCurie, g2a)) {
                associationMap.put(omimCurie, g2a);
              }
            } else {
              GeneToAssociation g2a = new GeneToAssociation(gene, AssociationType.MENDELIAN);
              if (!associationMap.containsEntry(omimCurie, g2a)) {
                associationMap.put(omimCurie, g2a);
              }
            }
          }
        }
      }
    }
    this.associationMap = associationMap;
    ImmutableMap.Builder<TermId, String> geneBuilder = new ImmutableMap.Builder<>();
    geneBuilder.putAll(geneMap);
    geneIdToSymbolMap = geneBuilder.build();
  }


}
