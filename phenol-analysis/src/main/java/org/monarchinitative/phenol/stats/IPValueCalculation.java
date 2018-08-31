package org.monarchinitative.phenol.stats;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;

/**
 *
 * This interface abstracts the p value calculation for the multiple test
 * correction procedure.
 *
 * @author Sebastian Bauer
 */
public interface IPValueCalculation
{
	/**
	 * Calculate raw (unadjusted) p values. An array of p value objects will be
	 * returned.
	 *
	 * @return the calculated raw p-values
	 */
	public Map<TermId, PValue> calculatePValues();


}
