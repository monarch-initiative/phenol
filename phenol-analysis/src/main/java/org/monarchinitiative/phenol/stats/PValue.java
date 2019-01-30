package org.monarchinitiative.phenol.stats;


/**
 * A class holding an p value and an object needed
 * for a class implementing IPValueCalculation.
 *
 * @author Sebastian Bauer
 */
public class PValue implements Cloneable, Comparable<PValue> {
  /**
   * The nominal (unadjusted) p value of a test.
   */
  private double p;
  /**
   * The adjusted p value of a test (adjusted according to a method such as Bonferoni
   * or Bejamini Holm).
   */
  private double p_adjusted;
  /**
   * Indicates whether the p value should be ignored my an MTC.(and hence no adjusted
   * p value will be applied). This is the case for instance if no genes in the study
   * set are annotated to this term (default=false).
   */
  private boolean ignoreAtMTC=false;


  public PValue() {
  }

  public PValue(double pval) {
    this.p = pval;
  }




  public void setRawPValue(double pv) {
    this.p = pv;
  }

  public void setAdjustedPValue(double p_adj) {
    this.p_adjusted = p_adj;
  }


  public double getRawPValue() {
    return this.p;
  }

  public double getAdjustedPValue() {
    return this.p_adjusted;
  }

  public void setIgnoreAtMTC() {
    ignoreAtMTC=true;
  }


  @Override
  public int compareTo(PValue o) {
    if (p < o.p) return -1;
    if (p > o.p) return 1;
    return 0;
  }

  public boolean doNotIgnore() {
    return !ignoreAtMTC;
  }

  /**
   * Returns whether this pvalue is significant according
   * to a given threshold.
   *
   * @param thresh significance threshold (alpha)
   * @return true if significant, else false
   */
  public boolean isSignificant(double thresh) {
    return p_adjusted < thresh;
  }
}

