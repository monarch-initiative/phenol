package org.monarchinitiative.phenol.analysis.mgsa;

import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.DirectAndIndirectTermAnnotations;
import org.monarchinitiative.phenol.analysis.PopulationSet;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;


/**
 * This class implements a model-based analysis. The description of the entire
 * method can be found in "GOing Bayesian: model-based gene set analysis of genome-scale data"
 *
 * @author Sebastian Bauer, modifications by Peter Robinson
 * @see <A HREF="http://nar.oxfordjournals.org/content/early/2010/02/19/nar.gkq045.short">GOing Bayesian: model-based gene set analysis of genome-scale data</A>
 */
public class MgsaCalculation {
  private static Logger logger = Logger.getLogger(MgsaCalculation.class.getName());

  private long seed = 0;

  private boolean usePrior = true;

  private boolean integrateParams = false;

  private DoubleParam alpha = new DoubleParam(MgsaParam.Type.MCMC);
  private DoubleParam beta = new DoubleParam(MgsaParam.Type.MCMC);
  private IntegerParam expectedNumberOfTerms = new IntegerParam(MgsaParam.Type.MCMC);

  private boolean takePopulationAsReference = false;
  private boolean randomStart = false;
  private final static int DEFAULT_MCMCSTEPS = 250_000;
  private final int mcmcSteps;
  private final int burnin = 20000;
  private int updateReportTime = 1000; /* Update report time in ms */

  private final AssociationContainer goAssociations;

  private TermToItemMatrix termToItemMatrix;
  /**
   * Reference to ontology (usually Gene Ontology).
   */
  private Ontology ontology;

  private final StudySet populationSet;


  /**
   * If this constructor is used, then all annotated genes are taken to be the population set.
   *
   * @param ontology       reference to Gene Ontology
   * @param goAssociations reference to contained with GO <-> gene associations
   * @param mcmcSteps      Number of iterations of MCMC to perform
   */
  public MgsaCalculation(Ontology ontology,
                         AssociationContainer goAssociations,
                         int mcmcSteps) {
    this.ontology = ontology;
    this.goAssociations = goAssociations;
    this.mcmcSteps = mcmcSteps;
    Objects.requireNonNull(goAssociations);
    System.err.println("MgsaCalculation, mcsc steps " + mcmcSteps);

    System.err.println("About to init termToItemMatrixs ");
    this.termToItemMatrix = new TermToItemMatrix(goAssociations);

    Set<TermId> allAnnotatedGenes = goAssociations.getAllAnnotatedGenes();
    Map<TermId, DirectAndIndirectTermAnnotations> assocs = goAssociations.getAssociationMap(allAnnotatedGenes, ontology);
    this.populationSet = new PopulationSet(goAssociations.getAllAnnotatedGenes(), assocs);
  }

  /**
   * @return the total count of genes (items) in the population set.
   */
  public int getPopulationSetCount() {
    return populationSet.getAnnotatedItemCount();
  }

  /**
   * Sets the seed of the random calculation.
   *
   * @param seed random number seed
   */
  public void setSeed(long seed) {
    this.seed = seed;
  }

  /**
   * Sets a fixed value for the alpha parameter.
   *
   * @param alpha
   */
  public void setAlpha(double alpha) {
    if (alpha < 0.000001) alpha = 0.000001;
    if (alpha > 0.999999) alpha = 0.999999;
    this.alpha.setValue(alpha);
  }

  /**
   * Sets a fixed value for the beta parameter.
   *
   * @param beta
   */
  public void setBeta(double beta) {
    if (beta < 0.000001) beta = 0.000001;
    if (beta > 0.999999) beta = 0.999999;
    this.beta.setValue(beta);
  }

  /**
   * Sets the type of the alpha parameter.
   *
   * @param alpha
   */
  public void setAlpha(MgsaParam.Type alpha) {
    this.alpha.setType(alpha);
  }

  /**
   * Sets the bounds of the alpha parameter.
   *
   * @param min
   * @param max
   */
  public void setAlphaBounds(double min, double max) {
    this.alpha.setMin(min);
    this.alpha.setMax(max);
  }

  /**
   * Sets the type of the beta parameter.
   *
   * @param beta
   */
  public void setBeta(MgsaParam.Type beta) {
    this.beta.setType(beta);
  }

  /**
   * Sets the bounds of the beta parameter.
   *
   * @param min
   * @param max
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
   * Set whether the parameter should be integrated.
   *
   * @param integrateParams
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
   * Sets whether a random start should be used.
   *
   * @param randomStart
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

  public MgsaEnrichedGOTermsResult calculateStudySet(StudySet studySet) {
    MgsaEnrichedGOTermsResult result = new MgsaEnrichedGOTermsResult(ontology,
      goAssociations,
      studySet,
      getPopulationSetCount());
    if (studySet.getAnnotatedItemCount() == 0) {
      System.err.println("[WARNING] Study set empty! Returning specious result");
      return result;
    }

    //  TermEnumerator populationEnumerator = populationSet.enumerateTerms(graph, goAssociations);
    // TermEnumerator studyEnumerator = studySet.enumerateTerms(graph, goAssociations);

    logger.log(INFO, "Starting calculation: expectedNumberOfTerms=" + expectedNumberOfTerms +
      " alpha=" + alpha +
      " beta=" + beta +
      " numberOfPop=" + getPopulationSetCount() +
      " numberOfStudy=" + studySet.getAnnotatedItemCount());

    long start = System.currentTimeMillis();
    calculateByMCMC(result, studySet);//, llr);
    long end = System.currentTimeMillis();
    logger.log(INFO, (end - start) + "ms");
    return result;
  }


  public void setUsePrior(boolean usePrior) {
    this.usePrior = usePrior;
  }

  private void calculateByMCMC(MgsaEnrichedGOTermsResult result,
                               StudySet studySet) {
    List<TermId> relevantTermList = new ArrayList<>(populationSet.getOntologyTermIds());// graph.filterRelevant(populationEnumerator.getAllAnnotatedTermsAsList());
    //IntMapper<TermId> termMapper = IntMapper.create(relevantTermList);
    // IntMapper<ByteString> geneMapper = IntMapper.create(populationEnumerator.getGenesAsList());
    if (goAssociations == null) {
      throw new PhenolRuntimeException("GOASS NULL");
    }
    TermToItemMatrix calcUtils = new TermToItemMatrix(goAssociations);
    int[][] termLinks = calcUtils.getTermLinks();
    boolean[] observedItems = calcUtils.getBooleanArrayobservedItems(studySet.getGeneSet()); //geneMapper.getDense(studyEnumerator.getGenes());
    double[] r = calculate(termLinks, observedItems);

    for (int i = 0; i < r.length; i++) {
      TermId tid = calcUtils.getGoTermAtIndex(i);
      MgsaGOTermProperties prop = new MgsaGOTermProperties();
      prop.term = tid;
      prop.annotatedStudyGenes = calcUtils.getAnnotatedGeneCount(tid);
      // studyEnumerator.getAnnotatedGenes(tid).totalAnnotatedCount();
      prop.annotatedPopulationGenes = populationSet.getAnnotatedItemCount();
      //populationEnumerator.getAnnotatedGenes(tid).totalAnnotatedCount();
      prop.marg = r[i];
      result.addGOTermProperties(prop);
    }

  }


  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getName() {
    return "MGSA";
  }


  public boolean supportsTestCorrection() {
    return false;
  }


  private MgsaGOTermProperties[] calculate2(StudySet studySet) {
    int[][] term2Items = termToItemMatrix.getTermLinks();
    boolean[] observedItems = this.termToItemMatrix.getBooleanArrayobservedItems(studySet.getGeneSet()); //geneMapper.getDense(studyEnumerator.getGenes());
    int numTerms = termToItemMatrix.getNumTerms();
    MgsaGOTermProperties[] res = new MgsaGOTermProperties[numTerms];
    Random rnd;
    if (seed != 0) {
      rnd = new Random(seed);
      logger.log(INFO, "Using random seed of: " + seed);
    } else {
      long newSeed = new Random().nextLong();
      logger.log(INFO, "Using random seed of: " + newSeed);
      rnd = new Random(newSeed);
    }
    boolean doAlphaEm = false;
    boolean doBetaEm = false;
    boolean doPEm = false;

    int maxIter;

    double alpha;
    double beta;
    double expectedNumberOfTerms;
    alpha = Double.NaN;
    beta = Double.NaN;
    expectedNumberOfTerms = Double.NaN;

    FixedAlphaBetaScore fixedAlphaBetaScore = new FixedAlphaBetaScore(rnd, term2Items, observedItems);
    fixedAlphaBetaScore.setIntegrateParams(integrateParams);
    logger.log(INFO, "MCMC only: " + alpha + "  " + beta + "  " + expectedNumberOfTerms);
    fixedAlphaBetaScore.setAlpha(alpha);
    if (this.alpha.hasMax())
      fixedAlphaBetaScore.setMaxAlpha(this.alpha.getMax());
    fixedAlphaBetaScore.setBeta(beta);
    if (this.beta.hasMax())
      fixedAlphaBetaScore.setMaxBeta(this.beta.getMax());
    fixedAlphaBetaScore.setExpectedNumberOfTerms(expectedNumberOfTerms);
    fixedAlphaBetaScore.setUsePrior(usePrior);

    logger.log(INFO, "Score of empty set: " + fixedAlphaBetaScore.getScore());

    /* Provide a starting point */
    if (randomStart) {
      int numberOfTerms = fixedAlphaBetaScore.EXPECTED_NUMBER_OF_TERMS[rnd.nextInt(fixedAlphaBetaScore.EXPECTED_NUMBER_OF_TERMS.length)];
      double pForStart = ((double) numberOfTerms) / term2Items.length;

      for (int j = 0; j < term2Items.length; j++)
        if (rnd.nextDouble() < pForStart) fixedAlphaBetaScore.switchState(j);

      logger.log(INFO, "Starting with " + fixedAlphaBetaScore.getActiveTerms().length + " terms (p=" + pForStart + ")");
    }

    double score = fixedAlphaBetaScore.getScore();
    logger.log(INFO, "Score of initial set: " + score);

    int maxSteps = mcmcSteps;

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
        logger.log(INFO, (t * 100 / maxSteps) + "% (score=" + score + " maxScore=" + maxScore + " #terms=" + fixedAlphaBetaScore.getActiveTerms().length +
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


    for (int t = 0; t < numTerms; t++) {
      TermId tid = termToItemMatrix.getGoTermAtIndex(t);
      MgsaGOTermProperties prop = new MgsaGOTermProperties();
      prop.term = tid;
      prop.annotatedStudyGenes = termToItemMatrix.getAnnotatedGeneCount(tid);
      // studyEnumerator.getAnnotatedGenes(tid).totalAnnotatedCount();
      prop.annotatedPopulationGenes = getPopulationSetCount();
      //populationEnumerator.getAnnotatedGenes(tid).totalAnnotatedCount();
      prop.marg = (double) fixedAlphaBetaScore.termActivationCounts[t] / fixedAlphaBetaScore.numRecords;
    }


    if (fixedAlphaBetaScore != null) {
      if (Double.isNaN(alpha)) {
        for (int j = 0; j < fixedAlphaBetaScore.totalAlpha.length; j++)
          logger.log(INFO, "alpha(" + fixedAlphaBetaScore.ALPHA[j] + ")=" + (double) fixedAlphaBetaScore.totalAlpha[j] / fixedAlphaBetaScore.numRecords);
      }

      if (Double.isNaN(beta)) {
        for (int j = 0; j < fixedAlphaBetaScore.totalBeta.length; j++)
          logger.log(INFO, "beta(" + fixedAlphaBetaScore.BETA[j] + ")=" + (double) fixedAlphaBetaScore.totalBeta[j] / fixedAlphaBetaScore.numRecords);
      }

      if (Double.isNaN(expectedNumberOfTerms)) {
        for (int j = 0; j < fixedAlphaBetaScore.totalExp.length; j++)
          logger.log(INFO, "exp(" + fixedAlphaBetaScore.EXPECTED_NUMBER_OF_TERMS[j] + ")=" + (double) fixedAlphaBetaScore.totalExp[j] / fixedAlphaBetaScore.numRecords);

      }
    }

    logger.log(INFO, "numAccepts=" + numAccepts + "  numRejects = " + numRejects);

    if (logger.isLoggable(INFO)) {
      StringBuilder b = new StringBuilder();

      logger.log(INFO, "Term combination that reaches score of " + maxScore +
        " when alpha=" + maxScoredAlpha +
        ", beta=" + maxScoredBeta +
        ", p=" + maxScoredP +
        " at step " + maxWhenSeen);
      b.append("Indices: ");
      for (int t : maxScoredTerms) {
        b.append(t);
        b.append(", ");
      }
      logger.log(INFO, b.toString());
    }

    return res;


  }

  /**
   * Perform the calculation.
   *
   * @param term2Items
   * @param observedItems
   * @return a vector of marginal probabilities for each term.
   */
  private double[] calculate(int[][] term2Items, boolean[] observedItems) {
    int numTerms = term2Items.length;
    double[] res = new double[numTerms];

    Random rnd;
    if (seed != 0) {
      rnd = new Random(seed);
      logger.log(INFO, "Using random seed of: " + seed);
    } else {
      long newSeed = new Random().nextLong();
      logger.log(INFO, "Using random seed of: " + newSeed);
      rnd = new Random(newSeed);
    }

    boolean doAlphaEm = false;
    boolean doBetaEm = false;
    boolean doPEm = false;

    int maxIter;

    double alpha;
    double beta;
    double expectedNumberOfTerms;

    switch (this.alpha.getType()) {
      case EM:
        alpha = 0.4;
        doAlphaEm = true;
        break;
      case MCMC:
        alpha = Double.NaN;
        break;
      default:
        alpha = this.alpha.getValue();
        break;
    }

    switch (this.beta.getType()) {
      case EM:
        beta = 0.4;
        doBetaEm = true;
        break;
      case MCMC:
        beta = Double.NaN;
        break;
      default:
        beta = this.beta.getValue();
        break;
    }


    switch (this.expectedNumberOfTerms.getType()) {
      case EM:
        expectedNumberOfTerms = 1;
        doPEm = true;
        break;
      case MCMC:
        expectedNumberOfTerms = Double.NaN;
        break;
      default:
        expectedNumberOfTerms = this.expectedNumberOfTerms.getValue();
        break;
    }

    boolean doEm = doAlphaEm || doBetaEm || doPEm;

    if (doEm) maxIter = 12;
    else maxIter = 1;

    for (int i = 0; i < maxIter; i++) {
      FixedAlphaBetaScore fixedAlphaBetaScore = new FixedAlphaBetaScore(rnd, term2Items, observedItems);
      fixedAlphaBetaScore.setIntegrateParams(integrateParams);

      if (doEm) {
        logger.log(INFO, "EM-Iter(" + i + ")" + alpha + "  " + beta + "  " + expectedNumberOfTerms);
      } else {
        logger.log(INFO, "MCMC only: " + alpha + "  " + beta + "  " + expectedNumberOfTerms);
      }

      fixedAlphaBetaScore.setAlpha(alpha);
      if (this.alpha.hasMax())
        fixedAlphaBetaScore.setMaxAlpha(this.alpha.getMax());
      fixedAlphaBetaScore.setBeta(beta);
      if (this.beta.hasMax())
        fixedAlphaBetaScore.setMaxBeta(this.beta.getMax());
      fixedAlphaBetaScore.setExpectedNumberOfTerms(expectedNumberOfTerms);
      fixedAlphaBetaScore.setUsePrior(usePrior);

      logger.log(INFO, "Score of empty set: " + fixedAlphaBetaScore.getScore());

      /* Provide a starting point */
      if (randomStart) {
        int numberOfTerms = fixedAlphaBetaScore.EXPECTED_NUMBER_OF_TERMS[rnd.nextInt(fixedAlphaBetaScore.EXPECTED_NUMBER_OF_TERMS.length)];
        double pForStart = ((double) numberOfTerms) / term2Items.length;

        for (int j = 0; j < term2Items.length; j++)
          if (rnd.nextDouble() < pForStart) fixedAlphaBetaScore.switchState(j);

        logger.log(INFO, "Starting with " + fixedAlphaBetaScore.getActiveTerms().length + " terms (p=" + pForStart + ")");
      }

      double score = fixedAlphaBetaScore.getScore();
      logger.log(INFO, "Score of initial set: " + score);

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
          logger.log(INFO, (t * 100 / maxSteps) + "% (score=" + score + " maxScore=" + maxScore + " #terms=" + fixedAlphaBetaScore.getActiveTerms().length +
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

      if (fixedAlphaBetaScore != null) {
        if (doAlphaEm) {
          double newAlpha = fixedAlphaBetaScore.getAvgN10() / (fixedAlphaBetaScore.getAvgN00() + fixedAlphaBetaScore.getAvgN10());
          if (newAlpha < 0.0000001) newAlpha = 0.0000001;
          if (newAlpha > 0.9999999) newAlpha = 0.9999999;
          logger.log(INFO, "alpha=" + alpha + "  newAlpha=" + newAlpha);
          alpha = newAlpha;
        }

        if (doBetaEm) {
          double newBeta = fixedAlphaBetaScore.getAvgN01() / (fixedAlphaBetaScore.getAvgN01() + fixedAlphaBetaScore.getAvgN11());
          if (newBeta < 0.0000001) newBeta = 0.0000001;
          if (newBeta > 0.9999999) newBeta = 0.9999999;
          logger.log(INFO, "beta=" + beta + "  newBeta=" + newBeta);
          beta = newBeta;
        }

        if (doPEm) {
          double newExpectedNumberOfTerms = fixedAlphaBetaScore.getAvgT();
          if (newExpectedNumberOfTerms < 0.0000001) newExpectedNumberOfTerms = 0.0000001;
          logger.log(INFO, "expectedNumberOfTerms=" + expectedNumberOfTerms + "  newExpectedNumberOfTerms=" + newExpectedNumberOfTerms);
          expectedNumberOfTerms = newExpectedNumberOfTerms;
        }
      }

      if (i == maxIter - 1) {
        for (int t = 0; t < numTerms; t++) {
          res[t] = (double) fixedAlphaBetaScore.termActivationCounts[t] / fixedAlphaBetaScore.numRecords;
        }
      }

      if (fixedAlphaBetaScore != null) {
        if (Double.isNaN(alpha)) {
          for (int j = 0; j < fixedAlphaBetaScore.totalAlpha.length; j++)
            logger.log(INFO, "alpha(" + fixedAlphaBetaScore.ALPHA[j] + ")=" + (double) fixedAlphaBetaScore.totalAlpha[j] / fixedAlphaBetaScore.numRecords);
        }

        if (Double.isNaN(beta)) {
          for (int j = 0; j < fixedAlphaBetaScore.totalBeta.length; j++)
            logger.log(INFO, "beta(" + fixedAlphaBetaScore.BETA[j] + ")=" + (double) fixedAlphaBetaScore.totalBeta[j] / fixedAlphaBetaScore.numRecords);
        }

        if (Double.isNaN(expectedNumberOfTerms)) {
          for (int j = 0; j < fixedAlphaBetaScore.totalExp.length; j++)
            logger.log(INFO, "exp(" + fixedAlphaBetaScore.EXPECTED_NUMBER_OF_TERMS[j] + ")=" + (double) fixedAlphaBetaScore.totalExp[j] / fixedAlphaBetaScore.numRecords);

        }
      }

      logger.log(INFO, "numAccepts=" + numAccepts + "  numRejects = " + numRejects);

      if (logger.isLoggable(INFO)) {
        StringBuilder b = new StringBuilder();

        logger.log(INFO, "Term combination that reaches score of " + maxScore +
          " when alpha=" + maxScoredAlpha +
          ", beta=" + maxScoredBeta +
          ", p=" + maxScoredP +
          " at step " + maxWhenSeen);
        b.append("Indices: ");
        for (int t : maxScoredTerms) {
          b.append(t);
          b.append(", ");
        }
        logger.log(INFO, b.toString());
      }
    }
    return res;

  }

  public double[] calculate(int[][] term2Items, int[] studyIds, int numItems) {
    boolean[] observedItems = new boolean[numItems];
    for (int j : studyIds) {
      observedItems[j] = true;
    }
    //for (int i = 0; i < studyIds.length; i++)
    //    observedItems[studyIds[i]] = true;
    return calculate(term2Items, observedItems);
  }
}
