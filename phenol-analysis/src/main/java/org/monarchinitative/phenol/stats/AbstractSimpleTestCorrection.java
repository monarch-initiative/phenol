package org.monarchinitative.phenol.stats;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Map;

public abstract class AbstractSimpleTestCorrection extends AbstractTestCorrection
{

	public abstract Map<TermId, PValue> adjustPValues(IPValueCalculation pValueCalculation );
}
