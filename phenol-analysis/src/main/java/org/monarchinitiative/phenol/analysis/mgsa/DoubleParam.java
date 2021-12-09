package org.monarchinitiative.phenol.analysis.mgsa;

/**
 * A class that encapsulates a parameter value that is adjusted by MGSA's MCMC procedure, together with minimum
 * and maximum allowable values.
 */
class DoubleParam extends MgsaParam {
    private double min = Double.NaN;
    private double max = Double.NaN;
    private double val;

    public DoubleParam(Type type, double val) {
        super(type);
        this.val = val;
    }

    public DoubleParam(DoubleParam p) {
        super(p);
        this.val = p.val;
    }

    public DoubleParam(Type type) {
        super(type);
        if (type == Type.FIXED) throw new IllegalArgumentException("Parameter could not be instanciated of type Fixed.");
    }

    double getValue()
    {
        return val;
    }

    void setValue(double newVal) {
        this.val = newVal;
        setType(Type.FIXED);
    }

    /**
     * Applicable for Variables of type MCMC or EM.
     *
     * @param min
     */
    public void setMin(double min)
    {
        this.min = min;
    }

    /**
     * Applicable for Variables of type MCMC or EM.
     *
     * @param max
     */
    public void setMax(double max)
    {
        this.max = max;
    }

    /**
     * Applicable for Variables of type MCMC or EM.
     *
     * @return NaN if no maximum has been specified.
     */
    public double getMin()
    {
        return min;
    }

    /**
     * @return whether variable has a minimum.
     */
    public boolean hasMin()
    {
        return !Double.isNaN(min);
    }

    /**
     * Applicable for Variables of type MCMC or EM.
     *
     * @return NaN if no maximum has been specified.
     */
    public double getMax()
    {
        return max;
    }

    /**
     * @return whether variable has a maximum.
     */
    public boolean hasMax()
    {
        return !Double.isNaN(max);
    }

    @Override
    public String toString()
    {
        if (isFixed()) return Double.toString(val);
        return getType().toString();
    }
}
