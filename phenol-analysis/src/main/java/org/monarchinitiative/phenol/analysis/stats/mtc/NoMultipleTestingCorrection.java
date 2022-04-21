package org.monarchinitiative.phenol.analysis.stats.mtc;

import org.monarchinitiative.phenol.analysis.stats.PValue;

import java.util.List;

/**
 * This is the identity function and applies no multiple testing correction.
 * Doing like this makes for cleaner code where choosing No MTC can be do as with the other MTCs.
 */
public class NoMultipleTestingCorrection implements MultipleTestingCorrection {
  /** The name of the correction method */
  private static final String NAME = "None";

  public String getDescription()
  {
    return "no MTC";
  }

  @Override
  public void adjustPvals(List<? extends PValue> pvals) {
    return; // no op, no MTC performed
  }

  public String getName()
  {
    return NAME;
  }
}
