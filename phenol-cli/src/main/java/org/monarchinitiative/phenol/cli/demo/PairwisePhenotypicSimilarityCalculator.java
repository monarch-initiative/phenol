package org.monarchinitiative.phenol.cli.demo;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.io.assoc.HpoAssociationParser;
import org.monarchinitiative.phenol.io.obo.hpo.HpoDiseaseAnnotationParser;
import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermIds;
import org.monarchinitiative.phenol.ontology.similarity.PrecomputingPairwiseResnikSimilarity;
import org.monarchinitiative.phenol.ontology.similarity.ResnikSimilarity;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;



/**
 * App for computing pairwise similarity scores between {@link HpoDisease} objects
 * It outputs a list of such similarities to file
 * Optionally, a threshold can be used to control which similarities to output.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax,org">Peter Robinson</a>
 */
public class PairwisePhenotypicSimilarityCalculator {


  /**
   * Number of threads to use.
   */
  private final int numThreads = 4;


  /**
   * Path to hp.obo file to read.
   */
  private final String pathHpObo;

  /**
   * Path to {@code phenotype.hpoa}
   */
  private final String pathPhenotypeHpoa;



  private final String output_filename;


  private String mimgeneMedgenPath;
  private String geneInfoPath;
  private boolean doGeneBasedAnalysis;

  private Ontology hpo;
  private Map<TermId, HpoDisease> diseaseMap;
  /** order list of HpoDiseases taken from the above map. */
  private List<HpoDisease> diseaseList;
  private Map<TermId,Integer> diseaseIdToIndexMap;
  private ResnikSimilarity resnikSimilarity;
  private  double[][] similarityScores;
  private Multimap<TermId,TermId> geneToDiseaseMap;
  private Map<TermId,String> geneIdToSymbolMap;


  static class DescriptiveStatistics{

    List<Double> vals;
    Double mean=null;
    Double sd=null;

    DescriptiveStatistics(){
      vals = new ArrayList<>();
    }

    void addValue(double v) {
      if (Double.isNaN(v)) {
        System.out.println("[ERROR] skipping NaN similarity value");
        return;
      }
      vals.add(v);
    }

    double getMean() {
      double sum=0.0;
      for (double d : vals) {
        sum += d;
      }
      this.mean=sum/(double)vals.size();
      return this.mean;
    }

    double getSd() {
      if (mean==null) {getMean();}
      final double m = this.mean;
      double sumOfSquares=0.0;
      for (double d : vals) {
        sumOfSquares += (d-m)*(d-m);
      }
      this.sd = Math.sqrt((1.0/vals.size())* sumOfSquares);
      return sd;
    }
  }


  /**
   * Construct with argument list.
   *

   */
  public PairwisePhenotypicSimilarityCalculator(Options options) {
    this.pathHpObo=options.getHpoPath();
    this.pathPhenotypeHpoa=options.getPhenotypeDotHpoaPath();
    this.output_filename=options.getOutname();
    this.geneInfoPath=options.geneInfoPath;
    this.mimgeneMedgenPath=options.mim2genMedgenPath;
    if (geneInfoPath==null || mimgeneMedgenPath==null){
      doGeneBasedAnalysis=false;
      System.out.println("[INFO] We will perform disease-based phenotypic similarity analysis");
    } else {
      doGeneBasedAnalysis=true;
      System.out.println("[INFO] We will perform gene-based phenotypic similarity analysis");
    }
  }


  private double getMaximumGeneGeneSimilarity(TermId geneI, TermId geneJ) {
    double max = 0.0;
    Collection<TermId> diseasesI = this.geneToDiseaseMap.get(geneI);
    Collection<TermId> diseasesJ = this.geneToDiseaseMap.get(geneJ);
    for (TermId i : diseasesI) {
      for (TermId j : diseasesJ) {
        int index_i = this.diseaseIdToIndexMap.get(i);
        int index_j = this.diseaseIdToIndexMap.get(j);
        double s = this.similarityScores[index_i][index_j];
        if (s>max) max=s;
      }
    }
    return max;
  }


  private void performGeneBasedAnalysis() {
    HpoAssociationParser hpoAssociationParser = new HpoAssociationParser(this.geneInfoPath,this.mimgeneMedgenPath,this.hpo);
    hpoAssociationParser.parse();
    this.geneToDiseaseMap = hpoAssociationParser.getGeneToDiseaseIdMap();
    System.out.println("[INFO] geneToDiseaseMap with " + geneToDiseaseMap.size() + " entries");
    this.geneIdToSymbolMap = hpoAssociationParser.getGeneIdToSymbolMap();
    System.out.println("[INFO] geneIdToSymbolMap with " + geneIdToSymbolMap.size() + " entries");
    List<TermId> geneList = new ArrayList<>(geneToDiseaseMap.keySet());
    int N = geneList.size();
    double[][] geneSimilarityMatrix = new double[N][N];
    DescriptiveStatistics stats = new DescriptiveStatistics();
    for (int i=0;i<N-1;i++) {
      for (int j = i+1; j < N; j++) {
        TermId geneI = geneList.get(i);
        TermId geneJ = geneList.get(j);
        double sim = getMaximumGeneGeneSimilarity(geneI,geneJ);
        geneSimilarityMatrix[i][j]=sim;
        geneSimilarityMatrix[j][i]=sim;
        stats.addValue(sim);
      }
    }
    double mean = stats.getMean();
    double sd = stats.getSd();
    System.out.println("\n\n[INFO] Done calculating gene based similarity matrix. Mean="+mean+", sd="+sd);
    double threshold = mean + 2.0*sd;
    System.out.println("[INFO] Writing pairwise gene similarity to file." );
    int aboveThreshold=0;
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(this.output_filename));
      String [] fields = {"gene1","symbol1","gene2","symbol2","similarity"};
      String header = String.join("\t",fields);
      writer.write(header + "\n");
      for (int i=0;i<N-1;i++) {
        for (int j = i+1; j < N; j++) {
          if (geneSimilarityMatrix[i][j]>threshold) {
            TermId geneId1=geneList.get(i);
            TermId geneId2=geneList.get(j);
            String g1 = geneId1.getValue();
            String g2 = geneList.get(j).getValue();
            String symbol1 = this.geneIdToSymbolMap.get(geneId1);
            String symbol2 = this.geneIdToSymbolMap.get(geneId2);
            writer.write(g1 + "\t" + symbol1 + "\t" + g2+"\t"+symbol2+"\t"+geneSimilarityMatrix[i][j] + "\n");
            aboveThreshold++;
          }
        }
      }
      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(String.format("[INFO] Wrote %d above threshold (%.3f) pairwise interactions.",aboveThreshold,threshold) );
  }



  private void performDiseaseBasedAnalysis(DescriptiveStatistics stats) {
    double mean = stats.getMean();
    double sd = stats.getSd();
    System.out.println("\n\nMean="+mean+", sd="+sd);

    double threshold = mean + 2.0*sd;
    System.out.println("[INFO] Writing pairwise phenotype similarity to file." );
    int aboveThreshold=0;
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(this.output_filename));
      String [] fields = {"disease1","disease2","similarity"};
      String header = String.join("\t",fields);
      writer.write(header + "\n");
      int N = diseaseList.size();
      for (int i=0;i<N-1;i++) {
        for (int j = i+1; j < N; j++) {
          if (similarityScores[i][j]>threshold) {
            String d1 = diseaseList.get(i).getDiseaseDatabaseId().getValue();
            String d2 = diseaseList.get(j).getDiseaseDatabaseId().getValue();
            writer.write(d1 + "\t" + d2 + "\t" + similarityScores[i][j] + "\n");
            aboveThreshold++;
          }
        }
      }
      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(String.format("[INFO] Wrote %d above threshold (%.3f) pairwise interactions.",aboveThreshold,threshold) );
  }



  /**
   * Run application.
   */
  public void run() {
    if (pathHpObo==null || pathPhenotypeHpoa == null) {
      System.err.println("[ERROR] Must pass path-to-hp.obo and path-to-phenotype.hpoa");
      System.exit(1);
    }
    this.hpo = OntologyLoader.loadOntology(new File(pathHpObo));
    System.out.println("[INFO] DONE: Loading HPO");

    try {
      List<String> databases = ImmutableList.of("OMIM"); // restrict ourselves to OMIM entries
      HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(this.pathPhenotypeHpoa, hpo,databases);
      this.diseaseMap = parser.parse();
    } catch (PhenolException e) {
      e.printStackTrace();
      System.exit(1);
      return; // javac complains otherwise
    }
    System.out.println("[INFO] DONE: Loading phenotype.hpoa");







    // Compute list of annoations and mapping from OMIM ID to term IDs.
    final Map<TermId, Collection<TermId>> diseaseIdToTermIds = new HashMap<>();
    final Map<TermId, Collection<TermId>> termIdToDiseaseIds = new HashMap<>();

    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      List<TermId> hpoTerms = disease.getPhenotypicAbnormalityTermIdList();
      diseaseIdToTermIds.putIfAbsent(diseaseId, new HashSet<>());
      // add term anscestors
      final Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(hpo, Sets.newHashSet(hpoTerms), true);

      for (TermId tid : inclAncestorTermIds) {
        termIdToDiseaseIds.putIfAbsent(tid, new HashSet<>());
        termIdToDiseaseIds.get(tid).add(diseaseId);
        diseaseIdToTermIds.get(diseaseId).add(tid);
      }
    }

    // Compute information content of HPO terms, given the term-to-disease annotation.
    System.out.println("[INFO] Performing IC precomputation...");
    final Map<TermId, Double> icMap =
      new InformationContentComputation(hpo)
        .computeInformationContent(termIdToDiseaseIds);
    System.out.println("[INFO] DONE: Performing IC precomputation");


    // Initialize Resnik similarity precomputation
    System.out.println("[INFO] Performing Resnik precomputation...");
    final PrecomputingPairwiseResnikSimilarity pairwiseResnikSimilarity =
      new PrecomputingPairwiseResnikSimilarity(hpo, icMap, numThreads);
    System.out.println("[INFO] DONE: Performing Resnik precomputation");
    this.resnikSimilarity =
      new ResnikSimilarity(pairwiseResnikSimilarity, false);
    System.out.println(String.format("name: %s  params %s",
      resnikSimilarity.getName(),
      resnikSimilarity.getParameters()));
    System.out.println("[INFO] Calculating pairwise phenotype similarity for " + diseaseMap.size() + "diseases." );

    this.diseaseList = new ArrayList<>(diseaseMap.values());
    int N = diseaseList.size();
    int expectedTotal = N*(N-1)/2;
    this.similarityScores = new double[N][N];
    DescriptiveStatistics stats = new DescriptiveStatistics();
    int c=0;
    this.diseaseIdToIndexMap=new HashMap<>();
    for (int i=0;i<N;i++) {
      this.diseaseIdToIndexMap.putIfAbsent(diseaseList.get(i).getDiseaseDatabaseId(),i);
      for (int j=i;j<N;j++) {
        HpoDisease d1 = diseaseList.get(i);
        HpoDisease d2 = diseaseList.get(j);
        List<TermId> pheno1 = d1.getPhenotypicAbnormalityTermIdList();
        List<TermId> pheno2 = d2.getPhenotypicAbnormalityTermIdList();
        double similarity = resnikSimilarity.computeScore(pheno1, pheno2);
        similarityScores[i][j]=similarity;
        similarityScores[j][i]=similarity; // symmetric
        stats.addValue(similarity);
        if (++c%10000==0) {
          System.out.print(String.format("Got %d/%d similarity counts \r",c,expectedTotal));
        }
      }
    }


    if (doGeneBasedAnalysis) {
      performGeneBasedAnalysis();
    } else {
      performDiseaseBasedAnalysis(stats);
    }


  }


//HpoAssociationParserString geneInfoPath, String mim2geneMedgenPath,


  @Parameters(commandDescription = "Compute similarity demo")
  public static class Options {
    @Parameter(names = {"-h"}, description = "path to hp.obo file")
    private String hpoPath;
    @Parameter(names="-a", description = "path to phenotype.hpoa file")
    private String phenotypeDotHpoaPath;
    @Parameter(names="-o",description = "output file name")
    private String outname="pairwise_disease_similarity.tsv";
    @Parameter(names="--geneinfo",description = "path to downloaded file ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/GENE_INFO/Mammalia/Homo_sapiens.gene_info.gz")
    private String geneInfoPath;
    @Parameter(names="--mimgene2medgen",description = "path to downloaded file from ftp://ftp.ncbi.nlm.nih.gov/gene/DATA/mim2gene_medgen")
    private String mim2genMedgenPath;
    String getHpoPath() {
      return hpoPath;
    }

    String getPhenotypeDotHpoaPath() {
      return phenotypeDotHpoaPath;
    }

    String getOutname() { return outname; }
  }


}
