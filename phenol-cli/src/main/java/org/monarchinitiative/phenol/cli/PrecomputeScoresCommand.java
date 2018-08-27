package org.monarchinitiative.phenol.cli;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoGeneAnnotation;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.base.TermAnnotationParserException;
import org.monarchinitiative.phenol.io.obo.hpo.HpOboParser;
import org.monarchinitiative.phenol.io.obo.hpo.HpoGeneAnnotationParser;
import org.monarchinitiative.phenol.io.scoredist.ScoreDistributionWriter;
import org.monarchinitiative.phenol.io.scoredist.TextFileScoreDistributionWriter;
import org.monarchinitiative.phenol.ontology.algo.InformationContentComputation;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreDistribution;
import org.monarchinitiative.phenol.ontology.scoredist.ScoreSamplingOptions;
import org.monarchinitiative.phenol.ontology.scoredist.SimilarityScoreSampling;
import org.monarchinitiative.phenol.ontology.similarity.ResnikSimilarity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@code precompute-scores} command.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class PrecomputeScoresCommand {

  /** {@link Logger} object to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(PrecomputeScoresCommand.class);

  /** Configuration parsed from command line. */
  private PrecomputeScoresOptions options;

  /** The HPO ontology. */
  private HpoOntology ontology;

  /** The phenotypic abnormality sub ontology. */
  private ImmutableOntology phenotypicAbnormalitySubOntology;

  /** The object ID to TermId mapping. */
  private TreeMap<Integer, Collection<TermId>> objectIdToTermId = new TreeMap<>();

  /** The TermId to object ID mapping. */
  private HashMap<TermId, Collection<Integer>> termIdToObjectId = new HashMap<>();

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
  public PrecomputeScoresCommand(PrecomputeScoresOptions options) {
    this.options = options;
  }

  /** Execute the command. */
  public void run() throws PhenolException {
    printHeader();
    loadOntology();
    precomputePairwiseResnik();
    performSampling();
    writeDistribution();
    printFooter();
  }

  private void printHeader() {
    LOGGER.info("OntoLib CLI -- Precomputing Scores");
    LOGGER.info("");
    LOGGER.info("Options");
    LOGGER.info("=======");
    LOGGER.info("");
    LOGGER.info(options.toString());
  }

  private void loadOntology() throws PhenolException {
    LOGGER.info("Loading ontology from OBO...");
    HpOboParser hpOboParser;
	try {
		hpOboParser = new HpOboParser(new File(options.getOboFile()));
	    HpoOntology ontology = hpOboParser.parse();
	    phenotypicAbnormalitySubOntology = ontology.getPhenotypicAbnormalitySubOntology();
	} catch (FileNotFoundException e) {
		LOGGER.error(e.getMessage(), e);
	    LOGGER.error("Problem reading from obo file.");
	    return;
	}
    LOGGER.info("Done loading ontology.");

    LOGGER.info("Loading gene-to-term link file...");
    final ArrayList<HpoGeneAnnotation> termAnnotations = new ArrayList<>();
    final File inputFile = new File(options.getGeneToTermLinkFile());
    try (HpoGeneAnnotationParser parser = new HpoGeneAnnotationParser(inputFile)) {
      while (parser.hasNext()) {
        final HpoGeneAnnotation anno = parser.next();
        if (phenotypicAbnormalitySubOntology.getNonObsoleteTermIds().contains(anno.getTermId())) {
          termAnnotations.add(anno);
        }
      }
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
      LOGGER.error("Problem reading from file.");
    } catch (TermAnnotationParserException e) {
      e.printStackTrace();
      LOGGER.error(e.getMessage(), e);
      LOGGER.error("Problem reading from file.");
    }

    TermAnnotations.constructTermLabelToAnnotationsMap(
            phenotypicAbnormalitySubOntology, termAnnotations)
        .forEach(
            (geneId, termIds) -> {
              objectIdToTermId.put(Integer.parseInt(geneId.substring("ENTREZ:".length())), termIds);
            });
    TermAnnotations.constructTermAnnotationToLabelsMap(
            phenotypicAbnormalitySubOntology, termAnnotations)
        .forEach(
            (termId, geneIds) -> {
              termIdToObjectId.put(
                  termId,
                  geneIds
                      .stream()
                      .map(geneId -> Integer.parseInt(geneId.substring("ENTREZ:".length())))
                      .collect(Collectors.toList()));
            });

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
            options.getNumThreads(),
            options.getMinObjectId(),
            options.getMaxObjectId(),
            options.getMinNumTerms(),
            options.getMaxNumTerms(),
            options.getSeed(),
            options.getNumIterations());

    final SimilarityScoreSampling sampling =
        new SimilarityScoreSampling(
            phenotypicAbnormalitySubOntology, resnikSimilarity, samplingOptions);
    scoreDistribution = sampling.performSampling(objectIdToTermId);

    LOGGER.info("Done with sampling.");
  }

  private void writeDistribution() {
    LOGGER.info("Writing out score distribution...");

    final int resolution = Math.min(1000, Math.max(100, options.getNumIterations() / 100));

    try (final ScoreDistributionWriter writer =
        new TextFileScoreDistributionWriter(new File(options.getOutputScoreDistFile()))) {
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
