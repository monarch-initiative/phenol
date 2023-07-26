package org.monarchinitiative.phenol.analysis.stats;


import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.analysis.DirectAndIndirectTermAnnotations;
import org.monarchinitiative.phenol.analysis.stats.mtc.MultipleTestingCorrection;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A specific term-for-term p-value calculation.
 *
 * @author Sebastian Bauer
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class TermForTermPValueCalculation extends PValueCalculation {
  Logger LOGGER = LoggerFactory.getLogger(TermForTermPValueCalculation.class);
  public TermForTermPValueCalculation(MinimalOntology graph,
                                      StudySet populationSet,
                                      StudySet studySet,
                                      MultipleTestingCorrection mtc) {
    super(graph, populationSet, studySet, mtc);
  }

  /**
   * Calculate the term-for-term p values for each ontology term being analyzed.
   * Put all terms with at least one annotated gene in the study set into the
   * list of results and discard other terms.
   * @return List of results with p values and study/population counts for each term.
   */
  public List<GoTerm2PValAndCounts> calculatePVals() {
    Map<TermId, DirectAndIndirectTermAnnotations> studySetAnnotationMap = this.studySet.getAnnotationMap();
    Map<TermId, DirectAndIndirectTermAnnotations> populationSetAnnotationMap = this.populationSet.getAnnotationMap();
    List<GoTerm2PValAndCounts> results = new ArrayList<>();

    int popGeneCount = populationSet.getAnnotatedItemCount();
    int studyGeneCount = studySet.getAnnotatedItemCount();
    for (Map.Entry<TermId, DirectAndIndirectTermAnnotations> entry : studySetAnnotationMap.entrySet()) {
      if (entry.getValue().totalAnnotatedCount() < 2) {
        continue; // only a single annotated entry -- do not perform a statistical test
      }
      TermId goId = entry.getKey();
      if (!this.populationSet.getAnnotationMap().containsKey(goId)) {
        LOGGER.error("Study set contains GO ID ({}) but pop set does not: ", goId.getValue());
      }
      int goidAnnotatedPopGeneCount = populationSet.getTotalAnnotationCount(goId);
      int goidAnnotatedStudyGeneCount = studySet.getTotalAnnotationCount(goId);
      if (goidAnnotatedStudyGeneCount != 0) {
        /* Imagine the following...
         *
         * In an urn you put popGeneCount number of balls where a color of a
         * ball can be white or black. The number of balls having white color
         * is goidAnnontatedPopGeneCount (all genes of the population which
         * are annotated by the current GOID).
         *
         * You choose to draw studyGeneCount number of balls without replacement.
         * How big is the probability, that you got goidAnnotatedStudyGeneCount
         * white balls after the whole drawing process?
         */
        double raw_pval = hyperg.phypergeometric(popGeneCount,
          (double) goidAnnotatedPopGeneCount / (double) popGeneCount,
          studyGeneCount,
          goidAnnotatedStudyGeneCount);
        GoTerm2PValAndCounts goPval = new GoTerm2PValAndCounts(goId, raw_pval, goidAnnotatedStudyGeneCount, studyGeneCount,
          goidAnnotatedPopGeneCount, popGeneCount);
        results.add(goPval);
      }
      // If desired we could record the SKIPPED TESTS (Terms) HERE
    }
    // Now do multiple testing correction
    this.testCorrection.adjustPvals(results);
    return results;
  }


}

