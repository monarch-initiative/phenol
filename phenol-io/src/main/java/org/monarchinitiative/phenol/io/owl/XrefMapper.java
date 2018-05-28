package org.monarchinitiative.phenol.io.owl;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermXref;

/**
 * Map the representation of Cross-references
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
class XrefMapper {
  static TermXref mapXref(String xref) {
    TermId xrefTermId = TermId.constructWithPrefix(xref);
    return new TermXref(xrefTermId, null);
  }
}
