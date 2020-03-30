package org.monarchinitiative.phenol.stats.mtc;

import org.monarchinitiative.phenol.stats.PValue;

import java.util.Collections;
import java.util.List;

public class BenjaminiYekutieli implements MultipleTestingCorrection {

  @Override
  public void adjustPvals(List<? extends PValue> pvals) {
    Collections.sort(pvals);
    int N = pvals.size();
    double h = 0.0;
    for (int l = 1; l <= N; l++) {
      h += 1.0 / l;
    }
    /* Adjust the p values according to BY. Note that all object
     * within relevantP also are objects within p!
     */
    for (int r = 0; r < N; r++) {
      PValue item = pvals.get(r);
      double raw_p = item.getRawPValue();
      double adj_p = raw_p * N * h / (r + 1);
      item.setAdjustedPValue(adj_p);
    }
    enforcePValueMonotony(pvals);
  }


  public String getName() {
    return "Benjamini-Yekutieli";
  }
}
