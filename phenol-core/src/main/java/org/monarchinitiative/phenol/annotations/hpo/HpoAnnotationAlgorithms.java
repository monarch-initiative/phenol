package org.monarchinitiative.phenol.annotations.hpo;

import org.monarchinitiative.phenol.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.getAncestorTerms;

/**
 * A set of "low-level" algorithms designed to perform some useful functions on collections
 * of HPO annotations (generally, following parsing of the HPO annotation file {@code phenotype.hpoa}
 * and import into {@link HpoDisease} objects).
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class HpoAnnotationAlgorithms {

  /**
   * Calculate the number of times HPO terms were used to annotate the diseases in diseaseSet
   * Excluded negated annotations. Only show terms with at least one annotation.
   * @param diseaseSet A set of diseases annotated with HPO terms
   * @param ontology A reference to the HPO ontology
   * @param propagate If true, include ancestors in the term counts
   * @return
   */
  public static Map<TermId,Integer> countAnnotationsInDiseaseSet(Set<HpoDisease> diseaseSet,
                                                                 HpoOntology ontology,
                                                                 boolean propagate) {
    Map<TermId,Integer> annotationCounts = new HashMap<>();
    for (HpoDisease disease : diseaseSet) {
      List<TermId> termlist= disease.getPhenotypicAbnormalityTermIdList();
      Set<TermId> termset = new HashSet<>(termlist);
      if (propagate) {
        termset.addAll(getAncestorTerms(ontology,termset,false));
      }
      for (TermId tid : termset) {
        annotationCounts.putIfAbsent(tid,0);
        int current = annotationCounts.get(tid);
        annotationCounts.put(tid,current+1);
      }
    }
    return annotationCounts;
  }

  /**
   * Calculate the number of times HPO terms were used to annotate the diseases in diseaseSet
   * Excluded negated annotations. Only show terms with at least one annotation.
   * Include ancestors in the term counts.
   * @param diseaseSet
   * @param ontology
   * @return
   */
  public static Map<TermId,Integer> countAnnotationsInDiseaseSet(Set<HpoDisease> diseaseSet,
                                                                 HpoOntology ontology) {
    return countAnnotationsInDiseaseSet(diseaseSet,ontology,true);
  }

}