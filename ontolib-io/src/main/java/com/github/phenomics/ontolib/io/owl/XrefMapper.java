package com.github.phenomics.ontolib.io.owl;

import com.github.phenomics.ontolib.ontology.data.ImmutableTermId;
import com.github.phenomics.ontolib.ontology.data.ImmutableTermXref;
import com.github.phenomics.ontolib.ontology.data.TermXref;

/**
 * Map the representation of Cross-references
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class XrefMapper {
	public static TermXref mapXref(String xref) {
		ImmutableTermId xrefTermId = ImmutableTermId.constructWithPrefix(xref);
		TermXref termXref = new ImmutableTermXref(xrefTermId, null);
		return termXref;
	}
}