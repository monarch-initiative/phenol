package org.monarchinitiative.phenol.stats;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * Created on 06.07.2005
 * A superclass for multiple test correction...
 *
 * @author Sebastian Bauer
 * @author <a href="mailto:peter.robinson@jax.org>Peter Robinson</a>
 */
public abstract class AbstractTestCorrection
{
	/**
	 * Perform multiple test correction on p values originating
	 * from the given p value calculation.
	 *
	 * @param pValueCalculation an object implementing the p value 	calculation.
	 * @return the adjusted p values matching the order of the
	 *         the p values of the nominal p value calculation.
	 */
	public abstract Map<TermId, PValue> adjustPValues(IPValueCalculation pValueCalculation);

	/**
	 * Return the name of the test correction.
	 *
	 * @return the name of the test.
	 */
	public abstract String getName();

	/**
	 * Returns the number of pvalues that don't have
	 * the ignoreAtMTC attribute set.
	 *
	 * @param p
	 * @return number of relevant p values
	 */
//	protected int countRelevantPValues(PValue [] p)
//	{
//		int pvalsCount = 0;
//
//		/* Count number of p values which shouldn't be ignored */
//		for (int i=0;i<p.length;i++)
//			if (!p[i].ignoreAtMTC) pvalsCount++;
//
//		return pvalsCount;
//	}

	/**
	 * Returns an array of all raw p values that don't have
	 * the ignoreAtMTC attribute set.
	 *
	 * @param p array of p values
	 * @return array of p values that don't have ignoreAtMTC set
	 */
	protected PValue [] getRelevantRawPValues(PValue [] p)
	{
		int i;
		int j;
		int pvalsCount = 0;

		/* Count number of p values which shouldn't be ignored */
		for (i=0;i<p.length;i++)
			if (!p[i].ignoreAtMTC) pvalsCount++;

		/* Now put all relevant p values into a new array */
		PValue [] filteredP = new PValue[pvalsCount];
		for (i=0,j=0;i<p.length;i++)
			if (!p[i].ignoreAtMTC) filteredP[j++] = p[i];

		return filteredP;
	}

  /** @return non-ignored pvalues (sorted) */
  protected Pair [] getRelevantRawPValues(Map<TermId, PValue> pvalmap) {
    int pvalsCount = (int) pvalmap.values().stream().filter(PValue::doNotIgnore).count();
    Pair[] pairs = new Pair[pvalsCount];
    int i = 0;
    for (Map.Entry<TermId, PValue> entry : pvalmap.entrySet()) {
      if (entry.getValue().doNotIgnore()) {
        pairs[i].tid = entry.getKey();
        pairs[i].pval = entry.getValue();
        i++;
      }
    }
    Arrays.sort(pairs, Comparator.comparing(Pair::getPVal));
    return pairs;
  }


	/**
	 * Enforce monotony constraints of the p values (i.e. that
	 * adjusted p values of increasing p values is increasing
	 * as well)
	 *
	 * @param p specifies the p values array which has to be already
	 *        in sorted order!
	 */
	public static void enforcePValueMonotony(PValue [] p)
	{
		int m = p.length;

		/* Do nothing if there are not enough pvalues */
		if (m<2) return;

		p[m-1].p_adjusted = Math.min(p[m-1].p_adjusted,1);

		for (int i=m-2;i>=0;i--)
			p[i].p_adjusted = Math.min(p[i].p_adjusted,p[i+1].p_adjusted);
	}

  /**
   * Enforce monotony constraints of the p values (i.e. that
   * adjusted p values of increasing p values is increasing
   * as well)
   *
   * @param pairs specifies the p values array which has to be already
   *        in sorted order!
   */
  public static void enforcePValueMonotony(Pair [] pairs)
  {
    int m = pairs.length;

    /* Do nothing if there are not enough pvalues */
    if (m<2) return;

    pairs[m-1].pval.p_adjusted = Math.min(pairs[m-1].pval.p_adjusted,1);

    for (int i=m-2;i>=0;i--)
      pairs[i].pval.p_adjusted = Math.min(pairs[i].pval.p_adjusted,pairs[i+1].pval.p_adjusted);
  }

  /**
   * A convenience class to allow us to sort TermId, Pvalue pairs.
   */
  static class Pair {
    TermId tid;
    PValue pval;
    public PValue getPVal() { return pval; }
  }
}
