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



  /** @return non-ignored pvalues (sorted) */
  Pair [] getRelevantRawPValues(Map<TermId, PValue> pvalmap) {
    int pvalsCount = (int) pvalmap.values().stream().filter(PValue::doNotIgnore).count();
    Pair[] pairs = new Pair[pvalsCount];
    int i = 0;
    for (Map.Entry<TermId, PValue> entry : pvalmap.entrySet()) {
      if (entry.getValue().doNotIgnore()) {
        pairs[i] = new Pair();
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

		p[m-1].setAdjustedPValue( Math.min(p[m-1].getAdjustedPValue(),1));

		for (int i=m-2;i>=0;i--)
			p[i].setAdjustedPValue( Math.min(p[i].getAdjustedPValue(),p[i+1].getAdjustedPValue()));
	}

  /**
   * Enforce monotony constraints of the p values (i.e. that
   * adjusted p values of increasing p values is increasing
   * as well)
   *
   * @param pairs specifies the p values array which has to be already
   *        in sorted order!
   */
  static void enforcePValueMonotony(Pair [] pairs)
  {
    int m = pairs.length;

    /* Do nothing if there are not enough pvalues */
    if (m<2) return;

    pairs[m-1].pval.setAdjustedPValue(Math.min(pairs[m-1].pval.getAdjustedPValue(),1));

    for (int i=m-2;i>=0;i--)
      pairs[i].pval.setAdjustedPValue( Math.min(pairs[i].pval.getAdjustedPValue(),pairs[i+1].pval.getAdjustedPValue()));
  }

  /**
   * A convenience class to allow us to sort TermId, Pvalue pairs.
   */
  static class Pair {
    TermId tid;
    PValue pval;
    PValue getPVal() { return pval; }
  }
}
