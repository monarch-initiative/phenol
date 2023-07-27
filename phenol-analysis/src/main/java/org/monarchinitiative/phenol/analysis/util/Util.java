package org.monarchinitiative.phenol.analysis.util;

import org.monarchinitiative.phenol.analysis.ItemAnnotations;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Util {

  public static Set<TermId> getDomainItemsAnnotatedByOntologyTerm(TermId termId,
                                                                  MinimalOntology ontology,
                                                                  Map<TermId, ? extends ItemAnnotations<TermId>> gene2associationMap) {
    Set<TermId> domainItemSet = new HashSet<>();
    // the following includes termId in the descendent set
    Set<TermId> descendentSet = ontology.graph().getDescendantsStream(termId, true).collect(Collectors.toSet());

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
