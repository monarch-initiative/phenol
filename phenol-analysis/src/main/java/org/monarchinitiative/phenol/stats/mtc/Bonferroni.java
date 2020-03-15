/*
 * Created on 06.07.2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.monarchinitiative.phenol.stats.mtc;

import org.monarchinitiative.phenol.stats.PValue;

import java.util.List;

/**
 * This class implements the Bonferroni multiple test correction which is the
 * most conservative approach.
 *
 * @author Sebastian Bauer
 */
public class Bonferroni implements MultipleTestingCorrection
{
	/** The name of the correction method */
	private static final String NAME = "Bonferroni";

  @Override
  public void adjustPvals(List<? extends PValue> pvals) {
    int N=pvals.size();
    for (PValue item : pvals) {
      double p_raw= item.getRawPValue();
      item.setAdjustedPValue(Math.min(1.0,N*p_raw));
    }
  }




	public String getName()
	{
		return NAME;
	}
}
