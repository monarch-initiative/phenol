package org.monarchinitiative.phenol.stats;


import java.util.Collections;
import java.util.List;

/**
 *
 * This class implements the BenjaminiHochberg multiple test
 * correction. It controls the FDR for independent and positive
 * regression dependent test statistics.
 *
 * The formula for p value adjustment is:
 *    adjusted-p-value = p-value * (n/n-rank),
 * with n being the number of p-values (tests) and rank being
 * the p-value's corresponding rank. Here rank starts at 0
 * whereby the highest p-value has the smallest rank (sorted
 * descreasingly).
 *
 * @author Sebastian Bauer
 *
 */
public class BenjaminiHochberg<T> implements MultipleTestingCorrection<T>
{
  @Override
  public void adjustPvals(List<? extends Item2PValue<T>> pvals) {
    Collections.sort(pvals);
    int N=pvals.size();
    for (int r=0;r<N;r++) {
      Item2PValue item = pvals.get(r);
      double raw_p = item.getRawPValue();
      item.setAdjustedPValue(raw_p * N/(r+1));
    }
    enforcePValueMonotony(pvals);
  }



  @Override
  public String getName() {
    return "Benjamini-Hochberg";
  }

}
