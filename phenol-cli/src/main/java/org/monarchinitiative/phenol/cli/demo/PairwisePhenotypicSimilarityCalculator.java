package org.monarchinitiative.phenol.cli.demo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoAssociationData;
import org.monarchinitiative.phenol.annotations.assoc.HpoAssociationLoader;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoader;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoaderOptions;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoaders;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermIds;
import org.monarchinitiative.phenol.ontology.similarity.PrecomputingPairwiseResnikSimilarity;
import org.monarchinitiative.phenol.ontology.similarity.ResnikSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.monarchinitiative.phenol.annotations.io.hpo.DiseaseDatabase.OMIM;


/**
 * App for computing pairwise similarity scores between {@link HpoDisease} objects
 * It outputs a list of such similarities to file
 * Optionally, a threshold can be used to control which similarities to output.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax,org">Peter Robinson</a>
 */
public class PairwisePhenotypicSimilarityCalculator {
  private final Logger LOGGER = LoggerFactory.getLogger(PairwisePhenotypicSimilarityCalculator.class);
  /**
   * Path to {@code hp.obo}.
   */
  private final Path pathHpObo;

  /**
   * Path to {@code phenotype.hpoa}
   */
  private final Path pathPhenotypeHpoa;

  private final Path outputFilename;

  /** Path to {@code mim2gene_medgen} file with gene to disease associations.*/
  private final Path mimgeneMedgenPath;
  /** Path to {@code Homo_sapiens_gene_info.gz} file. */
  private final Path geneInfoPath;
  /** If true, perform pairwise gene-gene similarity analysis. Otherwise, perform pairwise disease-disease analysis.*/
  private boolean doGeneBasedAnalysis;

  private Ontology hpo;
  private Map<TermId, HpoDisease> diseaseMap;
  /** order list of HpoDiseases taken from the above map. */
  private List<HpoDisease> diseaseList;
  private Map<TermId,Integer> diseaseIdToIndexMap;
  private ResnikSimilarity resnikSimilarity;
  private  double[][] similarityScores;
  private Map<TermId, Collection<TermId>> geneToDiseaseMap;
  private Map<TermId, String> geneIdToSymbolMap;
  private int n_diseases;




  static class DescriptiveStatistics{

    List<Double> vals;
    Double mean=null;
    Double sd=null;

    public int skippedNanValue=0;
    public int goodValue=0;

    DescriptiveStatistics(){
      vals = new ArrayList<>();
    }

    void addValue(double v) {
      if (Double.isNaN(v)) {
        //System.out.println("[ERROR] skipping NaN similarity value");
        skippedNanValue++;
        return;
      }
      goodValue++;
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
   */
  public PairwisePhenotypicSimilarityCalculator(Path hpoPath,
                                                Path hpoaPath,
                                                Path outname,
                                                Path geneInfoPath,
                                                Path mim2genMedgenPath) {
    this.pathHpObo=hpoPath;
    this.pathPhenotypeHpoa=hpoaPath;
    this.outputFilename =outname;
    this.geneInfoPath=geneInfoPath;
    this.mimgeneMedgenPath=mim2genMedgenPath;
    if (geneInfoPath==null || mimgeneMedgenPath==null){
      doGeneBasedAnalysis=false;
      System.out.println("[INFO] We will perform disease-based phenotypic similarity analysis");
    } else {
      doGeneBasedAnalysis=true;
      System.out.println("[INFO] We will perform gene-based phenotypic similarity analysis");
    }
    boolean badFile=false;
    // check existence of Files
    if (!Files.isRegularFile(pathHpObo)) {
      System.err.println("[ERROR] hp.obo file not found at "+pathHpObo);
      badFile=true;
    }
    if (!Files.isRegularFile(pathPhenotypeHpoa)) {
      System.err.println("[ERROR] phenotype.hpoa file not found at "+pathPhenotypeHpoa);
      badFile=true;
    }
    if (geneInfoPath == null || !Files.isRegularFile(geneInfoPath)) {
      System.err.println("[ERROR] Homo_sapiens_gene_info.gz not found at "+geneInfoPath);
      badFile=true;
    }
    if (mimgeneMedgenPath == null || !Files.isRegularFile(mimgeneMedgenPath)) {
      System.err.println("[ERROR] mim2gene_medgen not found at "+mimgeneMedgenPath);
      badFile=true;
    }
    if (badFile) {
      System.err.println("[ERROR] please correct paths and try again");
      System.exit(1);
    }

  }

  /**
   * A gene may be associated with multiple diseases. Here, we take the maximum disease-disease
   * similarity between any disease associated with gene i and any disease associated with gene j
   * @param geneI the first disease gene
   * @param geneJ the second disease gene
   * @return maximum similarity between the genes
   */
  private double getMaximumGeneGeneSimilarity(TermId geneI, TermId geneJ) {
    double max = 0.0;
    Collection<TermId> diseasesI = this.geneToDiseaseMap.get(geneI);
    Collection<TermId> diseasesJ = this.geneToDiseaseMap.get(geneJ);
    if (diseasesI==null) {
      System.out.println("{Could not get diseases for gene " + geneI.getValue());
      return 0;
    }
    if (diseasesJ==null) {
      System.out.println("{Could not get diseases for gene " + geneJ.getValue());
      return 0;
    }
    for (TermId i : diseasesI) {
      for (TermId j : diseasesJ) {
        Integer index_i = this.diseaseIdToIndexMap.get(i);
        Integer index_j = this.diseaseIdToIndexMap.get(j);
        if (index_i==null) {
          LOGGER.error("Could not retrieve index for disease " + i.getValue());
          continue;
        }
        if (index_j==null) {
          LOGGER.error("Could not retrieve index for disease " + j.getValue());
        }
        double s = this.similarityScores[index_i][index_j];
        if (s>max) max=s;
      }
    }
    return max;
  }

  /**
   * Do an analysis to get the maximum pairwise similarity between genes, calculated on the basis
   * of phenotypic similarity of the diseases to which the genes are annotated.
   */
  private void performGeneBasedAnalysis() throws IOException {
    HpoAssociationData hpoAssociationData = HpoAssociationLoader.loadHpoAssociationData(hpo, geneInfoPath, mimgeneMedgenPath, null, pathPhenotypeHpoa, null);
    this.geneToDiseaseMap = hpoAssociationData.geneToDiseases();
    System.out.println("[INFO] geneToDiseaseMap with " + geneToDiseaseMap.size() + " entries");
    this.geneIdToSymbolMap = hpoAssociationData.geneIdToSymbol();
    System.out.println("[INFO] geneIdToSymbolMap with " + geneIdToSymbolMap.size() + " entries");
    List<TermId> geneList = new ArrayList<>(geneToDiseaseMap.keySet());
    int N = geneList.size();
    double[][] geneSimilarityMatrix = new double[N][N];
    DescriptiveStatistics stats = new DescriptiveStatistics();
    for (int i=0;i<N-1;i++) {
      for (int j = i; j < N; j++) {
        try {
          TermId geneI = geneList.get(i);
          TermId geneJ = geneList.get(j);
          if (geneI == null) {
            System.err.println("gene i was null=" + i);
            continue;
          }
          if (geneJ == null) {
            System.err.println("gene j was null=" + j);
            continue;
          }
          if (i > n_diseases) {
            System.err.printf("i=%d but n_diseases=%d\n", i, n_diseases);
            continue;
          }
          if (j > n_diseases) {
            System.err.printf("j=%d but n_diseases=%d\n", j, n_diseases);
            continue;
          }
          double sim = getMaximumGeneGeneSimilarity(geneI, geneJ);
          geneSimilarityMatrix[i][j] = sim;
          geneSimilarityMatrix[j][i] = sim;
          stats.addValue(sim);
        } catch (Exception e){
          System.err.println("i="+i+", j="+j+ " "+e.getMessage());
        }
      }
    }
    double mean = stats.getMean();
    double sd = stats.getSd();
    System.out.println("\n\n[INFO] Done calculating gene based similarity matrix. Mean="+mean+", sd="+sd);
    double threshold = mean + 2.0*sd;
    System.out.println("[INFO] Writing pairwise gene similarity to file." );
    int aboveThreshold=0;
    try (BufferedWriter writer = Files.newBufferedWriter(outputFilename)){
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
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.printf("[INFO] skipped vales: %d, good values %d\n",stats.skippedNanValue,stats.goodValue);
    System.out.printf("[INFO] Wrote %d above threshold (%.3f) pairwise interactions.\n",aboveThreshold,threshold);
  }


  /**
   * Calculate the pairwise disease-disease similarities.
   * @param stats descriptive statices about the comparisons
   */
  private void performDiseaseBasedAnalysis(DescriptiveStatistics stats) {
    double mean = stats.getMean();
    double sd = stats.getSd();
    System.out.println("\n\nMean="+mean+", sd="+sd);

    double threshold = mean + 2.0*sd;
    System.out.println("[INFO] Writing pairwise phenotype similarity to file." );
    int aboveThreshold=0;
    try {
      BufferedWriter writer = Files.newBufferedWriter(outputFilename);
      String [] fields = {"disease1","disease2","similarity"};
      String header = String.join("\t",fields);
      writer.write(header + "\n");
      int N = diseaseList.size();
      for (int i=0;i<N-1;i++) {
        for (int j = i+1; j < N; j++) {
          if (similarityScores[i][j]>threshold) {
            String d1 = diseaseList.get(i).id().getValue();
            String d2 = diseaseList.get(j).id().getValue();
            writer.write(d1 + "\t" + d2 + "\t" + similarityScores[i][j] + "\n");
            aboveThreshold++;
          }
        }
      }
      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.printf("[INFO] Wrote %d above threshold (%.3f) pairwise interactions.\n",aboveThreshold,threshold);
  }



  /**
   * Run application.
   */
  public void run() throws IOException {
    if (pathHpObo==null || pathPhenotypeHpoa == null) {
      System.err.println("[ERROR] Must pass path-to-hp.obo and path-to-phenotype.hpoa");
      System.exit(1);
    }
    this.hpo = OntologyLoader.loadOntology(pathHpObo.toFile());
    System.out.println("[INFO] DONE: Loading HPO");

    // restrict ourselves to OMIM entries
    HpoDiseaseLoader loader = HpoDiseaseLoaders.defaultLoader(hpo, HpoDiseaseLoaderOptions.of(Set.of(OMIM), true, HpoDiseaseLoaderOptions.DEFAULT_COHORT_SIZE));
    HpoDiseases hpoDiseases = loader.load(pathPhenotypeHpoa);
    diseaseMap = hpoDiseases.diseaseById();
    System.out.println("[INFO] DONE: Loading phenotype.hpoa");

    // Compute list of annoations and mapping from OMIM ID to term IDs.
    final Map<TermId, Collection<TermId>> diseaseIdToTermIds = new HashMap<>();
    final Map<TermId, Collection<TermId>> termIdToDiseaseIds = new HashMap<>();

    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      List<TermId> hpoTerms = disease.getPhenotypicAbnormalityTermIds().collect(Collectors.toList());
      diseaseIdToTermIds.putIfAbsent(diseaseId, new HashSet<>());
      // add term ancestors
      final Set<TermId> inclAncestorTermIds = TermIds.augmentWithAncestors(hpo, new HashSet<>(hpoTerms), true);

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
      new PrecomputingPairwiseResnikSimilarity(hpo, icMap);
    System.out.println("[INFO] DONE: Performing Resnik precomputation");
    this.resnikSimilarity =
      new ResnikSimilarity(pairwiseResnikSimilarity, false);
    System.out.printf("name: %s  params %s\n",
      resnikSimilarity.getName(),
      resnikSimilarity.getParameters());
    System.out.println("[INFO] Calculating pairwise phenotype similarity for " + diseaseMap.size() + " diseases." );

    this.diseaseList = new ArrayList<>(diseaseMap.values());
    this.diseaseIdToIndexMap=new HashMap<>();
    for (int i=0;i<diseaseList.size();i++){
      this.diseaseIdToIndexMap.put(diseaseList.get(i).id(),i);
    }
    n_diseases = diseaseList.size();
    int expectedTotal = n_diseases*(n_diseases-1)/2;
    this.similarityScores = new double[n_diseases][n_diseases];
    DescriptiveStatistics stats = new DescriptiveStatistics();
    int c=0;

    for (int i=0;i<n_diseases;i++) {
      for (int j=i;j<n_diseases;j++) {
        HpoDisease d1 = diseaseList.get(i);
        HpoDisease d2 = diseaseList.get(j);
        List<TermId> pheno1 = d1.getPhenotypicAbnormalityTermIds().collect(Collectors.toList());
        List<TermId> pheno2 = d2.getPhenotypicAbnormalityTermIds().collect(Collectors.toList());
        double similarity = resnikSimilarity.computeScore(pheno1, pheno2);
        similarityScores[i][j]=similarity;
        similarityScores[j][i]=similarity; // symmetric
        stats.addValue(similarity);
        if (++c%10000==0) {
          System.out.printf("Got %d/%d similarity counts (%.1f%%)\r",c,expectedTotal,100.0*(double)c/expectedTotal);
        }
      }
    }

    System.out.printf("[INFO] Disease analysis: skipped vales: %d, good values %d\n",stats.skippedNanValue,stats.goodValue);
    if (doGeneBasedAnalysis) {
      performGeneBasedAnalysis();
    } else {
      performDiseaseBasedAnalysis(stats);
    }


  }



}
