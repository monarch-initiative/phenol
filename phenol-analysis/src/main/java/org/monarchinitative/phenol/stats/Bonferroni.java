/*
 * Created on 06.07.2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.monarchinitative.phenol.stats;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;

/**
 * This class implements the Bonferroni multiple test correction which is the
 * most conservative approach.
 *
 * @author Sebastian Bauer
 */
public class Bonferroni extends AbstractSimpleTestCorrection
{
	/** The name of the correction method */
	private static final String NAME = "Bonferroni";

	public Map<TermId, PValue> adjustPValues(IPValueCalculation pValueCalculation)
	{
		Map<TermId, PValue> pvalmap = pValueCalculation.calculatePValues();
		int pvalsCount = (int) pvalmap.values().stream().filter(PValue::doNotIgnore).count();

		/* Adjust the values */
		for (PValue p : pvalmap.values())
		{
			if (!p.ignoreAtMTC)
				p.p_adjusted = Math.min(1.0, p.p * pvalsCount);
		}
		return pvalmap;
	}

	public String getDescription()
	{
		return "The Bonferroni correction is the most conservative approach.";
	}

	public String getName()
	{
		return NAME;
	}
}
