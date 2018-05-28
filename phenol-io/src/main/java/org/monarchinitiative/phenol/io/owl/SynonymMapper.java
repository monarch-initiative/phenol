package org.monarchinitiative.phenol.io.owl;

import java.util.List;

import org.geneontology.obographs.model.meta.SynonymPropertyValue;
import org.geneontology.obographs.model.meta.SynonymPropertyValue.PREDS;

import org.monarchinitiative.phenol.ontology.data.TermSynonym;
import org.monarchinitiative.phenol.ontology.data.TermSynonymScope;
import org.monarchinitiative.phenol.ontology.data.TermXref;
import com.google.common.collect.Lists;

/**
 * Map the representation of Synonym
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class SynonymMapper {
  public static List<TermSynonym> mapSynonyms(List<SynonymPropertyValue> spvs) {
    if (spvs == null) return null;
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
      List<TermXref> termXrefs = Lists.newArrayList();
      for (String xref : xrefs) {
        termXrefs.add(XrefMapper.mapXref(xref));
      }

      TermSynonym its = new TermSynonym(spv.getVal(), scope, synonymTypeName, termXrefs);
      termSynonymList.add(its);
    }

    return termSynonymList;
  }
}
