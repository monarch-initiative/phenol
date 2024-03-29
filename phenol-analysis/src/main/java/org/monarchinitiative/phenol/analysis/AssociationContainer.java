package org.monarchinitiative.phenol.analysis;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the interface for the Ontology (usually: Gene Ontology) annotations.
 * It organizes the relationships between the annotated items (e.g., genes/proteins)
 * and the ontology terms that annotate them (e.g., GO terms). It also allows both
 * explicit (direct) and inferred (indirect) annotations to be retrieved. The
 * {@link GoAssociationContainer} is the implementation that we use mainly for
 * Gene Ontology annotations. Other implementations are made for isopret.
 * The AssociationContainer object contains all annotations that were derived from the
 * corresponding GoGaf file (or comparable source). The main purpose of the object is
 * to help construct StudySet objects with the {@link #getAssociationMap} function, which
 * takes a set of domain IDs and returns a map of domain IDs and corresponding
 * {@link DirectAndIndirectTermAnnotations} objects.
 * @author Peter N Robinson
 */
public interface AssociationContainer<T> {

  Map<TermId, List<T>> getOntologyTermToDomainItemsMap();

  Map<T, DirectAndIndirectTermAnnotations> getAssociationMap(Set<TermId> annotatedItemTermIds);

  Set<T> getAllAnnotatedGenes();

  /**
   * @param tid The {@link TermId} of an ontology term (e.g. Gene Ontology) used to annotated domain items
   * @return A set of domain items (e.g., genes) annotated by the ontology term
   */
  Set<T> getDomainItemsAnnotatedByOntologyTerm(TermId tid);
  /**
   * @return total number of GO (or HP, MP, etc) terms that are annotating the items in this container.
   */
  int getAnnotatingTermCount();

  int getTotalAnnotationCount();
  /**
   * Get total number of genes (or other domain items) that have at least one annotation).
   */
  default int getAnnotatedDomainItemCount() {
    return getAllAnnotatedGenes().size();
  }

}
