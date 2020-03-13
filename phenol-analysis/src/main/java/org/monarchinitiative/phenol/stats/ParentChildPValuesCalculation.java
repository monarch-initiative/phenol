package org.monarchinitiative.phenol.stats;


import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.DirectAndIndirectTermAnnotations;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.stats.mtc.MultipleTestingCorrection;

import java.util.*;

/**
 * This class hides all the details about how the p values are calculated
 * from the multiple test correction.
 *
 * @author Sebastian Bauer
 * @author Peter Robinson (refactor)
 */
abstract class ParentChildPValuesCalculation extends PValueCalculation {

  protected final Map<TermId, DirectAndIndirectTermAnnotations> studySetAnnotationMap;

  /**
   * Return value type for getCounts().
   */
  protected static class Counts {
    public final int m_pa_t;
    public final int n_pa_t;

    public Counts(int m, int n) {
      this.m_pa_t = m;
      this.n_pa_t = n;
    }
  }

  public ParentChildPValuesCalculation(Ontology graph,
                                       AssociationContainer goAssociations,
                                       StudySet populationSet,
                                       StudySet studySet,
                                       Hypergeometric hyperg,
                                       MultipleTestingCorrection mtc) {
    super(graph, goAssociations, populationSet, studySet, hyperg, mtc);
    this.studySetAnnotationMap = this.studySet.getAnnotationMap();
  }

  @Override
  public List<GoTerm2PValAndCounts> calculatePVals() {
    Map<TermId, DirectAndIndirectTermAnnotations> populationSetAnnotationMap = this.populationSet.getAnnotationMap();
    List<GoTerm2PValAndCounts> results = new ArrayList<>();
    for (Map.Entry<TermId, DirectAndIndirectTermAnnotations> entry : studySetAnnotationMap.entrySet()) {
      if (entry.getValue().totalAnnotatedCount() < 2) {
        continue; // only a single annotated entry -- do not perform a statistical test
      }
      TermId goId = entry.getKey();
      if (!this.annotationMap.containsKey(goId)) {
        System.err.println("ERROR -- study set contains ID but pop set does not: " + goId.getValue());
        continue;
      }
      int goidAnnotatedPopGeneCount = populationSetAnnotationMap.get(goId).totalAnnotatedCount();
      int goidAnnotatedStudyGeneCount = studySetAnnotationMap.get(goId).totalAnnotatedCount();
      if (goidAnnotatedStudyGeneCount != 0) {
        /*
         * The parent child calculation is a one-sided Fisher exact test where the population is defined based not
         * on the entire set of genes in an experiment but instead on the set of genes that annotate the parent(s) of
         * the GO term of interest (t). If we denote pa(t) as the parent term(s) of t, then pa(t) will have some
         * direct annotations to pa(t) and will also include annotations from distinct children of pa(t). It is possible
         * that t has more than one parent term. In this case, parent-child union takes the set of all genes that
         * annotate any term in pa(t), and parent-child intersect takes the set of genes that are annotated to all of
         * the terms in pa(t). The latter tends to be more conservative.
         * The study gene count is identical as with term for term, but we need to substitution the counts for the population
         */

        // get parents of current GO term. Do not include original term in this set
        Set<TermId> parents = OntologyAlgorithm.getParentTerms(ontology, goId, false);
        // methodology for Term for Term was like this:
        //        double raw_pval = hyperg.phypergeometric(popGeneCount,
        //          (double) goidAnnotatedPopGeneCount / (double) popGeneCount,
        //          studyGeneCount,
        //          goidAnnotatedStudyGeneCount);
        double raw_pval = 1;
        if (parents.size() == 1) {
          // in this case, PC union and PC intersect are identical
          // get m_pa(t), the number of genes annotated to the parent of t in the population
          TermId pa_t_id = parents.iterator().next(); // get the first and only element of the set
          int m_pa_t = populationSetAnnotationMap.get(pa_t_id).totalAnnotatedCount();
          // get n_pa(t), the number of genes annotation to pa(t) in the study set
          int n_pa_t = studySetAnnotationMap.get(pa_t_id).totalAnnotatedCount();
          double proportion = (double) goidAnnotatedPopGeneCount / (double) m_pa_t;
          raw_pval = hyperg.phypergeometric(m_pa_t,
            proportion,
            n_pa_t,
            goidAnnotatedStudyGeneCount);
        } else {
          Counts count = getCounts(goId, parents);
          int m_pa_t = count.m_pa_t;
          int n_pa_t = count.n_pa_t;
          raw_pval = hyperg.phypergeometric(m_pa_t,
            (double) goidAnnotatedPopGeneCount / (double) m_pa_t,
            n_pa_t,
            goidAnnotatedStudyGeneCount);
        }

        GoTerm2PValAndCounts goPval = new GoTerm2PValAndCounts(goId, raw_pval, goidAnnotatedStudyGeneCount, goidAnnotatedPopGeneCount);
        results.add(goPval);
      }
    }
    // Now do multiple testing correction
    this.testCorrection.adjustPvals(results);
    return results;
  }

  /**
   * Calculate the counts for the given study set ids for the term.
   * Implemented by the PC Union/Intersection subclasses.
   *
   * @return the count structure.
   */
  protected abstract Counts getCounts(TermId goId, Set<TermId> parents);


}
