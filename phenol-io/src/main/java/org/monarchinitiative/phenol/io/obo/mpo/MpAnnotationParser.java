package org.monarchinitiative.phenol.io.obo.mpo;

import com.google.common.collect.*;
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
  private final InputStream genePhenoInputstream;
  /** Path of MGI_GenePheno.rpt file.*/
  private final String phenoSexPath;
  /** Input stream corresponding to MGI_GenePhenot.rpt */
  private final InputStream phenoSexInputstream;
  /** Used to store sex-specific phenotype annotations. */
  private Map<TermId,Map<TermId,MpAnnotation>> geno2ssannotMap = ImmutableMap.of();

  public Map<TermId, MpModel> getGenotypeAccessionToMpModelMap() {
    return genotypeAccessionToMpModelMap;
  }

  /** Key: A term id representing the genotype accession id of an MPO model
   * Value: the corresponding MpModel object
   */
  private Map<TermId,MpModel> genotypeAccessionToMpModelMap;

  private boolean verbose = true;



  /**
   * @param path Path of MGI_GenePheno.rpt file.
   */
  public MpAnnotationParser(String path) throws PhenolException {
    this.genePhenoPath=path;
    this.phenoSexPath=null;//do not use sex spcific phenotypes
    this.phenoSexInputstream=null; // not needed
    try {
      this.genePhenoInputstream = new FileInputStream(this.genePhenoPath);
      parse();
    } catch (IOException e) {
      throw new PhenolException("Could not parse MGI_GenePheno.rpt: " + e.getMessage());
    }
  }

  /**
   * Input single-gene phenotype data but disregard sex-specific phenotypes.
   * @param is Input stream that corresponds to MGI_GenePheno.rpt
   * @throws PhenolException if there is an I/O problem
   */
  public MpAnnotationParser(InputStream is ) throws PhenolException {
    this.genePhenoPath="n/a"; // unknown since we are starting with an input stream
    this.phenoSexPath="n/a"; // not needed
    this.genePhenoInputstream =is;
    this.phenoSexInputstream=null; // not needed
    try {
      parse();
    } catch (IOException e) {
      throw new PhenolException("Could not parse MGI_GenePheno.rpt: " + e.getMessage());
    }
  }

  /**
   * Input single-gene phenotype data and include sex-specific phenotypes using
   * appropriate modifiers
   * @param phenoSexPath Path to MGI_GenePheno.rpt
   * @param genePhenoPath Path to MGI_Pheno_Sex.rpt
   * @throws PhenolException if there is an I/O problem
   */
  public MpAnnotationParser(String genePhenoPath,String phenoSexPath) throws PhenolException {
    this.genePhenoPath=genePhenoPath;
    this.phenoSexPath=phenoSexPath;
    try {
      this.genePhenoInputstream = new FileInputStream(this.genePhenoPath);
      this.phenoSexInputstream=new FileInputStream((this.phenoSexPath));
      parsePhenoSexData();
      parse();
    } catch (IOException e) {
      throw new PhenolException("Could not parse MGI_GenePheno.rpt: " + e.getMessage());
    }

  }


  /**
   * Parse data from MGI_Pheno_Sex.rpt.
   * Note that there may be multiple lines that suuport the same assertion that differ only in PMID
   * @throws IOException
   * @throws PhenolException
   */
  private void parsePhenoSexData() throws IOException, PhenolException {
    int EXPECTED_NUMBER_SEXSPECIFIC_FIELDS=7;
    BufferedReader br = new BufferedReader(new InputStreamReader(this.phenoSexInputstream));
    //this.sexSpecificAnnotationMap = ArrayListMultimap.create();
    this.geno2ssannotMap=new HashMap<>();
    String line;
    line=br.readLine(); // the header
    if (! line.startsWith("Genotype ID")) {
      throw new PhenolException("Malformed header of MGI_Pheno_Sex.rpt: " + line);
    }
    while ((line=br.readLine())!=null) {
      //System.out.println(line);
      String A[] = line.split("\t");
      if (A.length < EXPECTED_NUMBER_SEXSPECIFIC_FIELDS) {
        if (verbose) {
          //throw new PhenolException("Unexpected number of fields (" + A.length + ") in line " + line);
        System.err.println("[Phenol-ERROR] Unexpected number of fields in MGI_Pheno_Sex.rpt(" + A.length + ") in line " + line);
        }
        continue;
      }
      SexSpecificAnnotationLine ssaline = new SexSpecificAnnotationLine(A);
      TermId genotypeId=ssaline.genotypeID;
      MpAnnotation annot = ssaline.toMpAnnotation();
      geno2ssannotMap.putIfAbsent(genotypeId,new HashMap<>());
      Map<TermId,MpAnnotation> annotset = geno2ssannotMap.get(genotypeId);
      if (annotset.containsKey(annot.getTermId())) {
        // there is a previous annotation for this MP term --
        // the current annotation is from a separate PMID
        MpAnnotation previousannot=annotset.get(annot.getTermId());
        previousannot=previousannot.merge(annot);
      } else {
        annotset.put(annot.getTermId(),annot);
      }
    }
//    for (TermId t : geno2ssannotMap.keySet()) {
//      System.out.println("tt="+t.getIdWithPrefix() + ":" + geno2ssannotMap.get(t));
//    }
  }

  /** Parse the data in MGI_GenePheno.rpt. Interpolate the sex-specific data if available. */
  private void parse() throws IOException, PhenolException {
    BufferedReader br = new BufferedReader(new InputStreamReader(this.genePhenoInputstream));
    Multimap<TermId,AnnotationLine> annotationCollector = ArrayListMultimap.create();
    String line;
    while ((line=br.readLine())!=null) {
      //System.out.println(line);
      String A[] = line.split("\t");
      /* Expected number of fields of the MGI_GenePheno.rpt file (note -- there
         appears to be a stray tab  between the penultimate and last column) */
      int EXPECTED_NUMBER_OF_FIELDS = 9;
      if (A.length < EXPECTED_NUMBER_OF_FIELDS) {
        if (verbose) {
          //throw new PhenolException("Unexpected number of fields (" + A.length + ") in line " + line);
          System.err.println("[Phenol-ERROR] Unexpected number of fields in MGI_GenePheno.rpt (" + A.length + ") in line " + line);
        }
        continue;
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
      // get the sex-specific annotations for this genotypeId, if any
      Map<TermId,MpAnnotation> sexSpecific = ImmutableMap.of(); // default, empty set
      if (this.geno2ssannotMap.containsKey(genoId)) {
        ImmutableMap.Builder<TermId,MpAnnotation> imapbuilder = new ImmutableMap.Builder<>();
        Map<TermId,MpAnnotation> annots = this.geno2ssannotMap.get(genoId);
        for (MpAnnotation mpann : annots.values()) {
          imapbuilder.put(mpann.getTermId(), mpann);
        }
        try {
          sexSpecific = imapbuilder.build();
        } catch (Exception e) {
          System.err.println("Error building map of sex-specific annotations for " + genoId.getIdWithPrefix() +": " + e.getMessage());
        }
      }
      while (it.hasNext()) {
        AnnotationLine aline = it.next();
        MpAnnotation annot = aline.toMpAnnotation();
        TermId mpoId = aline.getMpId();
        background = aline.geneticBackground;
        allelicComp=aline.getAllelicComp();
        alleleId=aline.getAlleleId();
        alleleSymbol=aline.getAlleleSymbol();
        markerId=aline.getMarkerAccessionId();
        // TODO we could check that these are identical for any given genotype id
        // check if we have a sex-specific annotation matching the current annotation
        if (sexSpecific.containsKey(mpoId)) {
          annotbuilder.add(sexSpecific.get(mpoId));
        } else {
          annotbuilder.add(annot); // no sex-specific available
        }
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

  /**
   * A convenience class that allows us to collect all of the annotations that belong
   * to a given model (genotype accession id).
   */
  private static class SexSpecificAnnotationLine {
    TermId genotypeID;
    MpSex sex;
    TermId mpId;
    MpAllelicComposition allelicComposition;
    MpStrain strain;
    boolean negated;
    final List<String> pmidList;

    SexSpecificAnnotationLine(String[] A) throws PhenolException {
      this.genotypeID=TermId.constructWithPrefix(A[0]);
      this.sex = MpSex.fromString(A[1]);
      this.mpId = TermId.constructWithPrefix(A[2]);
      this.allelicComposition =  MpAllelicComposition.fromString(A[3]);
      this.strain = MpStrain.fromString(A[4]);
      negated = A[5].equals("Y");
      String pmids=A[6];
      ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
      String B[]=pmids.split(Pattern.quote("|"));
      for (String b: B) {
        builder.add(b);
      }
      this.pmidList=builder.build();

    }

    public MpAnnotation toMpAnnotation() {
      MpAnnotation.Builder builder = new MpAnnotation.Builder(this.mpId,this.pmidList)
        .sex(this.sex)
        .negated(this.negated);
      return builder.build();
    }


  }



}
