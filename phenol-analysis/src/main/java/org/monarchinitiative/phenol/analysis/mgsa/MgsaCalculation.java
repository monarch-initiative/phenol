package org.monarchinitiative.phenol.analysis.mgsa;

import org.monarchinitiative.phenol.analysis.GoAssociationContainer;
import org.monarchinitiative.phenol.analysis.DirectAndIndirectTermAnnotations;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;



/**
 * This class implements a model-based analysis. The description of the entire
 * method can be found in "GOing Bayesian: model-based gene set analysis of genome-scale data"
 *
 * @author Sebastian Bauer
 * @author Peter Robinson
 * @see <A HREF="http://nar.oxfordjournals.org/content/early/2010/02/19/nar.gkq045.short">GOing Bayesian: model-based gene set analysis of genome-scale data</A>
 */
public class MgsaCalculation {
  private static final Logger logger = LoggerFactory.getLogger(MgsaCalculation.class.getName());

  private final long seed;

  private boolean usePrior = true;

  private boolean integrateParams = false;

  private final DoubleParam alpha = new DoubleParam(MgsaParam.Type.MCMC);
  private final DoubleParam beta = new DoubleParam(MgsaParam.Type.MCMC);
  private final IntegerParam expectedNumberOfTerms = new IntegerParam(MgsaParam.Type.MCMC);

  private boolean takePopulationAsReference = false;
  private boolean randomStart = false;
  private final static int DEFAULT_MCMCSTEPS = 250_000;
  private final int mcmcSteps;
  private final int burnin = 20000;
  private int updateReportTime = 1000; /* Update report time in ms */

  private final GoAssociationContainer goAssociations;

  private final TermToItemMatrix termToItemMatrix;
  /**
   * Reference to ontology (usually Gene Ontology).
   */
  private final Ontology ontology;

  private final StudySet populationSet;


  /**
   * If this constructor is used, then all annotated genes are taken to be the population set.
   *
   * @param ontology       reference to Gene Ontology
   * @param goAssociations reference to contained with GO <-> gene associations
   * @param mcmcSteps      Number of iterations of MCMC to perform
   */
  public MgsaCalculation(Ontology ontology,
                         GoAssociationContainer goAssociations,
                         int mcmcSteps,
                         long seed) {
    this.ontology = ontology;
    this.goAssociations = goAssociations;
    this.mcmcSteps = mcmcSteps;
    this.seed = seed;
    Objects.requireNonNull(goAssociations);
    System.err.println("MgsaCalculation, mcsc steps " + mcmcSteps);
    this.termToItemMatrix = new TermToItemMatrix(goAssociations);

    Set<TermId> allAnnotatedGenes = goAssociations.getAllAnnotatedGenes();
    Map<TermId, DirectAndIndirectTermAnnotations> assocs = goAssociations.getAssociationMap(allAnnotatedGenes);
    this.populationSet =  StudySet.populationSet(assocs);
  }

  /**
   * This constructor will generate a new random seed.
   * @param ontology reference to target ontology, usually GO
   * @param goAssociations Container of associations to items, usually genes
   * @param mcmcSteps Number of MCMC steps to take
   */
  public MgsaCalculation(Ontology ontology,
                         GoAssociationContainer goAssociations,
                         int mcmcSteps) {
      this(ontology,goAssociations,mcmcSteps,new Random().nextLong());
  }

  /**
   * @return the total count of genes (items) in the population set.
   */
  public int getPopulationSetCount() {
    return populationSet.getAnnotatedItemCount();
  }


  /**
   * Sets a fixed value for the alpha parameter.
   *
   * @param alpha false-positive rate of MGSA
   */
  public void setAlpha(double alpha) {
    if (alpha < 0.000001) alpha = 0.000001;
    if (alpha > 0.999999) alpha = 0.999999;
    this.alpha.setValue(alpha);
  }

  /**
   * Sets a fixed value for the beta parameter.
   *
   * @param beta false-negative rate of MGSA
   */
  public void setBeta(double beta) {
    if (beta < 0.000001) beta = 0.000001;
    if (beta > 0.999999) beta = 0.999999;
    this.beta.setValue(beta);
  }

  /**
   * Sets the type of the alpha parameter.
   *
   * @param alpha false-positive parameter for MGSA algorithm
   */
  public void setAlpha(MgsaParam.Type alpha) {
    this.alpha.setType(alpha);
  }

  /**
   * Sets the bounds of the alpha parameter.
   *
   * @param min minimum value of the false-positive rate of MGSA
   * @param max maximum value of the false-positive rate of MGSA
   */
  public void setAlphaBounds(double min, double max) {
    this.alpha.setMin(min);
    this.alpha.setMax(max);
  }

  /**
   * Sets the type of the beta parameter.
   *
   * @param beta false-negative parameter for MGSA algorithm
   */
  public void setBeta(MgsaParam.Type beta) {
    this.beta.setType(beta);
  }

  /**
   * Sets the bounds of the beta parameter.
   *
   * @param min minimum value of the false-negative rate of MGSA
   * @param max maximum value of the false-negative rate of MGSA
   */
  public void setBetaBounds(double min, double max) {
    this.beta.setMin(min);
    this.beta.setMax(max);
  }

  /**
   * @param expectedNumber specifies the expected number of terms.
   */
  public void setExpectedNumber(int expectedNumber) {
    this.expectedNumberOfTerms.setValue(expectedNumber);
  }

  /**
   * @param type specifies the type of the expected number variable.
   */
  public void setExpectedNumber(MgsaParam.Type type) {
    this.expectedNumberOfTerms.setType(type);
  }

  /**
   * @param integrateParams flag to set whether the parameter should be integrated.
   */
  public void setIntegrateParams(boolean integrateParams) {
    this.integrateParams = integrateParams;
  }

  /**
   * Sets whether all terms that are annotated to the population set should be
   * considered.
   *
   * @param takePopulationAsReference
   */
  public void setTakePopulationAsReference(boolean takePopulationAsReference) {
    this.takePopulationAsReference = takePopulationAsReference;
  }

  /**
   * @param randomStart true if a random start should be used.
   */
  public void useRandomStart(boolean randomStart) {
    this.randomStart = randomStart;
  }

  /**
   * Sets the update report time.
   *
   * @param updateReportTime
   */
  public void setUpdateReportTime(int updateReportTime) {
    this.updateReportTime = updateReportTime;
  }

  public MgsaGOTermsResultContainer calculateStudySet(StudySet studySet) {
    MgsaGOTermsResultContainer result = new MgsaGOTermsResultContainer(ontology,
      goAssociations,
      studySet,
      getPopulationSetCount());
    if (studySet.getAnnotatedItemCount() == 0) {
      System.err.println("[WARNING] Study set empty! Returning specious result");
      return result;
    }


    logger.info("Starting calculation: expectedNumberOfTerms=" + expectedNumberOfTerms +
      " alpha=" + alpha +
      " beta=" + beta +
      " numberOfPop=" + getPopulationSetCount() +
      " numberOfStudy=" + studySet.getAnnotatedItemCount());
  this.expectedNumberOfTerms.setValue(10);
    long start = System.currentTimeMillis();
    calculateByMCMC(result, studySet);
    long end = System.currentTimeMillis();
    logger.info((end - start) + "ms");
    return result;
  }


  public void setUsePrior(boolean usePrior) {
    this.usePrior = usePrior;
  }

  private void calculateByMCMC(MgsaGOTermsResultContainer result,
                               StudySet studySet) {
    Objects.requireNonNull(goAssociations);
    TermToItemMatrix calcUtils = new TermToItemMatrix(goAssociations);
    int[][] termLinks = calcUtils.getTermLinks();
    boolean[] observedItems = calcUtils.getBooleanArrayobservedItems(studySet.getGeneSet()); //geneMapper.getDense(studyEnumerator.getGenes());
    double[] marginalProbabilities = calculate(termLinks, observedItems);

    for (int i = 0; i < marginalProbabilities.length; i++) {
      TermId tid = calcUtils.getGoTermAtIndex(i);
      MgsaGOTermResult prop = new MgsaGOTermResult(tid,
        calcUtils.getAnnotatedGeneCount(tid),
        populationSet.getAnnotatedItemCount(),
        marginalProbabilities[i]);
      result.addGOTermProperties(prop);
    }

  }



  public String getName() {
    return "MGSA";
  }


  public boolean supportsTestCorrection() {
    return false;
  }


  /**
   * Perform the calculation.
   *
   * @param term2Items rows: indices of terms; columns: indices of genes
   * @param observedItems true if a gene is observed
   * @return a vector of marginal probabilities for each term.
   */
  private double[] calculate(int[][] term2Items, boolean[] observedItems) {
    int numTerms = term2Items.length;
    double[] res = new double[numTerms];

    Random rnd = new Random(seed);
    logger.info("Using random seed of: " + seed);

    int maxIter;

    double alpha;
    double beta;
    double expectedNumberOfTerms;
    alpha = Double.NaN;
    beta = Double.NaN;
    expectedNumberOfTerms = Double.NaN;

    maxIter = 1;

    for (int i = 0; i < maxIter; i++) {
      FixedAlphaBetaScore fixedAlphaBetaScore = new FixedAlphaBetaScore(rnd, term2Items, observedItems);
      fixedAlphaBetaScore.setIntegrateParams(integrateParams);
      logger.info("MCMC only: " + alpha + "  " + beta + "  " + expectedNumberOfTerms);
      fixedAlphaBetaScore.setAlpha(alpha);
      if (this.alpha.hasMax())
        fixedAlphaBetaScore.setMaxAlpha(this.alpha.getMax());
      fixedAlphaBetaScore.setBeta(beta);
      if (this.beta.hasMax())
        fixedAlphaBetaScore.setMaxBeta(this.beta.getMax());
      fixedAlphaBetaScore.setExpectedNumberOfTerms(expectedNumberOfTerms);
      fixedAlphaBetaScore.setUsePrior(usePrior);

      logger.info("Score of empty set: " + fixedAlphaBetaScore.getScore());

      /* Provide a starting point */
      if (randomStart) {
        int numberOfTerms = fixedAlphaBetaScore.EXPECTED_NUMBER_OF_TERMS[rnd.nextInt(fixedAlphaBetaScore.EXPECTED_NUMBER_OF_TERMS.length)];
        double pForStart = ((double) numberOfTerms) / term2Items.length;

        for (int j = 0; j < term2Items.length; j++)
          if (rnd.nextDouble() < pForStart) fixedAlphaBetaScore.switchState(j);

        logger.info("Starting with " + fixedAlphaBetaScore.getActiveTerms().length + " terms (p=" + pForStart + ")");
      }

      double score = fixedAlphaBetaScore.getScore();
      logger.info("Score of initial set: " + score);

      int maxSteps = mcmcSteps;
      int burnin = 20000;
      int numAccepts = 0;
      int numRejects = 0;


      double maxScore = score;
      int[] maxScoredTerms = fixedAlphaBetaScore.getActiveTerms();
      double maxScoredAlpha = Double.NaN;
      double maxScoredBeta = Double.NaN;
      double maxScoredP = Double.NaN;
      int maxWhenSeen = -1;

      long start = System.currentTimeMillis();

      for (int t = 0; t < maxSteps; t++) {
        /* Remember maximum score and terms */
        if (score > maxScore) {
          maxScore = score;
          maxScoredTerms = fixedAlphaBetaScore.getActiveTerms();
          if (fixedAlphaBetaScore != null) {
            maxScoredAlpha = fixedAlphaBetaScore.getAlpha();
            maxScoredBeta = fixedAlphaBetaScore.getBeta();
            maxScoredP = fixedAlphaBetaScore.getP();
          }
          maxWhenSeen = t;
        }

        long now = System.currentTimeMillis();
        if (now - start > updateReportTime) {
          logger.info((t * 100 / maxSteps) + "% (score=" + score + " maxScore=" + maxScore + " #terms=" + fixedAlphaBetaScore.getActiveTerms().length +
            " accept/reject=" + (double) numAccepts / (double) numRejects +
            " accept/steps=" + (double) numAccepts / (double) t +
            " exp=" + expectedNumberOfTerms + " usePrior=" + usePrior + ")");
          start = now;

        }

        long oldPossibilities = fixedAlphaBetaScore.getNeighborhoodSize();
        long r = rnd.nextLong();
        fixedAlphaBetaScore.proposeNewState(r);
        double newScore = fixedAlphaBetaScore.getScore();
        long newPossibilities = fixedAlphaBetaScore.getNeighborhoodSize();

        double acceptProb = Math.exp(newScore - score) * (double) oldPossibilities / (double) newPossibilities; /* last quotient is the hasting ratio */

        double u = rnd.nextDouble();
        if (u >= acceptProb) {
          fixedAlphaBetaScore.undoProposal();
          numRejects++;
        } else {
          score = newScore;
          numAccepts++;
        }
        //System.err.println("t="+t);
        if (t > burnin)
          fixedAlphaBetaScore.record();

      }


      if (i == maxIter - 1) {
        for (int t = 0; t < numTerms; t++) {
          res[t] = (double) fixedAlphaBetaScore.termActivationCounts[t] / fixedAlphaBetaScore.numRecords;
        }
      }

      if (fixedAlphaBetaScore != null) {
        if (Double.isNaN(alpha)) {
          for (int j = 0; j < fixedAlphaBetaScore.totalAlpha.length; j++)
            logger.info("alpha(" + fixedAlphaBetaScore.ALPHA[j] + ")=" + (double) fixedAlphaBetaScore.totalAlpha[j] / fixedAlphaBetaScore.numRecords);
        }

        if (Double.isNaN(beta)) {
          for (int j = 0; j < fixedAlphaBetaScore.totalBeta.length; j++)
            logger.info("beta(" + fixedAlphaBetaScore.BETA[j] + ")=" + (double) fixedAlphaBetaScore.totalBeta[j] / fixedAlphaBetaScore.numRecords);
        }

        if (Double.isNaN(expectedNumberOfTerms)) {
          for (int j = 0; j < fixedAlphaBetaScore.totalExp.length; j++)
            logger.info("exp(" + fixedAlphaBetaScore.EXPECTED_NUMBER_OF_TERMS[j] + ")=" + (double) fixedAlphaBetaScore.totalExp[j] / fixedAlphaBetaScore.numRecords);

        }
      }

      logger.info("numAccepts=" + numAccepts + "  numRejects = " + numRejects);

      if (logger.isInfoEnabled()) {
        StringBuilder b = new StringBuilder();

        logger.info("Term combination that reaches score of " + maxScore +
          " when alpha=" + maxScoredAlpha +
          ", beta=" + maxScoredBeta +
          ", p=" + maxScoredP +
          " at step " + maxWhenSeen);
        b.append("Indices: ");
        for (int t : maxScoredTerms) {
          b.append(t);
          b.append(", ");
        }
        logger.info(b.toString());
      }
    }
    return res;
  }

}
