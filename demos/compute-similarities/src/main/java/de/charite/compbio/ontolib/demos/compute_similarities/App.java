package de.charite.compbio.ontolib.demos.compute_similarities;

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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.charite.compbio.ontolib.formats.hpo.HpoGeneAnnotation;
import de.charite.compbio.ontolib.formats.hpo.HpoOntology;
import de.charite.compbio.ontolib.formats.hpo.HpoTerm;
import de.charite.compbio.ontolib.formats.hpo.HpoTermRelation;
import de.charite.compbio.ontolib.io.base.TermAnnotationParserException;
import de.charite.compbio.ontolib.io.obo.hpo.HpoGeneAnnotationParser;
import de.charite.compbio.ontolib.io.obo.hpo.HpoOboParser;
import de.charite.compbio.ontolib.ontology.algo.InformationContentComputation;
import de.charite.compbio.ontolib.ontology.data.ImmutableTermId;
import de.charite.compbio.ontolib.ontology.data.TermAnnotations;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermIds;
import de.charite.compbio.ontolib.ontology.scoredist.ScoreDistribution;
import de.charite.compbio.ontolib.ontology.scoredist.ScoreSamplingOptions;
import de.charite.compbio.ontolib.ontology.scoredist.SimilarityScoreSampling;
import de.charite.compbio.ontolib.ontology.similarity.ResnikSimilarity;

/**
 * App for computing similarity scores between gene (by entrez ID) and HPO term list.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class App {

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

    System.err.println("Loading HPO...");
    final HpoOntology hpo;
    try {
      hpo = new HpoOboParser(new File(pathHpObo)).parse();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
      return; // javac complains otherwise
    }
    System.err.println("=> DONE: Loading HPO");

    // Compute list of annoations and mapping from Entrez ID to term IDs.
    System.err.println("Loading HPO to gene annotation file...");
    // Raw annotations as read from file.
    final List<HpoGeneAnnotation> annos = new ArrayList<>();
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
    System.err.println("=> DONE: Loading HPO to gene annotation file");

    // Compute information content of HPO terms, given the term-to-gene annotation.
    System.err.println("Performing IC precomputation...");
    final Map<TermId, Double> icMap =
        new InformationContentComputation<HpoTerm, HpoTermRelation>(hpo)
            .computeInformationContent(termIdToEntrezGeneIds);
    System.err.println("=> DONE: Performing IC precomputation");

    // TODO: Want shortcut for this important case?s
    // Build mapping from numeric Entrez gene ID to
    final Map<Integer, Collection<TermId>> labelToTermIds =
        TermAnnotations.constructTermLabelToAnnotationsMap(annos).entrySet().stream()
            .collect(Collectors.toMap(e -> {
              final String[] tokens = e.getKey().split(":");
              return (Integer) Integer.parseInt(tokens[1]);
            }, e -> e.getValue()));

    // Initialize Resnik similarity without precomputation
    final ResnikSimilarity<HpoTerm, HpoTermRelation> resnikSimilarity =
        new ResnikSimilarity<HpoTerm, HpoTermRelation>(hpo, icMap, false);

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
              entrezGeneIdToTermIds.getOrDefault(entrezId, Sets.newHashSet());

          // Convert string list of HPO terms to TermId objects.
          final List<String> rawHpoTermIds =
              Arrays.asList(arr[nameToCol.get("hpo_terms")].split(","));
          final List<TermId> hpoTermIds = rawHpoTermIds.stream()
              .map(s -> ImmutableTermId.constructWithPrefix(s)).collect(Collectors.toList());

          // Compute Resnik similarity between the two sets of terms.
          final double resnikSim = resnikSimilarity.computeScore(hpoTermIds, geneTermIds);
          // Benchmark...
          List<Double> resnikTimes = new ArrayList<>();
          for (int i = 0; i < 1_000; ++i) {
            final long resnikStartTime = System.nanoTime();
            resnikSimilarity.computeScore(hpoTermIds, geneTermIds);
            final long resnikEndTime = System.nanoTime();
            resnikTimes.add((resnikEndTime - resnikStartTime) / 1_000_000.0);
          }
          double avg = resnikTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
          double stdev = Math.sqrt(resnikTimes.stream().map(x -> (x - avg) * (x - avg))
              .mapToDouble(Double::doubleValue).average().orElse(0.0) / resnikTimes.size());
          System.err.println("Resnik computation took " + avg + " ms (stdev: " + stdev + ")");

          // Compute p value.
          final long pValueStartTime = System.nanoTime();
          final ScoreSamplingOptions options = new ScoreSamplingOptions();
          options.setMinNumTerms(hpoTermIds.size());
          options.setMaxNumTerms(hpoTermIds.size());
          options.setMinObjectId(entrezId);
          options.setMaxObjectId(entrezId);
          final SimilarityScoreSampling<HpoTerm, HpoTermRelation> sampling =
              new SimilarityScoreSampling<HpoTerm, HpoTermRelation>(hpo, resnikSimilarity, options);
          final Map<Integer, ScoreDistribution> scoreDist =
              sampling.performSampling(labelToTermIds);
          final double pValue = scoreDist.get(hpoTermIds.size())
              .getObjectScoreDistribution(entrezId).estimatePValue(resnikSim);
          final long pValueEndTime = System.nanoTime();
          System.err.println(
              "p value computation took " + (pValueStartTime - pValueEndTime) / 1_000_000 + " ms");
          
          List<String> fields = Lists.newArrayList(Arrays.asList(line.split("\t")));
          fields.addAll(ImmutableList.of(Double.toString(resnikSim), Double.toString(pValue)));
          System.out.println(Joiner.on('\t').join(fields));
        }
      }
    } catch (FileNotFoundException e) {
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
