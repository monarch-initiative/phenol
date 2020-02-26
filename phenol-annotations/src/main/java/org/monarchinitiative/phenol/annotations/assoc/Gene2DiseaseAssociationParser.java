package org.monarchinitiative.phenol.annotations.assoc;


import com.google.common.collect.*;
import org.monarchinitiative.phenol.annotations.formats.Gene;
import org.monarchinitiative.phenol.annotations.formats.hpo.AssociationType;
import org.monarchinitiative.phenol.annotations.formats.hpo.GeneToAssociation;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class Gene2DiseaseAssociationParser {
  private static final String ENTREZ_GENE_PREFIX = "NCBIGene";
  private static final String OMIM_PREFIX = "OMIM";

  /** Key--an EntrezGene id; value--the corresponding symbol. */
  private BiMap<TermId,String> allGeneIdToSymbolMap;
  /** Key--an EntrezGene id; value--the corresponding symbol. */
  private ImmutableMap<TermId, String> geneIdToSymbolMap;
  /** Key: an OMIM curie (e.g., OMIM:600100); value--corresponding GeneToAssociation object). For instance,
   * MICROVASCULAR COMPLICATIONS OF DIABETES, SUSCEPTIBILITY TO, 1; is associated to the gene VEGF as POLYGENIC,
   * and MARFAN SYNDROME is associated to the gene FBN1 as MENDELIAN.*/
  private Multimap<TermId,GeneToAssociation> associationMap;


  Gene2DiseaseAssociationParser(String homoSapiensGeneInfo, String mim2geneMedgen, String orphanet2Gene) {
    this(new File(homoSapiensGeneInfo), new File(mim2geneMedgen), new File(orphanet2Gene));
  }

  Gene2DiseaseAssociationParser(File homoSapiensGeneInfoFile, File mim2geneMedgenFile, File orphanet2GeneFile) {
    if (! homoSapiensGeneInfoFile.exists()) {
      throw new PhenolRuntimeException("Cannot find Homo_sapiens.gene_info.gz file");
    }
    if (! mim2geneMedgenFile.exists()) {
      throw new PhenolRuntimeException("Cannot find mim2gene_medgen file");
    }
    if (! orphanet2GeneFile.exists()) {
      throw new PhenolRuntimeException("Cannot find Orphanet en_product6.xml file");
    }
    try {
      parseGeneInfo(homoSapiensGeneInfoFile);
    } catch (IOException e) {
        throw new PhenolRuntimeException("Could not parse Homo_sapiens.gene_info.gz: " +
          homoSapiensGeneInfoFile.getAbsolutePath());
    }
    try {
      parseMim2GeneMedgen(mim2geneMedgenFile);
    } catch (IOException e) {
      throw new PhenolRuntimeException("Could not parse mim2gene_medgen: " +
        mim2geneMedgenFile.getAbsolutePath());
    }
    parseOrphaToGene(orphanet2GeneFile);
  }



  public Multimap<TermId,GeneToAssociation> getAssociationMap() {
    ImmutableMultimap.Builder<TermId,GeneToAssociation> associationBuilder = new ImmutableMultimap.Builder<>();
    associationBuilder.putAll(associationMap);
    return associationBuilder.build();
  }

  public Map<TermId,String> getGeneIdToSymbolMap() { return this.geneIdToSymbolMap;}


  private void parseOrphaToGene(File orphaToGeneFile) {
    Multimap<TermId, String> orphaToGene;
    Map<String, TermId> geneSymbolToId = this.allGeneIdToSymbolMap.inverse();
    try {
      OrphaGeneToDiseaseParser parser = new OrphaGeneToDiseaseParser(orphaToGeneFile);
      orphaToGene = parser.getOrphaDiseaseToGeneSymbolMap();
      for (Map.Entry<TermId, String> entry : orphaToGene.entries()) {
        TermId orpha = entry.getKey();
        String geneSymbol = entry.getValue();
        if (geneSymbolToId.containsKey(geneSymbol)) {
          Gene gene = new Gene(geneSymbolToId.get(geneSymbol), geneSymbol);
          GeneToAssociation g2a = new GeneToAssociation(gene, AssociationType.UNKNOWN);
          if (!associationMap.containsEntry(orpha, g2a)) {
            associationMap.put(orpha, g2a);
          }
        }
      }
    } catch (PhenolException e) {
      System.err.println(e.toString());
    }
  }



  /**
   * Parse the NCBI Homo_sapiens_gene_info.gz file
   * Add the mappings to a Guava bimap, e.g., NCBIGene:150-ADRA2A
   * @throws IOException if the file cannot be read
   */
  private void parseGeneInfo(File homoSapiensGeneInfoFile) throws IOException {
    ImmutableBiMap.Builder<TermId,String> builder=new ImmutableBiMap.Builder<>();
    InputStream fileStream = new FileInputStream(homoSapiensGeneInfoFile);
    InputStream gzipStream = new GZIPInputStream(fileStream);
    Reader decoder = new InputStreamReader(gzipStream);
    BufferedReader br = new BufferedReader(decoder);
    String line;
    // We have seen that occasionally the Homo_sapiens_gene_info.gz
    // contains duplicate lines, which is an error but we do not want the code
    // to crash, so we check for previously found term ids with the seen set.
    // The TermId <-> symbol mapping is one to one.
    Set<TermId> seen = new HashSet<>();
    while ((line=br.readLine())!=null) {
      String[] a = line.split("\t");
      String taxon=a[0];
      if (! taxon.equals("9606")) continue; // i.e., we want only Homo sapiens sapiens and not Neaderthal etc.
      if(!("unknown".equals(a[9]))){
        String geneId=a[1];
        String symbol=a[2];
        TermId tid = TermId.of(ENTREZ_GENE_PREFIX,geneId);
        if (seen.contains(tid)) {
          continue;
        }
        seen.add(tid);
        builder.put(tid,symbol);
      }
    }
    this.allGeneIdToSymbolMap = builder.build();
  }

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
    this.associationMap =  associationMap;
    ImmutableMap.Builder<TermId, String> geneBuilder = new ImmutableMap.Builder<>();
    geneBuilder.putAll(geneMap);
    geneIdToSymbolMap = geneBuilder.build();

   // this.allGeneIdToSymbolMap = null;
  }


}
