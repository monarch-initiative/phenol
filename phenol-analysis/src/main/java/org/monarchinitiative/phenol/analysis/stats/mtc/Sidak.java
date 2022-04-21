package org.monarchinitiative.phenol.analysis.stats.mtc;

import org.monarchinitiative.phenol.analysis.stats.PValue;

import java.lang.Math;
import java.util.List;

/**
 * @author Sebastian Bauer
 * @author Peter Robinson (refactored)
 */
public class Sidak implements MultipleTestingCorrection
{
	/** The name of the correction method */
	private static final String NAME = "Sidak";

	public String getDescription()
	{
		return "";
	}

  @Override
  public void adjustPvals(List<? extends PValue> pvals) {
	  int pvalsCount = pvals.size();
    /* Adjust the values */
    for (PValue pval : pvals)
    {
      double p = pval.getRawPValue();
      double p_adjusted = 1.0 - Math.pow(1.0 - p, pvalsCount);
      pval.setAdjustedPValue(p_adjusted);
    }
  }

  public String getName()
	{
		return NAME;
	}
}
