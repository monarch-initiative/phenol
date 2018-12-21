package org.monarchinitiative.phenol.stats;


import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;

/**
 *
 * This class implements the BenjaminiHochberg multiple test
 * correction. It controls the FDR for independent and positive
 * regression dependent test statistics.
 *
 * The formular for p value adjustment is:
 *    adjusted-p-value = p-value * (n/n-rank),
 * with n being the number of p-values (tests) and rank being
 * the p-value's corresponding rank. Here rank starts at 0
 * whereby the highest p-value has the smallest rank (sorted
 * descreasingly).
 *
 * @author Sebastian Bauer
 *
 */
public class BenjaminiHochberg extends AbstractTestCorrection
{
  @Override
  public Map<TermId, PValue> adjustPValues(IPValueCalculation pValueCalculation)
  {
    Map<TermId, PValue> pvalmap = pValueCalculation.calculatePValues();
    Pair [] relevantP = getRelevantRawPValues(pvalmap);
    int n = relevantP.length;

    /* Adjust the p values according to BH. Note that all object
     * within relevantP also are objects within p!
     */
    for (int r=0;r<n;r++) {
      relevantP[r].pval.p_adjusted = relevantP[r].pval.p * n / (r + 1);
    }
    enforcePValueMonotony(relevantP);
    return pvalmap;
  }

  @Override
  public String getName() {
    return "Benjamini-Hochberg";
  }

}
