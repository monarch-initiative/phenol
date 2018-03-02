package org.monarchinitiative.phenol.io.owl;

import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermXref;
import org.monarchinitiative.phenol.ontology.data.TermXref;

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
