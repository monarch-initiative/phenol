package org.monarchinitiative.phenol.stats;


import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.DirectAndIndirectTermAnnotations;
import org.monarchinitiative.phenol.analysis.PopulationSet;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.stats.mtc.MultipleTestingCorrection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * This class hides all the details about how the p values are calculated
 * from the multiple test correction.
 *
 * @author Sebastian Bauer
 * @author Peter Robinson (refactor)
 *
 */
abstract class ParentChildPValuesCalculation extends PValueCalculation
{
	//protected SlimDirectedGraphView<Term> slimGraph;

	/**
	 * Return value type for getCounts().
	 *
	 * @author Sebastian Bauer
	 */
	protected static class Counts
	{
		public final int parents;
		public final int studyFamilyCount;
		public final int popFamilyCount;

		public Counts(int parents, int studyFamilyCount, int popFamilyCount)
		{
			this.parents = parents;
			this.studyFamilyCount = studyFamilyCount;
			this.popFamilyCount = popFamilyCount;
		}
	}

	public ParentChildPValuesCalculation(Ontology graph,
                                       AssociationContainer goAssociations, PopulationSet populationSet,
                                       StudySet studySet, Hypergeometric hyperg, MultipleTestingCorrection mtc)
	{
		super(graph, goAssociations, populationSet, studySet, hyperg, mtc);

		//slimGraph = graph.getSlimGraphView();
	}

	public List<GoTerm2PValAndCounts> calculatePValues(StudySet studySet)
  {
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
	/*{
		//int[] studyIds = getUniqueIDs(studySet);

		//PValue p [] = new PValue[getTotalNumberOfAnnotatedTerms()];

		for (int i = 0; i < termIds.length; i++)
		{
			if (progress != null && (i % 256) == 0)
			{
				progress.update(i);
			}

			p[i] = calculateTerm(studyIds, i);
		}

		return p;
	} */

	/*
	private ParentChildGOTermProperties calculateTerm(int [] studyIds, int termIndex)
	{
		TermID termId = termIds[termIndex];
		// counts annotated to term
		int studyTermCount = Util.commonInts(studyIds, term2Items[termIndex]);
		int popTermCount = term2Items[termIndex].length;

		// this is what we give back
		ParentChildGOTermProperties prop = new ParentChildGOTermProperties();
		prop.term = termId;
		prop.annotatedPopulationGenes = popTermCount;
		prop.annotatedStudyGenes = studyTermCount;

		if (graph.isRootTerm(termId))
		{
			prop.nparents = 0;
			prop.ignoreAtMTC = true;
			prop.p = 1.0;
			prop.p_adjusted = 1.0;
			prop.p_min = 1.0;
		} else
		{
			Counts counts = getCounts(studyIds, graph.getTerm(termId));

			int studyFamilyCount = counts.studyFamilyCount;
			int popFamilyCount = counts.popFamilyCount;

			prop.popFamilyGenes = popFamilyCount;
			prop.studyFamilyGenes = studyFamilyCount;
			prop.nparents = counts.parents;

			if (studyTermCount != 0)
			{
				if (popFamilyCount == popTermCount)
				{
					prop.ignoreAtMTC = true;
					prop.p = 1.0;
					prop.p_adjusted = 1.0;
					prop.p_min = 1.0;
				} else
				{
					double p = hyperg.phypergeometric(
							popFamilyCount,
							(double)popTermCount / (double)popFamilyCount,
							studyFamilyCount,
							studyTermCount);

					prop.ignoreAtMTC = false;
					prop.p = p;
					prop.p_min = hyperg.dhyper(
							popTermCount,
							popFamilyCount,
							popTermCount,
							popTermCount);
				}
			} else
			{
				prop.ignoreAtMTC = true;
				prop.p = 1.0;
				prop.p_adjusted = 1.0;
				prop.p_min = 1.0;
			}
		}

		return prop;
	}
	 */

	/**
	 * Calculate the counts for the given study set ids for the term.
	 *
	 * @param studyIds the study sets
	 * @param term the term for which the counts shall be determined.
	 * @return the count structure.
	 */
	protected abstract Counts getCounts(int[] studyIds, TermId term);

  /**
   * Determine the number of integer values that are common in the given sorted arrays.
   *
   * @param a sorted arrays
   * @return number of ints that are common.
   */
  public static int commonInts(int [] ... a)
  {
    if (a.length == 0) return 0;
    if (a.length == 1) return a[0].length;
    if (a.length == 2) return commonIntsWithResult(a[0], a[1], null);

    /* Slice array */
    int [][] newA = Arrays.copyOfRange(a, 1, a.length);
    int [] tmp = new int[Math.max(a[0].length, a[1].length)];
    int tmpLen = commonIntsWithResult(a[0], a[1], tmp);
    newA[0] = Arrays.copyOf(tmp, tmpLen);
    return commonInts(newA);
  }

  /**
   * Intersection of a and b with result in c.
   * @param a sorted array number one
   * @param b sorted array number two
   * @param c destination array or null.
   * @return number of valid entries of c (number of common elements).
   */
  private static int commonIntsWithResult(int [] a, int [] b, int c[])
  {
    int numCommon = 0;
    for (int i = 0, j = 0; i < a.length && j < b.length;)
    {
      if (a[i] > b[j])
      {
        j++;
      } else if (a[i] < b[j])
      {
        i++;
      } else
      {
        if (c != null)
        {
          c[numCommon] = a[i];
        }
        i++;
        j++;
        numCommon++;
      }
    }
    return numCommon;
  }

}
