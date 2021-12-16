package org.monarchinitiative.phenol.analysis.mgsa;

/**
 * A basic class to represent different settings parameter.
 *
 *
 * @author sba
 *
 */
abstract public class MgsaParam {
    public enum Type {
        FIXED,
        MCMC
    }

    private Type type;

    MgsaParam(Type type)
    {
        this.type = type;
    }

    MgsaParam(MgsaParam p)
    {
        this.type = p.type;
    }

    public Type getType()
    {
        return type;
    }

    public boolean isFixed()
    {
        return type == Type.FIXED;
    }

    public boolean isMCMC()
    {
        return type == Type.MCMC;
    }

    public void setType(Type type)
    {
        this.type = type;
    }
}

