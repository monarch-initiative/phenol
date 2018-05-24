package org.monarchinitiative.phenol.io.owl;

import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.TermXref;
import org.monarchinitiative.phenol.ontology.data.TermXrefI;

/**
 * Map the representation of Cross-references
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class XrefMapper {
  public static TermXref mapXref(String xref) {
    ImmutableTermId xrefTermId = ImmutableTermId.constructWithPrefix(xref);
    return new TermXref(xrefTermId, null);
  }
}
