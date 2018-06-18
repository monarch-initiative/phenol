package org.monarchinitiative.phenol.io.gene2phen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.formats.Gene;
import org.monarchinitiative.phenol.formats.hpo.AssociationType;
import org.monarchinitiative.phenol.formats.hpo.Gene2Association;
import org.monarchinitiative.phenol.formats.hpo.Disease2GeneAssociation;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * <p>This class parses the files {@code mim2gene_medgen}, available from
 * <a href="ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen">ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen</a>
 * as well as the file {@code Homo_sapiens_gene_info.gz}, available from
 * <a href="ftp://ftp.ncbi.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz">
 *     ftp://ftp.ncbi.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz</a>.
 * mim2gene_medgen contains the MIM number of diseases and EntrezGene number of genes associated with the disease;
 * The relevant lines of the file are marked with "phenotype". The Homo_sapiens_gene_info.gz file contains the  entrez gene
 * number of genes as well as their gene symbol. </p>
 * <p>The goal of this class is to return a list of {@link Disease2GeneAssociation} objects
 * representing the gene-to-disease links in OMIM.</p>
 * <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class HpoDisease2GeneParser {

  private final String homoSapiensGeneInfoPath;

  private final String mim2gene_medgenPath;
  /** Key--an EntrezGene id; value--the corresponding symbol. */
  private Map<String,String> geneInfoMap;
  /** Key: an OMIM curie (e.g., OMIM:600100); value--corresponding Gene2Association object). */
  private ImmutableMultimap<TermId,Gene2Association> associationMap;

  private static final TermPrefix ENTREZ_GENE_PREFIX=new TermPrefix("NCBIGene");
  private static final TermPrefix OMIM_PREFIX = new TermPrefix("OMIM");

  public HpoDisease2GeneParser(String geneInfoPath, String mim2gene_medgenPath){
    this.homoSapiensGeneInfoPath =geneInfoPath;
    this.mim2gene_medgenPath=mim2gene_medgenPath;
  }


  public Map<TermId,Disease2GeneAssociation> getDiseaseId2AssociationMap() {
    List<Disease2GeneAssociation> lst = parse();
    ImmutableMap.Builder<TermId,Disease2GeneAssociation> builder = new ImmutableMap.Builder<>();
    for (Disease2GeneAssociation g2p : lst) {
      TermId diseaseId = g2p.getDiseaseId();
      builder.put(diseaseId,g2p);
    }
    return builder.build();
  }


  public Multimap<TermId,TermId> getGene2DiseaseIdMap() {
    List<Disease2GeneAssociation> lst = parse();
    ImmutableMultimap.Builder<TermId,TermId> builder = new ImmutableMultimap.Builder<>();
    for (Disease2GeneAssociation g2p : lst) {
      TermId diseaseId = g2p.getDiseaseId();
      List<Gene> geneList = g2p.getGeneList();
      for (Gene g: geneList) {
        TermId geneId = g.getId();
        builder.put(geneId,diseaseId);
      }
    }
    return builder.build();
  }





  public List<Disease2GeneAssociation> parse() {

    ImmutableList.Builder<Disease2GeneAssociation> builder = new ImmutableList.Builder<>();


    try {
      parseGeneInfo();
      parseMim2GeneMedgen();
      for (TermId omimCurie : associationMap.keySet()) {
        Collection<Gene2Association> g2aList = associationMap.get(omimCurie);
        Disease2GeneAssociation g2p = new Disease2GeneAssociation(omimCurie, ImmutableList.copyOf(g2aList));
        builder.add(g2p);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return builder.build();
  }


  /**
   * Creates a multimap from the medgene_medgen file. We need a multimap because some
   * OMIM phenotype id's (the key of the multimap) are associated with more than one
   * gene (EntrezGene id). This method must be called AFTER {@link #parseGeneInfo()}.
   * @throws IOException if the mim2gene_medgen file cannot be read
   */
  private void parseMim2GeneMedgen() throws IOException {
    ImmutableMultimap.Builder<TermId,Gene2Association> associationBuilder = new ImmutableMultimap.Builder<>();
    BufferedReader br = new BufferedReader(new FileReader(mim2gene_medgenPath));
    String line;
    while ((line=br.readLine())!=null) {
      if (line.startsWith("#")) continue;
      String a[] = line.split("\t");
      if (a[2].equals("phenotype")) {
        String mimid=a[0];
        TermId omimCurie = new TermId(OMIM_PREFIX, mimid);
        String entrezGeneNumber=a[1];
        String symbol = this.geneInfoMap.get(entrezGeneNumber);
        if (symbol==null) {
          symbol="-";
        }
        TermId geneId=new TermId(ENTREZ_GENE_PREFIX,entrezGeneNumber);
        Gene gene = new Gene(geneId,symbol);
        if (a[5].contains("susceptibility")) {
          Gene2Association g2a = new Gene2Association(gene,AssociationType.POLYGENIC);
          associationBuilder.put(omimCurie,g2a);
        } else {
          Gene2Association g2a = new Gene2Association(gene,AssociationType.MENDELIAN);
          associationBuilder.put(omimCurie, g2a);
        }
      }
    }
    associationMap = associationBuilder.build();
  }



  private void parseGeneInfo() throws IOException {
    Map<String,String> genmap=new HashMap<>();
    InputStream fileStream = new FileInputStream(homoSapiensGeneInfoPath);
    InputStream gzipStream = new GZIPInputStream(fileStream);
    Reader decoder = new InputStreamReader(gzipStream);
    BufferedReader br = new BufferedReader(decoder);
    String line;
    while ((line=br.readLine())!=null) {
      String a[] = line.split("\t");
      String taxon=a[0];
      if (! taxon.equals("9606")) continue; // i.e., we want only Homo sapiens sapiens and not Neaderthal etc.
      String geneId=a[1];
      String symbol=a[2];
      genmap.put(geneId,symbol);
      //System.out.println(geneId + ": "+symbol);
    }
    this.geneInfoMap = genmap;
  }







}
