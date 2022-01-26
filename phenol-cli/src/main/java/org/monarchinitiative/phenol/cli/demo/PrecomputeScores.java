package org.monarchinitiative.phenol.cli.demo;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoGeneAnnotation;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoSubOntologyRootTermIds;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreSamplingOptions;
import org.monarchinitiative.phenol.ontology.scoredist.SimilarityScoreSampling;
import org.monarchinitiative.phenol.ontology.similarity.ResnikSimilarity;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.monarchinitiative.phenol.analysis.scoredist.ScoreDistributionWriter;
import org.monarchinitiative.phenol.analysis.scoredist.TextFileScoreDistributionWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@code precompute-scores} command.
 * Needs testing after refactor
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class PrecomputeScores {

  /** {@link Logger} object to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(PrecomputeScores.class);

  /** The phenotypic abnormality sub ontology. */
  private final Ontology phenotypicAbnormalitySubOntology;

  private final int numIterations;

  private final int seed;

  private final int numThreads;

  private final String outputScoreDistFile;


  /** The TermId to object ID mapping. */
  private final HashMap<TermId, Collection<TermId>> termIdToObjectId = new HashMap<>();

  private final Map<TermId, Collection<TermId>> objectIdToTermId = new HashMap<>();

  /** The Resnik similarity. */
  private ResnikSimilarity resnikSimilarity;

  /**
   * The resulting score distribution; will be written out.
   *
   * <p>The output is a mapping from term count to {@link ScoreDistribution} which maps Entrez gene
   * ID to empirical p value distribution.
   */
  private Map<Integer, ScoreDistribution> scoreDistribution;

  /** Constructor. */
  public PrecomputeScores(String hpOboPath, int numIter, int seed,int numThreads, String outfile) {
    LOGGER.info("Loading ontology from OBO...");
      Ontology hpo = OntologyLoader.loadOntology(new File(hpOboPath));
    phenotypicAbnormalitySubOntology = hpo.subOntology(HpoSubOntologyRootTermIds.PHENOTYPIC_ABNORMALITY);
    LOGGER.info("Done loading ontology.");
    this.numIterations = numIter;
    this.seed = seed;
    this.numThreads = numThreads;
    this.outputScoreDistFile = outfile;
  }

  /** Execute the command. */
  public void run() {
    printHeader();
    loadOntology();
    precomputePairwiseResnik();
    performSampling();
    writeDistribution();
    printFooter();
  }

  private void printHeader() {
    LOGGER.info("Precomputing Scores");
    LOGGER.info("");
    LOGGER.info("Options");
    LOGGER.info("=======");
    LOGGER.info("");

  }

  private void loadOntology() {


    LOGGER.info("Loading gene-to-term link file...");
    final ArrayList<HpoGeneAnnotation> termAnnotations = new ArrayList<>();

    TermAnnotations.constructTermLabelToAnnotationsMap(phenotypicAbnormalitySubOntology, termAnnotations)
        .forEach(objectIdToTermId::put);

    LOGGER.info("Done loading gene-phenotype links.");
  }

  private void precomputePairwiseResnik() {
    LOGGER.info("Performing information content precomputation...");
    final InformationContentComputation icPrecomputation =
        new InformationContentComputation(phenotypicAbnormalitySubOntology);
    final Map<TermId, Double> termToIc =
        icPrecomputation.computeInformationContent(termIdToObjectId);
    LOGGER.info("Done with precomputing information content.");

    LOGGER.info("Performing pairwise Resnik similarity precomputation...");
    resnikSimilarity =
        new ResnikSimilarity(
            phenotypicAbnormalitySubOntology, termToIc, /* symmetric= */ false);
    LOGGER.info("Done with precomputing pairwise Resnik similarity.");
  }

  private void performSampling() {
    LOGGER.info("Performing sampling...");

    final ScoreSamplingOptions samplingOptions =
        new ScoreSamplingOptions(
            numThreads,
            null, // MinObjectId(),
            null, // MaxObjectId(),
            1,
            20,
            this.seed,
            this.numIterations);

    final SimilarityScoreSampling sampling =
        new SimilarityScoreSampling(
            phenotypicAbnormalitySubOntology, resnikSimilarity, samplingOptions,objectIdToTermId);
    scoreDistribution = sampling.performSampling();

    LOGGER.info("Done with sampling.");
  }

  private void writeDistribution() {
    LOGGER.info("Writing out score distribution...");

    final int resolution = Math.min(1000, Math.max(100, numIterations / 100));

    try (ScoreDistributionWriter writer = new TextFileScoreDistributionWriter(new File(outputScoreDistFile))) {
      for (Entry<Integer, ScoreDistribution> e : scoreDistribution.entrySet()) {
        writer.write(e.getKey(), e.getValue(), resolution);
      }
    } catch (IOException | PhenolException e) {
      throw new RuntimeException("Problem writing to file", e);
    }

    LOGGER.info("Done writing out distribution.");
  }

  private void printFooter() {
    LOGGER.info("All Done.\nHave a nice day!\n");
  }
}
