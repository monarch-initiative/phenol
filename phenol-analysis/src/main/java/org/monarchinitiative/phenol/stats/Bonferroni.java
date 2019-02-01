/*
 * Created on 06.07.2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.monarchinitiative.phenol.stats;

import java.util.List;

/**
 * This class implements the Bonferroni multiple test correction which is the
 * most conservative approach.
 *
 * @author Sebastian Bauer
 */
public class Bonferroni<T> implements MultipleTestingCorrection<T>
{
	/** The name of the correction method */
	private static final String NAME = "Bonferroni";

  @Override
  public void adjustPvals(List<Item2PValue<T>> pvals) {
    int N=pvals.size();
    for (Item2PValue<T> item : pvals) {
      double p_raw= item.getRawPValue();
      item.setAdjustedPValue(Math.min(1.0,N*p_raw));
    }
  }

  @Override
  public void adjustSortedPvals(List<Item2PValue<T>> pvals) {
    adjustPvals(pvals); // no difference
  }




	public String getName()
	{
		return NAME;
	}
}
