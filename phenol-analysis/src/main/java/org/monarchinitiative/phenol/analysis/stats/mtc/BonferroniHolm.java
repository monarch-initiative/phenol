package org.monarchinitiative.phenol.analysis.stats.mtc;


import org.monarchinitiative.phenol.analysis.stats.PValue;

import java.util.*;

/**
 * This class implements the Bonferroni-Holm (or step down) multiple test
 * correction.
 *
 * @author Sebastian Bauer
 * @author <a href="mailto:peter.robinson@jax.org>Peter Robinson</a>
 */

public class BonferroniHolm implements MultipleTestingCorrection {
  /** The name of the correction method. */
  private static final String NAME = "Bonferroni-Holm";

  @Override
  public void adjustPvals(List<? extends PValue> pvals) {
    Collections.sort(pvals);
    int N=pvals.size();
    for (int r=0;r<N;r++) {
      PValue item = pvals.get(r);
      double raw_p = item.getRawPValue();
      item.setAdjustedPValue(raw_p * N/(r+1));
    }
    enforcePValueMonotony(pvals);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
