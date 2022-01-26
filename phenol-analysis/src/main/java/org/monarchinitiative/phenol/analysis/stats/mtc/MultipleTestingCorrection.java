package org.monarchinitiative.phenol.analysis.stats.mtc;

import org.monarchinitiative.phenol.analysis.stats.PValue;

import java.util.List;

/**
 * Created on 06.07.2005
 * A superclass for multiple test correction...
 *
 * @author Sebastian Bauer
 * @author <a href="mailto:peter.robinson@jax.org>Peter Robinson</a>
 */
public interface MultipleTestingCorrection
{
	/**
	 * Perform multiple test correction on p values originating
	 * from the given p value calculation. The p values are adjusted in place
	 *
	 * @param pvals an object implementing the p value 	calculation.
	 */
  void adjustPvals(List<? extends PValue> pvals);


	/**
	 * Return the name of the test correction.
	 *
	 * @return the name of the test.
	 */
	String getName();

  /**
   * Enforce monotony constraints of the p values (i.e. that
   * adjusted p values of increasing p values is increasing
   * as well)
   *
   * @param pairs specifies the p values array which has to be already
   *        in sorted order!
   */
  default void enforcePValueMonotony(List<? extends PValue> pairs)
  {
    int m = pairs.size();

    /* Do nothing if there are not enough pvalues */
    if (m<2) return;

    pairs.get(m-1).setAdjustedPValue(Math.min(pairs.get(m-1).getAdjustedPValue(),1));

    for (int i=m-2;i>=0;i--)
      pairs.get(i).setAdjustedPValue( Math.min(pairs.get(i).getAdjustedPValue(),pairs.get(i+1).getAdjustedPValue()));
  }


}
