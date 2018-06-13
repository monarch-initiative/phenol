package org.monarchinitiative.phenol.io.gene2phen;

import org.junit.Test;
import org.monarchinitiative.phenol.formats.hpo.Gene2Phenotype;

import java.util.List;

public class HpoGene2PhenoParserTest {



  @Test
  public void testIt(){
    String geneInfo="/home/robinp/data/ncbi/Homo_sapiens.gene_info.gz";
    String m2gMedgen="/home/robinp/GIT/Robinson-Lab/manuscripts/likratio/scripts/mim2gene_medgen";
    System.out.println("testIt");
    HpoGene2PhenoParser parser = new HpoGene2PhenoParser(geneInfo,m2gMedgen);
    List<Gene2Phenotype> g2p_list = parser.parse();
    for (Gene2Phenotype g2p : g2p_list) {
      System.out.println(g2p);
    }
  }




}
