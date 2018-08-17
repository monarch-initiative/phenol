package org.monarchinitiative.phenol.io.obo.mpo;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import jdk.nashorn.internal.ir.annotations.Immutable;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.mpo.*;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

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
  /** Expected number of fields of the MGI_GenePheno.rpt file (note -- there
   * appears to be a stray tab  between the penultimate and last column)
   */
  private final int EXPECTED_NUMBER_OF_FIELDS=9;

  public Map<TermId, MpModel> getGenotypeAccessionToMpModelMap() {
    return genotypeAccessionToMpModelMap;
  }

  /** Key: A term id representing the genotype accession id of an MPO model
   * Value: the corresponding MpModel object
   */
  private Map<TermId,MpModel> genotypeAccessionToMpModelMap;



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


  private void parse() throws IOException, PhenolException {
    BufferedReader br = new BufferedReader(new InputStreamReader(this.inputstream));
    Multimap<TermId,AnnotationLine> annotationCollector = ArrayListMultimap.create();
    String line;
    while ((line=br.readLine())!=null) {
      System.out.println(line);
      String A[] = line.split("\t");
      if (A.length < EXPECTED_NUMBER_OF_FIELDS) {
        throw new PhenolException("UNexpected number of fields (" + A.length + ") in line " + line);
      }
      try {
        AnnotationLine annot = new AnnotationLine(A);
        TermId modelId = annot.getGenotypeAccessionId();
        annotationCollector.put(modelId,annot);
      } catch (PhenolException e) {
        e.printStackTrace();
      }
    }
    // When we get here, we have parsed all of the MGI_GenePheno.rpt file.
    // Annotation lines are groups according to genotype accession id in the multimap
    // our goal in the following is to parse everything into corresponding MpModel objects
    ImmutableMap.Builder<TermId,MpModel> builder = new ImmutableMap.Builder<>();
    for (TermId genoId : annotationCollector.keySet()) {
      Collection<AnnotationLine> annotationLines = annotationCollector.get(genoId);
      ImmutableList.Builder<MpAnnotation> annotbuilder = new ImmutableList.Builder<>();
      Iterator<AnnotationLine> it = annotationLines.iterator();
      MpStrain background=null;
      MpAllelicComposition allelicComp=null;
      TermId alleleId=null;
      String alleleSymbol=null;
      TermId markerId=null;
      while (it.hasNext()) {
        AnnotationLine aline = it.next();
        MpAnnotation annot = aline.toMpAnnotation();
        background = aline.geneticBackground;
        allelicComp=aline.getAllelicComp();
        alleleId=aline.getAlleleId();
        alleleSymbol=aline.getAlleleSymbol();
        markerId=aline.getMarkerAccessionId();
        // TODO we could check that these are identical for any given genotype id
        annotbuilder.add(annot);
      }
      MpModel mod = new MpModel(genoId,background,allelicComp,alleleId,alleleSymbol,markerId,annotbuilder.build());
      builder.put(genoId,mod);
    }
    genotypeAccessionToMpModelMap=builder.build();
  }


  /**
   * A convenience class that allows us to collect all of the annotations that belong
   * to a given model (genotype accession id).
   */
   private static class AnnotationLine {
    /** [0] Index of Allelic Composition	Allele Symbol(s) field */
    private final int ALLELIC_COMPOSITION_IDX=0;

    public MpAllelicComposition getAllelicComp() {
      return allelicComp;
    }

    public String getAlleleSymbol() {
      return alleleSymbol;
    }

    public TermId getAlleleId() {
      return alleleId;
    }

    public MpStrain getGeneticBackground() {
      return geneticBackground;
    }

    public TermId getMpId() {
      return mpId;
    }

    public List<String> getPmidList() {
      return pmidList;
    }

    public TermId getMarkerAccessionId() {
      return markerAccessionId;
    }

    public TermId getGenotypeAccessionId() {
      return genotypeAccessionId;
    }

    /** [1] Index of Allele Symbol(s) field. */
    private final int ALLELE_SYMBOL_IDX =1;
    /** [1] Index of Allele ID(s) field. */
    private final int ALLELE_ID_IDX=2;
    /** [2] Index of Genetic Background field */
    private final int GENETIC_BACKGROUND_IDX=3;
    /** [3] Index of Mammalian Phenotype ID */
    private final int MPO_IDX=4;
    /** Index of PubMed ID (pipe-delimited) */
    private final int PUBMED_IDX=5;
    /** Index of MGI Marker Accession ID (pipe-delimited). For example, for a model with
     * a mutation of the RB1 gene, this would be the id for that gene (MGI:97874). */
    private final int MGI_MARKER_IDX=6;
    /** Index of MGI Genotype Accession ID (pipe-delimited) */
    private final int GENOTYPE_ACCESSION_IDX=8;
    final MpAllelicComposition allelicComp;
    final String alleleSymbol;
    final TermId alleleId;
    final MpStrain geneticBackground;
    final TermId mpId;
    final List<String> pmidList;
    final TermId markerAccessionId;
    final TermId genotypeAccessionId;

    AnnotationLine(String[] A) throws PhenolException {
      this.allelicComp = MpAllelicComposition.fromString(A[ALLELIC_COMPOSITION_IDX]);
      this.alleleSymbol = A[ALLELE_SYMBOL_IDX];
      this.alleleId=TermId.constructWithPrefix(A[ALLELE_ID_IDX]);
      this.geneticBackground=MpStrain.fromString(A[GENETIC_BACKGROUND_IDX]);
      this.mpId = TermId.constructWithPrefix(A[MPO_IDX]);
      String pmids=A[PUBMED_IDX];
      ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
      String B[]=pmids.split(Pattern.quote("|"));
      for (String b: B) {
        builder.add(b);
      }
      this.pmidList=builder.build();
      this.markerAccessionId=TermId.constructWithPrefix(A[MGI_MARKER_IDX]);
      this.genotypeAccessionId=TermId.constructWithPrefix(A[GENOTYPE_ACCESSION_IDX]);
    }

    /**
     * TODO -- do we need anything more here?
     * @return
     */
    public MpAnnotation toMpAnnotation() {
      return new MpAnnotation(this.mpId,this.pmidList);
    }

  }



}
