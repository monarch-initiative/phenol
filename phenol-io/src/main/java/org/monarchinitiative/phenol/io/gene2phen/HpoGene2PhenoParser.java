package org.monarchinitiative.phenol.io.gene2phen;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.formats.Gene;
import org.monarchinitiative.phenol.formats.hpo.Gene2Phenotype;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;

import java.io.*;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * <p>This class parses the files {@code mim2gene_medgen}, available from
 * <a href="ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen">ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen</a>
 * as well as the file {@code Homo_sapiens_gene_info.gz}, available from
 * <a href="ftp://ftp.ncbi.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens_gene_info.gz">
 *     ftp://ftp.ncbi.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens_gene_info.gz</a>.
 * mim2gene_medgen contains the MIM number of diseases and EntrezGene number of genes associated with the disease;
 * The relevant lines of the file are marked with "phenotype". The Homo_sapiens_gene_info.gz file contains the  entrez gene
 * number of genes as well as their gene symbol. </p>
 * <p>The goal of this class is to return a list of {@link org.monarchinitiative.phenol.formats.hpo.Gene2Phenotype} objects
 * representing the gene-to-disease links in OMIM.</p>
 * <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class HpoGene2PhenoParser {

  private final String homoSapiensGeneInfoPath;

  private final String mim2gene_medgenPath;

  public HpoGene2PhenoParser(String geneInfoPath, String mim2gene_medgenPath){
    this.homoSapiensGeneInfoPath =geneInfoPath;
    this.mim2gene_medgenPath=mim2gene_medgenPath;
  }


  public List<Gene2Phenotype> parse() {
    TermPrefix ENTREZ_PREFIX = new TermPrefix("NCBIGene");
    TermPrefix OMIM_PREFIX = new TermPrefix("OMIM");
    ImmutableList.Builder<Gene2Phenotype> builder = new ImmutableList.Builder<>();
    try {
      Map<String,String> geneId2symbol=parseGeneInfo();
      Map<String,String> medgenmap=parseMim2GeneMedgen();
      for (Map.Entry<String,String> entry : medgenmap.entrySet()) {
        TermId omimCurie = new TermId(OMIM_PREFIX, entry.getKey());
        String geneid = entry.getValue();
        String symbol="-";
        if (! geneid.equals("-")) {
          if (geneId2symbol.containsKey(geneid)) {
            symbol=geneId2symbol.get(geneid);
            TermId entrez = new TermId(ENTREZ_PREFIX,geneid);
            Gene gene = new Gene(entrez,symbol);
            Gene2Phenotype g2p=new Gene2Phenotype(omimCurie,ImmutableList.of(gene), Gene2Phenotype.AssociationType.MENDELIAN);
            builder.add(g2p);
          }
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return builder.build();
  }



  private Map<String,String> parseMim2GeneMedgen() throws IOException {
    Map<String,String> medgenmap=new HashMap<>();
    BufferedReader br = new BufferedReader(new FileReader(mim2gene_medgenPath));
    String line;
    while ((line=br.readLine())!=null) {
      if (line.startsWith("#")) continue;
      String a[] = line.split("\t");
      if (a[2].equals("phenotype")) {
        String mimid=a[0];
        String geneId=a[1];
        medgenmap.put(mimid,geneId);
      }
    }
    return medgenmap;
  }



  private Map<String,String> parseGeneInfo() throws IOException {
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
    return genmap;
  }







}
