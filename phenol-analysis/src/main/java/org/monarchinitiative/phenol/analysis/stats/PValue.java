package org.monarchinitiative.phenol.analysis.stats;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.analysis.stats.mtc.Bonferroni;

/**
 * This class is used to store an item (an ontology TermId) and its associated
 * raw and adjusted p values.
 */
public class PValue implements Comparable<PValue> {
  /** The Ontology TermId whose overrepresentation has been assessed to have this p-value. */
  protected final TermId item;
  /** The nominal (i.e., uncorrected) p-value for this item. */
  protected final double p_raw;
  /**
   * The adjusted p_raw value of a test (adjusted according to a method such as Bonferoni
   * or Bejamini Holm).
   */
  protected double p_adjusted;





  /**
   * This constructor takes an Item for which a pvalue was calculated. It assigned both {@link #p_raw} (the
   * raw pavel) and {@link #p_adjusted} to this value (i.e., by default there is no multiple testing
   * correction. The class is designed to be used with other classes such as {@link Bonferroni} to
   * adjust the raw pvalues that are stored in {@link #p_adjusted}.
   * @param item An item whose p value was calculated
   * @param p the corresponding p value
   */
  public PValue(TermId item, double p) {
      this.item=item;
      this.p_raw =p;
      this.p_adjusted=p;
  }

  public TermId getItem() {
    return this.item;
  }


  public double getRawPValue() {
    return this.p_raw;
  }

  public void setAdjustedPValue(double p_adj) {
    this.p_adjusted = p_adj;
  }

  public double getAdjustedPValue() {
    return this.p_adjusted;
  }


  @Override
  public int compareTo(PValue o) {
    return Double.compare(p_raw,o.p_raw);
  }


}
