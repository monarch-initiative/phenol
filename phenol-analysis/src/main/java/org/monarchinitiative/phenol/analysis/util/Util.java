package org.monarchinitiative.phenol.analysis.util;

import org.monarchinitiative.phenol.analysis.ItemAnnotations;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Util {

  public static Set<TermId> getDomainItemsAnnotatedByOntologyTerm(TermId termId,
                                                                  Ontology ontology,
                                                                  Map<TermId, ? extends ItemAnnotations<TermId>> gene2associationMap) {
    Set<TermId> domainItemSet = new HashSet<>();
    // the following includes termId in the descendent set
    Set<TermId> descendentSet = OntologyAlgorithm.getDescendents(ontology, termId);

    for (Map.Entry<TermId, ? extends ItemAnnotations<TermId>> entry : gene2associationMap.entrySet()) {
      TermId gene = entry.getKey();
      for (TermId ontologyTermId : entry.getValue().getAnnotatingTermIds()) {
        if (descendentSet.contains(ontologyTermId) || ontologyTermId.equals(termId)) {
          domainItemSet.add(gene);
        }
      }
    }

    return domainItemSet;
  }

}
