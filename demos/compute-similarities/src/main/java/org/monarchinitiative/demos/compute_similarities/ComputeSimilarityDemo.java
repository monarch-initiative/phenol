package org.monarchinitiative.demos.compute_similarities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoGeneAnnotation;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.base.TermAnnotationParserException;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.io.obo.hpo.HpoGeneAnnotationParser;
import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.*;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistributions;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreSamplingOptions;
import org.monarchinitiative.phenol.ontology.scoredist.SimilarityScoreSampling;
import org.monarchinitiative.phenol.ontology.similarity.PrecomputingPairwiseResnikSimilarity;
import org.monarchinitiative.phenol.ontology.similarity.ResnikSimilarity;
import org.monarchinitiative.phenol.io.obo.hpo.*;
import org.monarchinitiative.phenol.formats.hpo.*;


// TODO: This needs some refactorization love

/**
 * App for computing similarity scores between gene (by entrez ID) and HPO term list.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ComputeSimilarityDemo {


  /**
   * Number of threads to use.
   */
  private final int numThreads = 4;

  /**
   * Command line arguments.
   */
  private final String[] args;

  /**
   * Path to hp.obo file to read.
   */
  private String pathHpObo;

  /**
   * Path to {@code phenotype.hpoa}
   */
  private String pathPhenotypeHpoa;

  /**
   * Path to TSV file to read
   */
  private String pathTsvFile = "resources/omim-example.txt";

  /**
   * Construct with argument list.
   *
   * @param args Argument list.
   */
  public ComputeSimilarityDemo(String[] args) {
    this.args = args;
  }

  /**
   * Run application.
   */
  public void run() {
    this.parseArgs();

    final HpoOntology hpo;
    try {
      hpo = new HpOboParser(new File(pathHpObo)).parse();
    } catch (IOException | PhenolException e) {
      e.printStackTrace();
      System.exit(1);
      return; // javac complains otherwise
    }
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
    System.out.println("Loading HPO to gene annotation file...");
    final Map<TermId, Set<TermId>> diseaseIdToTermIds = new HashMap<>();
    final Map<TermId, Set<TermId>> termIdToDiseaseIds = new HashMap<>();
    for (TermId diseaseId : diseaseMap.keySet()) {
      HpoDisease disease = diseaseMap.get(diseaseId);
      List<TermId> terms = disease.getPhenotypicAbnormalityTermIdList();
      diseaseIdToTermIds.putIfAbsent(diseaseId, new HashSet<>());
      for (TermId tid : terms) {
        termIdToDiseaseIds.putIfAbsent(tid, new HashSet<>());
        termIdToDiseaseIds.get(tid).add(diseaseId);
        diseaseIdToTermIds.get(diseaseId).add(tid);
      }
    }


    // Compute information content of HPO terms, given the term-to-gene annotation.
    System.out.println("Performing IC precomputation...");
    final Map<TermId, Double> icMap =
      new InformationContentComputation(hpo)
        .computeInformationContent(termIdToDiseaseIds);
    System.out.println("DONE: Performing IC precomputation");


    // Initialize Resnik similarity precomputation
    System.out.println("Performing Resnik precomputation...");
    final PrecomputingPairwiseResnikSimilarity pairwiseResnikSimilarity =
      new PrecomputingPairwiseResnikSimilarity(hpo, icMap, numThreads);
    System.out.println("DONE: Performing Resnik precomputation");
    final ResnikSimilarity resnikSimilarity =
      new ResnikSimilarity(pairwiseResnikSimilarity, false);

    // Temporary storage of term count to score distributions.
    final Map<Integer, ScoreDistribution> scoreDists = new HashMap<>();

    // Read file line-by line and process.


  }



  /**
   * Parse command line arguments.
   */
  private void parseArgs() {
    if (args.length < 2) {
      printUsageError("Invalid argument count!");
    }

    pathHpObo = args[0];
    pathPhenotypeHpoa = args[1];
    if (args.length==3) { // otherwise use default
      pathTsvFile = args[2];
    }
  }

  /**
   * Print error and usage, then exit.
   */
  private void printUsageError(String string) {
    System.err.println("ERROR: " + string + "\n");
    System.err.println("Usage: java -jar ComputeSimilarityDemo.jar hp.obo phenotype.hpoa");
    System.exit(1);
  }

  /**
   * Program entry point.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    new ComputeSimilarityDemo(args).run();
  }


  /**
   * This is various code from the old app -- not sure what it does

  @Deprecated
  private void oldcode() {
    // Compute list of annoations and mapping from Entrez ID to term IDs.
    System.out.println("Loading HPO to gene annotation file...");
    // Raw annotations as read from file.
    final List<HpoGeneAnnotation> annos = new ArrayList<>();
    // TODO: augmentation happens already below?
    // Build mappings from entrez gene ID to term IDs and the inverse. Note that these two mappings
    // are built using the implicit transitive labeling: if a term is anotated with A, then all of
    // its ancestors are labeled with this term as well.
    final Map<Integer, Set<TermId>> entrezGeneIdToTermIds = new HashMap<>();
    final Map<TermId, Set<Integer>> termIdToEntrezGeneIds = new HashMap<>();
    // Build the actual mappings
    try (HpoGeneAnnotationParser annoParser =
           new HpoGeneAnnotationParser(new File(pathPhenotypeHpoa))) {
      while (annoParser.hasNext()) {
        final HpoGeneAnnotation anno = annoParser.next();
        annos.add(anno);

        // TODO: ancestors should be precomputed at some point...
        // Also apply all annotations for all ancestors of the annotation, including root.
        final Set<TermId> inclAncestorTermIds =
          TermIds.augmentWithAncestors(hpo, Sets.newHashSet(anno.getTermId()), true);

        // TODO: can we put this mapping building into a helper function?
        if (entrezGeneIdToTermIds.containsKey(anno.getEntrezGeneId())) {
          entrezGeneIdToTermIds.get(anno.getEntrezGeneId()).addAll(inclAncestorTermIds);
        } else {
          entrezGeneIdToTermIds.put(anno.getEntrezGeneId(), Sets.newHashSet(inclAncestorTermIds));
        }

        for (TermId termId : inclAncestorTermIds) {
          if (termIdToEntrezGeneIds.containsKey(termId)) {
            termIdToEntrezGeneIds.get(termId).add(anno.getEntrezGeneId());
          } else {
            termIdToEntrezGeneIds.put(termId, Sets.newHashSet(anno.getEntrezGeneId()));
          }
        }
      }
    } catch (IOException | TermAnnotationParserException e) {
      e.printStackTrace();
      System.exit(1);
    }
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
          .map(s -> TermId.constructWithPrefix(s)).collect(Collectors.toList());
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
