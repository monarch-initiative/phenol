package org.monarchinitiative.phenol.cli.demo;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.collect.Sets;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.io.obo.hpo.HpoDiseaseAnnotationParser;
import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermIds;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreSamplingOptions;
import org.monarchinitiative.phenol.ontology.scoredist.SimilarityScoreSampling;
import org.monarchinitiative.phenol.ontology.similarity.PrecomputingPairwiseResnikSimilarity;
import org.monarchinitiative.phenol.ontology.similarity.ResnikSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.*;


// TODO: This needs some refactorization love

/**
 * App for computing similarity scores between gene (by entrez ID) and HPO term list.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:peter.robinson@jax,org">Peter Robinson</a>
 */
public class ComputeSimilarityDemo {


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

  /**
   * Path to TSV file to read
   */
  @Deprecated
  private String pathTsvFile = "resources/omim-example.txt";

  /**
   * Construct with argument list.
   *

   */
  public ComputeSimilarityDemo(Options options) {
    this.pathHpObo=options.getHpoPath();
    this.pathPhenotypeHpoa=options.getPhenotypeDotHpoaPath();
  }


  /**
   * Run application.
   */
  public void run() {
    final Ontology hpo = OntologyLoader.loadOntology(new File(pathHpObo));
    System.out.println("DONE: Loading HPO");
    final Map<TermId, HpoDisease> diseaseMap;
    try {
      HpoDiseaseAnnotationParser parser = new HpoDiseaseAnnotationParser(this.pathPhenotypeHpoa, hpo);
      diseaseMap = parser.parse();
    } catch (PhenolException e) {
      e.printStackTrace();
      System.exit(1);
      return; // javac complains otherwise
    }
    // Compute list of annoations and mapping from OMIM ID to term IDs.
    System.out.println("Loading HPO to disease annotation file...");
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
    System.out.println("Performing IC precomputation...");
    final Map<TermId, Double> icMap =
      new InformationContentComputation(hpo)
        .computeInformationContent(termIdToDiseaseIds);
    System.out.println("DONE: Performing IC precomputation");
//    int i=0;
//    for (TermId t:icMap.keySet()) {
//      System.out.println("IC-> "+t.getIdWithPrefix() + ": " + icMap.get(t));
//    }


    // Initialize Resnik similarity precomputation
    System.out.println("Performing Resnik precomputation...");
    final PrecomputingPairwiseResnikSimilarity pairwiseResnikSimilarity =
      new PrecomputingPairwiseResnikSimilarity(hpo, icMap, numThreads);
    System.out.println("DONE: Performing Resnik precomputation");
    final ResnikSimilarity resnikSimilarity =
      new ResnikSimilarity(pairwiseResnikSimilarity, false);
    System.out.println(String.format("name: %s  params %s",
      resnikSimilarity.getName(),
      resnikSimilarity.getParameters()));
    // example of computing score between the sets of HPO terms that annotate two
    // diseases (get the diseases at random)
    System.out.println("About to calculate phenotype similarity from two random diseases from a map of size " + diseaseMap.size());
    List<HpoDisease> valuesList = new ArrayList<>(diseaseMap.values());
    int randomIndex1 = new Random().nextInt(valuesList.size());
    HpoDisease randomDisease1 = valuesList.get(randomIndex1);
    int randomIndex2 = new Random().nextInt(valuesList.size());
    HpoDisease randomDisease2 = valuesList.get(randomIndex2);

    List<TermId> phenoAbnormalities1 = randomDisease1.getPhenotypicAbnormalityTermIdList();
    List<TermId> phenoAbnormalities2 = randomDisease2.getPhenotypicAbnormalityTermIdList();


    double similarity = resnikSimilarity.computeScore(phenoAbnormalities1, phenoAbnormalities2);

    System.out.println(String.format("Similarity score between the query %s and the target disease %s was %.4f",
      randomDisease1.getName(), randomDisease2.getName(), similarity));

    //public final double computeScore(Collection<TermId> query, Collection<TermId> target) {


    // Temporary storage of term count to score distributions.
    Map<Integer, ScoreDistribution> scoreDists;
    // to save computing time for the demo, we just compute score distributions when query term is [4-6]
    ScoreSamplingOptions samplingOption = new ScoreSamplingOptions();
    samplingOption.setMinNumTerms(4);
    samplingOption.setMaxNumTerms(6);
    SimilarityScoreSampling sampleing = new SimilarityScoreSampling(hpo, resnikSimilarity, samplingOption);
    Map<Integer, List<TermId>> diseaseTerm_index = new HashMap<>();
    Map<Integer, TermId> disease_index = new HashMap<>();
    int count = 0;
    for (Map.Entry<TermId, Collection<TermId>> entry : diseaseIdToTermIds.entrySet()) {
      diseaseTerm_index.put(count, new ArrayList<>(entry.getValue()));
      disease_index.put(count, entry.getKey());
      count++;
      if (count > 3) { //to save computing time for the demo, we just compute score distributions for three diseases
        break;
      }
    }
    scoreDists = sampleing.performSampling(diseaseTerm_index);

    double p = scoreDists.get(5).getObjectScoreDistribution(1).estimatePValue(1);
    System.out.println(String.format("If similarity score (using 5 terms as query) is 1, the p-value for disease %s is: %f.", disease_index.get(1).getValue(), p));
    double p2 = scoreDists.get(5).getObjectScoreDistribution(2).estimatePValue(7);
    System.out.println(String.format("If similarity score (using 5 terms as query) is 7, the p-value for disease %s is: %f.", disease_index.get(2).getValue(), p2));

    // Read file line-by line and process.


  }





  @Parameters(commandDescription = "Compute similarity demo")
  public static class Options {
    @Parameter(names = {"-h"}, description = "path to hp.obo file")
    private String hpoPath;
    @Parameter(names="-a", description = "path to phenotype.hpoa file")
    private String phenotypeDotHpoaPath;

    String getHpoPath() {
      return hpoPath;
    }

    String getPhenotypeDotHpoaPath() {
      return phenotypeDotHpoaPath;
    }
  }

  /**
   * This is various code from the old app -- not sure what it does

   @Deprecated private void oldcode() {


   System.out.println("DONE: Loading HPO to gene annotation file");
   try (InputStream fis = new FileInputStream(pathTsvFile);
   InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
   BufferedReader br = new BufferedReader(isr)) {
   while ((line = br.readLine()) != null) {
   // Compute term set from gene.
   final int entrezId = Integer.parseInt(arr[nameToCol.get("entrez_id")]);
   final Set<TermId> geneTermIds = diseaseIdToTermIds.getOrDefault(entrezId, new HashSet<>());
   // Convert string list of HPO terms to TermId objects.
   final List<String> rawHpoTermIds =
   Arrays.asList(arr[nameToCol.get("hpo_terms")].split(","));
   // TODO: add convenience routines for converting from term strings, filtering out obsolete
   // ones, possibly falling back to replaced_by
   final List<TermId> unfilteredHpoTermIds = rawHpoTermIds.stream()
   .map(s -> TermId.of(s)).collect(Collectors.toList());
   final List<TermId> hpoTermIds = unfilteredHpoTermIds.stream().filter(termId -> {
   if (hpo.getObsoleteTermIds().contains(termId)) {
   System.err.println("File contained term which is marked as obsolete!" + termId.getIdWithPrefix());
   return false;
   } else {
   return true;
   }
   }).collect(Collectors.toList());
   // Prepare values to write out.
   final List<String> fields = Lists.newArrayList(Arrays.asList(line.split("\t")));
   // Append names into output.
   fields.add(Joiner.on('|').join(unfilteredHpoTermIds.stream().map(termId -> {
   if (hpo.getObsoleteTermIds().contains(termId)) {
   return hpo.getTermMap().get(termId).getName() + " (obsolete)";
   } else if (hpo.getTermMap().containsKey(termId)) {
   return hpo.getTermMap().get(termId).getName();
   } else {
   return "UNRESOLVABLE: " + termId;
   }
   }).collect(Collectors.toList())));
   // Compute Resnik similarity and p-value if the gene is annotated with any terms.
   if (diseaseIdToTermIds.containsKey(entrezId)) {
   // Compute Resnik similarity between the two sets of terms.
   final double resnikSim = resnikSimilarity.computeScore(hpoTermIds, geneTermIds);
   fields.add(Double.toString(resnikSim));

   System.out.println("Computing p-value...");
   final ScoreSamplingOptions options = new ScoreSamplingOptions();
   options.setNumThreads(numThreads);
   options.setMinNumTerms(hpoTermIds.size());
   options.setMaxNumTerms(hpoTermIds.size());
   options.setMinObjectId(entrezId);
   options.setMaxObjectId(entrezId);
   final int termCount = hpoTermIds.size();

   // Only re-precompute if we have no precomputation value yet.
   if (!scoreDists.containsKey(termCount)
   || scoreDists.get(termCount).getObjectScoreDistribution(entrezId) == null) {
   final SimilarityScoreSampling sampling =
   new SimilarityScoreSampling(hpo, resnikSimilarity,
   options);
   final Map<Integer, ScoreDistribution> tmpDists =
   sampling.performSampling(labelToTermIds);
   if (!scoreDists.containsKey(termCount)) {
   // no need to merge
   scoreDists.put(termCount, tmpDists.get(termCount));
   } else {
   // we need to merge
   scoreDists.put(termCount,
   ScoreDistributions.merge(scoreDists.get(termCount), tmpDists.get(termCount)));
   }

   final double pValue = scoreDists.get(hpoTermIds.size())
   .getObjectScoreDistribution(entrezId).estimatePValue(resnikSim);
   fields.add(Double.toString(pValue));
   System.out.println("DONE: p-value computation");
   } else {
   System.err.println("Could not perform p value precomputation for Entrez gene ID {}, gene "
   + "not linked to any terms!" + entrezId);
   fields.addAll(ImmutableList.of("NA", "NA"));
   }

   System.out.println(Joiner.on('\t').join(fields));
   }
   }
   }
   }
   */


}
