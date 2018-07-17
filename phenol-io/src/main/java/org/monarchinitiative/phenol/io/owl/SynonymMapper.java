package org.monarchinitiative.phenol.io.owl;

import java.util.List;

import com.google.common.collect.ImmutableList;
import org.geneontology.obographs.model.meta.SynonymPropertyValue;
import org.geneontology.obographs.model.meta.SynonymPropertyValue.PREDS;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermSynonym;
import org.monarchinitiative.phenol.ontology.data.TermSynonymScope;
import org.monarchinitiative.phenol.ontology.data.TermXref;
import com.google.common.collect.Lists;

/**
 * Map the representation of Synonym
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
class SynonymMapper {
  /** @return list of synoynms (can be an empty list but cannot be null). */
  static List<TermSynonym> mapSynonyms(List<SynonymPropertyValue> spvs) {
    if (spvs == null) return ImmutableList.of();
    List<TermSynonym> termSynonymList = Lists.newArrayList();
    for (SynonymPropertyValue spv : spvs) {

      // Map the scope of Synonym
      TermSynonymScope scope = null;
      String pred = spv.getPred();
      if (pred.equals(PREDS.hasExactSynonym.toString())) {
        scope = TermSynonymScope.EXACT;
      } else if (pred.equals(PREDS.hasBroadSynonym.toString())) {
        scope = TermSynonymScope.BROAD;
      } else if (pred.equals(PREDS.hasNarrowSynonym.toString())) {
        scope = TermSynonymScope.NARROW;
      } else if (pred.equals(PREDS.hasRelatedSynonym.toString())) {
        scope = TermSynonymScope.RELATED;
      }

      // Map the synonym's type name.
      String synonymTypeName = String.join(", ", spv.getTypes());

      // Map the synonym's cross-references.
      List<String> xrefs = spv.getXrefs();
      List<TermXref> termXrefs = mapXref(xrefs);

      TermSynonym its = new TermSynonym(spv.getVal(), scope, synonymTypeName, termXrefs);
      termSynonymList.add(its);
    }

    return termSynonymList;
  }

  /**
   * We try to map the cross references to Curies, e.g., ORCID:0000-0000-0000-0123.
   * If a cross-reference is not in CURIE for, we just ignore it. For now we
   * use an empty string for the Description field of the cross-reference.
   * @param xrefs list of cross references as Strings
   * @return list of cross references as {@link TermXref} objects. Can be empty but not null.
   */
  private static List<TermXref> mapXref(List<String>  xrefs) {
    List<TermXref> termXrefs = Lists.newArrayList();
    for (String xref : xrefs) {
      try {
        TermId xrefTermId = TermId.constructWithPrefix(xref);
        TermXref trf = new TermXref(xrefTermId,"");
        termXrefs.add(trf);
      } catch (Exception e) {
        // ignore
      }
    }

    return termXrefs;
  }




}
