package org.monarchinitative.phenol.stats;

/**
 * GO Properties for term for term approach.
 *
 * @author Sebastian Bauer
 */

public class TermForTermGOTermProperties extends AbstractGOTermProperties
{
	private static final String [] propertyNames = new String[]{
		"ID","Pop.total","Pop.term","Study.total","Study.term","p","p.adjusted","p.min"
		};

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
			case	5: return Double.toString(p);
			case	6: return Double.toString(p_adjusted);
			case	7: return Double.toString(p_min);
      default: return "TermForTermGOTermProperties-did not recognize propNumber " + propNumber;
		}
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
}
