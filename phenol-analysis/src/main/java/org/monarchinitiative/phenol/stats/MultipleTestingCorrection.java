package org.monarchinitiative.phenol.stats;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created on 06.07.2005
 * A superclass for multiple test correction...
 *
 * @author Sebastian Bauer
 * @author <a href="mailto:peter.robinson@jax.org>Peter Robinson</a>
 */
public interface MultipleTestingCorrection<T>
{
	/**
	 * Perform multiple test correction on p values originating
	 * from the given p value calculation.
	 *
	 * @param pvals an object implementing the p value 	calculation.
	 * @return the adjusted p values matching the order of the
	 *         the p values of the nominal p value calculation.
	 */
  public void adjustPvals(List<Item2PValue<T>> pvals);

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
  default void enforcePValueMonotony(List<Item2PValue<T> > pairs)
  {
    int m = pairs.size();

    /* Do nothing if there are not enough pvalues */
    if (m<2) return;

    pairs.get(m-1).setAdjustedPValue(Math.min(pairs.get(m-1).getAdjustedPValue(),1));

    for (int i=m-2;i>=0;i--)
      pairs.get(i).setAdjustedPValue( Math.min(pairs.get(i).getAdjustedPValue(),pairs.get(i+1).getAdjustedPValue()));
  }


}
