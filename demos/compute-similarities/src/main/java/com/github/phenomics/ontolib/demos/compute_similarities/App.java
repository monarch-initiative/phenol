package com.github.phenomics.ontolib.demos.compute_similarities;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.github.phenomics.ontolib.formats.hpo.HpoGeneAnnotation;
import com.github.phenomics.ontolib.formats.hpo.HpoOntology;
import com.github.phenomics.ontolib.formats.hpo.HpoTerm;
import com.github.phenomics.ontolib.formats.hpo.Hporelationship;
import com.github.phenomics.ontolib.io.base.TermAnnotationParserException;
import com.github.phenomics.ontolib.io.obo.hpo.HpoGeneAnnotationParser;
import com.github.phenomics.ontolib.io.obo.hpo.HpoOboParser;
import com.github.phenomics.ontolib.ontology.algo.InformationContentComputation;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.TermAnnotations;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.data.TermIds;
import com.github.phenomics.ontolib.ontology.scoredist.ScoreDistribution;
import com.github.phenomics.ontolib.ontology.scoredist.ScoreDistributions;
import com.github.phenomics.ontolib.ontology.scoredist.ScoreSamplingOptions;
import com.github.phenomics.ontolib.ontology.scoredist.SimilarityScoreSampling;
import com.github.phenomics.ontolib.ontology.similarity.PrecomputingPairwiseResnikSimilarity;
import com.github.phenomics.ontolib.ontology.similarity.ResnikSimilarity;

// TODO: This needs some refactorization love

/**
 * App for computing similarity scores between gene (by entrez ID) and HPO term list.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class App {

  /**
   * {@link Logger} object to use.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  /** Number of threads to use. */
  private final int numThreads = 4;

  /** Command line arguments. */
  private final String[] args;

  /** Path to hp.obo file to read. */
  private String pathHpObo;

  /** Path to ALL_SOURCES_ALL_FREQUENCIES_genes_to_phenotype.txt */
  private String pathGeneToPhenoTsvFile;

  /** Path to TSV file to read. */
  private String pathTsvFile;

  /**
   * Construct with argument list.
   *
   * @param args Argument list.
   */
  public App(String[] args) {
    this.args = args;
  }

  /**
   * Run application.
   */
  public void run() {
    this.parseArgs();

    LOGGER.info("Loading HPO...");
    final HpoOntology hpo;
    try {
      hpo = new HpoOboParser(new File(pathHpObo)).parse();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
      return; // javac complains otherwise
    }
    LOGGER.info("DONE: Loading HPO");

    // Compute list of annoations and mapping from Entrez ID to term IDs.
    LOGGER.info("Loading HPO to gene annotation file...");
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
        new HpoGeneAnnotationParser(new File(pathGeneToPhenoTsvFile))) {
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
    LOGGER.info("DONE: Loading HPO to gene annotation file");

    // Compute information content of HPO terms, given the term-to-gene annotation.
    LOGGER.info("Performing IC precomputation...");
    final Map<TermId, Double> icMap =
        new InformationContentComputation<HpoTerm, Hporelationship>(hpo)
            .computeInformationContent(termIdToEntrezGeneIds);
    LOGGER.info("DONE: Performing IC precomputation");

    // TODO: Want shortcut for this important case?s
    // Build mapping from numeric Entrez gene ID to
    final Map<Integer, Collection<TermId>> labelToTermIds =
        TermAnnotations.constructTermLabelToAnnotationsMap(hpo, annos).entrySet().stream()
            .collect(Collectors.toMap(e -> {
              final String[] tokens = e.getKey().split(":");
              return (Integer) Integer.parseInt(tokens[1]);
            }, e -> e.getValue()));

    // Initialize Resnik similarity precomputation
    LOGGER.info("Performing Resnik precomputation...");
    final PrecomputingPairwiseResnikSimilarity<HpoTerm, Hporelationship> pairwiseResnikSimilarity =
        new PrecomputingPairwiseResnikSimilarity<HpoTerm, Hporelationship>(hpo, icMap, numThreads);
    LOGGER.info("DONE: Performing Resnik precomputation");
    final ResnikSimilarity<HpoTerm, Hporelationship> resnikSimilarity =
        new ResnikSimilarity<HpoTerm, Hporelationship>(pairwiseResnikSimilarity, false);

    // Temporary storage of term count to score distributions.
    final Map<Integer, ScoreDistribution> scoreDists = new HashMap<>();

    // Read file line-by line and process.
    Map<String, Integer> nameToCol = null;
    String line;
    try (InputStream fis = new FileInputStream(pathTsvFile);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);) {
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if (nameToCol == null) {
          nameToCol = loadHeader(line);

          List<String> fields = Lists.newArrayList(Arrays.asList(line.split("\t")));
          fields.addAll(Lists.newArrayList("resnik_sim", "p_value"));
          System.out.println(Joiner.on('\t').join(fields));
        } else {
          String arr[] = line.split("\t");
          if (arr.length != nameToCol.size()) {
            System.err.println("Line had wrong field count, expected " + nameToCol.size()
                + ", but was " + arr.length);
            System.exit(1);
          }

          // Compute term set from gene.
          final int entrezId = Integer.parseInt(arr[nameToCol.get("entrez_id")]);
          final Set<TermId> geneTermIds =
              entrezGeneIdToTermIds.getOrDefault(entrezId, new HashSet<>());

          // Convert string list of HPO terms to TermId objects.
          final List<String> rawHpoTermIds =
              Arrays.asList(arr[nameToCol.get("hpo_terms")].split(","));
          // TODO: add convenience routines for converting from term strings, filtering out obsolete
          // ones, possibly falling back to replaced_by
          final List<TermId> unfilteredHpoTermIds = rawHpoTermIds.stream()
              .map(s -> ImmutableTermId.constructWithPrefix(s)).collect(Collectors.toList());
          final List<TermId> hpoTermIds = unfilteredHpoTermIds.stream().filter(termId -> {
            if (hpo.getObsoleteTermIds().contains(termId)) {
              LOGGER.warn("File contained term {} which is marked as obsolete!",
                  new Object[] {termId.getIdWithPrefix()});
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
          if (entrezGeneIdToTermIds.containsKey(entrezId)) {
            // Compute Resnik similarity between the two sets of terms.
            final double resnikSim = resnikSimilarity.computeScore(hpoTermIds, geneTermIds);
            fields.add(Double.toString(resnikSim));

            LOGGER.info("Computing p-value...");
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
              final SimilarityScoreSampling<HpoTerm, Hporelationship> sampling =
                  new SimilarityScoreSampling<HpoTerm, Hporelationship>(hpo, resnikSimilarity,
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
            }
            LOGGER.info("DONE: p-value computation");
          } else {
            LOGGER.warn("Could not perform p value precomputation for Entrez gene ID {}, gene "
                + "not linked to any terms!", new Object[] {entrezId});
            fields.addAll(ImmutableList.of("NA", "NA"));
          }

          System.out.println(Joiner.on('\t').join(fields));
        }
      }
    } catch (

    FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Load header into map from colunm name to column number.
   *
   * @param line Line to parse.
   * @return Mapping from column name to column number.
   */
  private Map<String, Integer> loadHeader(String line) {
    Map<String, Integer> result = new HashMap<>();
    final String[] arr = line.split("\t");
    for (int i = 0; i < arr.length; i++) {
      result.put(arr[i], i);
    }

    if (!result.containsKey("entrez_id")) {
      System.err.println("Column 'entrez_id' is missing!");
      System.exit(1);
    }
    if (!result.containsKey("hpo_terms")) {
      System.err.println("Column 'hpo_terms' is missing!");
      System.exit(1);
    }

    return result;
  }

  /**
   * Parse command line arguments.
   */
  private void parseArgs() {
    if (args.length != 3) {
      printUsageError("Invalid argument count!");
    }

    pathHpObo = args[0];
    pathGeneToPhenoTsvFile = args[1];
    pathTsvFile = args[2];
  }

  /** Print error and usage, then exit. */
  private void printUsageError(String string) {
    System.err.println("ERROR: " + string + "\n");
    System.err.println("Usage: java -jar app.jar hp.obo gene_to_pheno.tsv IN.tsv");
    System.exit(1);
  }

  /**
   * Program entry point.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    new App(args).run();
  }

}
