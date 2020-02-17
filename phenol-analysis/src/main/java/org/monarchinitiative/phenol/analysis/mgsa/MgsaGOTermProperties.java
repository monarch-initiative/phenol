package org.monarchinitiative.phenol.analysis.mgsa;


/**
 * GO Properties for term for term approach.
 *
 * @author Sebastian Bauer
 */

public class MgsaGOTermProperties extends AbstractGOTermProperties
{
    private static final String[] propertyNames = new String[]{
            "ID","Pop.total","Pop.term","Study.total","Study.term","marg"
    };

    public double marg;

    @Override
    public int getNumberOfProperties()
    {
        return propertyNames.length;
    }

    @Override
    public String getPropertyName(int propNumber)
    {
        return propertyNames[propNumber];
    }

    @Override
    public String getProperty(int propNumber)
    {
        switch (propNumber)
        {
            case	0: return term.toString();
            case	1: return null; /* population gene count */
            case	2: return Integer.toString(annotatedPopulationGenes);
            case	3: return null; /* study gene count */
            case 	4: return Integer.toString(annotatedStudyGenes);
            case	5: return Double.toString(marg);
        }
        return null;
    }

    @Override
    public boolean isPropertyPopulationGeneCount(int propNumber)
    {
        return propNumber == 1;
    }

    @Override
    public boolean isPropertyStudyGeneCount(int propNumber)
    {
        return propNumber == 3;
    }

    @Override
    public boolean isSignificant(double thresh) {
        return marg > thresh;
    }
}
