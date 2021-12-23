package org.monarchinitiative.phenol.analysis;


import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>ItemAssociations objects store all the annotations for one single item.
 * For instance, we might store all of the GO annotations for one gene/protein (the item).
 * For many use cases, the datatype of the item will also be a TermId (e.g., one that represents
 * the accession number of the gene).
 * </p>
 * <p>The default implementation in phenol is GeneAnnotations (for GO), but
 * the interface can be used for other annotated objects.</p>
 *
 * @author Peter Robinson, Sebastian Bauer
 */

public interface ItemAnnotations<T> {

    /** @return the annotated domain item (e.g., a gene) that is annotated by ontology terms */
    T annotatedItem();

  /**
   * @return an arraylist of all {@link TermAnnotation} objects to which this item is directly annotated
   */
  List<TermAnnotation> getAnnotations();

    /**
     * @return an arraylist of all ontology Ids to which this item is directly annotated
     */
    default List<TermId> getAnnotatingTermIds() {
      return getAnnotations().stream()
        .map(TermAnnotation::getTermId)
        .collect(Collectors.toList());
    }

    /**
     * Returns whether the given term id is associated.
     *
     * @param tid the id of the term that should be checked.
     * @return whether tid is contained in this mapping.
     */
    default boolean containsAnnotation(TermId tid) {
      return getAnnotations().stream().anyMatch(annot -> annot.getTermId().equals(tid) );
    }
}
