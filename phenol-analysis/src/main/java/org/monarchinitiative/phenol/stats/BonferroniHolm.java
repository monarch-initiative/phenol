package org.monarchinitiative.phenol.stats;


import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

/**
 * This class implements the Bonferroni-Holm (or step down) multiple test
 * correction.
 *
 * @author Sebastian Bauer
 * @author <a href="mailto:peter.robinson@jax.org>Peter Robinson</a>
 */

public class BonferroniHolm extends AbstractTestCorrection {
  /** The name of the correction method. */
  private static final String NAME = "Bonferroni-Holm";

  @Override
  public Map<TermId, PValue> adjustPValues(IPValueCalculation pValueCalculation) {
    Map<TermId, PValue> pvalmap = pValueCalculation.calculatePValues();
    Pair[] pairs = getRelevantRawPValues(pvalmap);

    // Adjust the p values. Note that all object within pvalmap
    // are also objects within pairs!
    for (int i = 0; i < pairs.length; i++) {
      pairs[i].pval.p_adjusted = pairs[i].pval.p * (pairs.length - i);
    }
    enforcePValueMonotony(pairs);
    return pvalmap;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
