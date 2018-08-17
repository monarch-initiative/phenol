package org.monarchinitiative.phenol.io.obo.mpo;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.mpo.MpGene;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.Map;

/**
 * Class to parse a number of files from the MGI site.
 * 1.  MGI_Strain.rpt
 * 2.  MGI_Nonstandard_Strain.rpt
 * 3.  MRK_List2.rpt  (Markers, i.e., mainly genes)
 * 4.  MGI_GenePheno.rpt (for now, omit  MGI_PhenoGenoMP.rpt).
 * 5.  MGI_Pheno_Sex.rpt
 * // Plan to have a parallel parser for http://www.mousephenotype.org/ data.
 * // The MpAnnotation class needs to be flexible enough to work with both
 * // c.f. HpAnnotation and HpDisease
 */
public class MpAnnotationParser {
  /** Path of MGI_GenePheno.rpt file.*/
  private final String genePhenoPath;
  /** Input stream corresponding to MGI_GenePhenot.rpt */
  private final InputStream inputstream;

  /*
  Allelic Composition	Allele Symbol(s)	Allele ID(s)	Genetic Background	Mammalian Phenotype ID	PubMed ID (pipe-delimited)	MGI Marker Accession ID (pipe-delimited)	MGI Genotype Accession ID (pipe-delimited)
   */
  /** Index of allelic compoisition field */
  private final int ALLELIC_COMPIOSITION_IDX=0;

  /** key. an MGI Gene id; value-corresponding MpGene object. */
  private Map<TermId, MpGene> mpGenes;

  /**
   * @param path Path of MGI_GenePheno.rpt file.
   */
  public MpAnnotationParser(String path) throws PhenolException {
    this.genePhenoPath=path;
    // 1. parse mpGenes with MpGeneParser
    // 2. parse MGI_PhenoGeno.rpt file and connect the model to the gene
    try {
      this.inputstream = new FileInputStream(this.genePhenoPath);
      parse();
    } catch (IOException e) {
      throw new PhenolException("Could not parse MGI_GenePheno.rpt: " + e.getMessage());
    }
  }

  public MpAnnotationParser(InputStream is ) throws PhenolException {
    this.genePhenoPath="n/a";
    this.inputstream=is;
    try {
      parse();
    } catch (IOException e) {
      throw new PhenolException("Could not parse MGI_GenePheno.rpt: " + e.getMessage());
    }
  }


  private void parse() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(this.inputstream));
    String line;
    while ((line=br.readLine())!=null) {
      System.out.println(line);
    }

  }








}
