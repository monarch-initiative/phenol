package org.monarchinitiative.phenol.analysis.mgsa;


class IntegerParam extends MgsaParam
{
    private int val;

    public IntegerParam(Type type, int val)
    {
        super(type);

        this.val = val;
    }

    public IntegerParam(Type type)
    {
        super(type);

        if (type == Type.FIXED) throw new IllegalArgumentException("Parameter could not be instantiated of type Fixed.");
    }

    public IntegerParam(IntegerParam p)
    {
        super(p);

        this.val = p.val;
    }

    int getValue()
    {
        return val;
    }

    void setValue(int newVal)
    {
        this.val = newVal;
        setType(Type.FIXED);
    }

    @Override
    public String toString()
    {
        if (isFixed()) return Integer.toString(val);
        return getType().toString();
    }
}
