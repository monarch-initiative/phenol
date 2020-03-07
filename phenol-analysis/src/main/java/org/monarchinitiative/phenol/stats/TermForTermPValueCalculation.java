package org.monarchinitiative.phenol.stats;


import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.analysis.DirectAndIndirectTermAnnotations;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;


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
  public TermForTermPValueCalculation(Ontology graph,
                                      AssociationContainer goAssociations,
                                      StudySet populationSet,
                                      StudySet studySet,
                                      Hypergeometric hyperg,
                                      MultipleTestingCorrection mtc) {
    super(graph, goAssociations, populationSet, studySet, hyperg, mtc);
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
      if (!this.annotationMap.containsKey(goId)) {
        System.err.println("ERROR -- study set contains ID but pop set does not: " + goId.getValue());
      }
     // int goidAnnotatedPopGeneCount = this.annotationMap.get(goId).totalAnnotatedCount();
      int goidAnnotatedPopGeneCount = populationSetAnnotationMap.get(goId).totalAnnotatedCount();
      int goidAnnotatedStudyGeneCount = studySetAnnotationMap.get(goId).totalAnnotatedCount();
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
        GoTerm2PValAndCounts goPval = new GoTerm2PValAndCounts(goId, raw_pval, goidAnnotatedStudyGeneCount, goidAnnotatedPopGeneCount);
        results.add(goPval);
      }
      // If desired we could record the SKIPPED TESTS (Terms) HERE
    }
    // Now do multiple testing correction
    this.testCorrection.adjustPvals(results);
    return results;
  }


}

