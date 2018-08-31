package org.monarchinitative.phenol.stats;


/**
 *
 * A class holding an p value and an object needed
 * for a class implementing IPValueCalculation.
 *
 * @author Sebastian Bauer
 *
 */
public class PValue implements Cloneable, Comparable<PValue>
{
    public double p;
    public double p_adjusted;
    public double p_min;

    /**
     * Indicates whether the p value should be ignored my a mtc
     * (and hence no adjusted p value will be applied)
     */
    public boolean ignoreAtMTC;

    @Override
    public int compareTo(PValue o)
    {
        if (p < o.p) return -1;
        if (p > o.p) return 1;
        return 0;
    }

    public boolean doNotIgnore(){ return !ignoreAtMTC; }

    /**
     * Returns whether this pvalue is significant according
     * to a given threshold.
     *
     * @param thresh
     * @return true if significant, else false
     */
    public boolean isSignificant(double thresh)
    {
        return p_adjusted < thresh;
    }
}

