package org.monarchinitiative.phenol.cli.demo;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.io.OntologyLoader;
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
import java.util.stream.Collectors;


/**
 * App for computing pairwise similarity scores between {@link HpoDisease} objects
 * It outputs a list of such similarities to file
 * Optionally, a threshold can be used to control which similarities to output.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax,org">Peter Robinson</a>
 */
public class DiseasePairwiseSimilarityDemo {


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
  public DiseasePairwiseSimilarityDemo(Options options) {
    this.pathHpObo=options.getHpoPath();
    this.pathPhenotypeHpoa=options.getPhenotypeDotHpoaPath();
    this.output_filename=options.getOutname();
  }


  /**
   * Run application.
   */
  public void run() {
    if (pathHpObo==null || pathPhenotypeHpoa == null) {
      System.err.println("Must pass path-to-hp.obo and path-to-phenotype.hpoa");
      System.exit(1);
    }
    final Ontology hpo = OntologyLoader.loadOntology(new File(pathHpObo));
    System.out.println("[INFO] DONE: Loading HPO");
    final Map<TermId, HpoDisease> diseaseMap;
    try {
      List<String> databases = ImmutableList.of("OMIM"); // restrict ourselves to OMIM entries
      HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(this.pathPhenotypeHpoa, hpo,databases);
      diseaseMap = parser.parse();
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
    final ResnikSimilarity resnikSimilarity =
      new ResnikSimilarity(pairwiseResnikSimilarity, false);
    System.out.println(String.format("name: %s  params %s",
      resnikSimilarity.getName(),
      resnikSimilarity.getParameters()));
    // example of computing score between the sets of HPO terms that annotate two
    // diseases (get the diseases at random)
    System.out.println("[INFO] Calculating pairwise phenotype similarity for " + diseaseMap.size() + "diseases." );
    List<HpoDisease> diseaseList = new ArrayList<>(diseaseMap.values());



    int N = diseaseList.size();
    int expectedTotal = N*(N-1)/2;
    double[][] similarityScores = new double[N][N];
    DescriptiveStatistics stats = new DescriptiveStatistics();
    int c=0;
    for (int i=0;i<N;i++) {
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
      for (int i=0;i<N;i++) {
        for (int j = i; j < N; j++) {
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





  @Parameters(commandDescription = "Compute similarity demo")
  public static class Options {
    @Parameter(names = {"-h"}, description = "path to hp.obo file")
    private String hpoPath;
    @Parameter(names="-a", description = "path to phenotype.hpoa file")
    private String phenotypeDotHpoaPath;
    @Parameter(names="-o",description = "output file name")
    private String outname="pairwise_disease_similarity.tsv";

    String getHpoPath() {
      return hpoPath;
    }

    String getPhenotypeDotHpoaPath() {
      return phenotypeDotHpoaPath;
    }

    String getOutname() { return outname; }
  }


}
