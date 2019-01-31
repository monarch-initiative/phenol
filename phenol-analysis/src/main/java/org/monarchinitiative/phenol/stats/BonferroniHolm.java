package org.monarchinitiative.phenol.stats;


import java.util.*;

/**
 * This class implements the Bonferroni-Holm (or step down) multiple test
 * correction.
 *
 * @author Sebastian Bauer
 * @author <a href="mailto:peter.robinson@jax.org>Peter Robinson</a>
 */

public class BonferroniHolm<T> implements MultipleTestingCorrection<T> {
  /** The name of the correction method. */
  private static final String NAME = "Bonferroni-Holm";

  @Override
  public void adjustPvals(List<Item2PValue<T>> pvals) {
    int N=pvals.size();
    if (N<2) return;
    for (int r=0;r<N;r++) {
      Item2PValue item = pvals.get(r);
      double raw_p = item.getRawPValue();
      item.setAdjustedPValue(raw_p * (N-1));
    }
    enforcePValueMonotony(pvals);
  }



  @Override
  public String getName() {
    return NAME;
  }
}
