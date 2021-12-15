package org.monarchinitiative.phenol.analysis;


import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Iterator;
import java.util.List;


/**
 * <p>ItemAssociations objects store all the annotations for one single item.
 * For instance, we might store all of the GO annotations for one gene/protein.
 * </p>
 * <p>
 * Note that duplicate entries are possible in the bla32 files. For this
 * reason, we make sure there is only one entry for each GO:id number. We do
 * this by storing a list of all goIDs seen in the arrayList goIDs.
 * </p>
 * <p>
 * This class implements the Iterable interface, so you easily can iterate
 * over the goAssociations to this gene.</p>
 * <p>The default implementation in phenol is GeneAnnotations (for GO), but
 * the interface can be used for other annotated objects.</p>
 *
 * @author Peter Robinson, Sebastian Bauer
 */

public interface ItemAnnotations<T> extends Iterable<TermAnnotation> {

    /**
     * Add a new {@link TermAnnotation} to the item.
     *
     * @param a defines the {@link TermAnnotation} to be added.
     */
    void addAnnotation(TermAnnotation a);
    /** @return the annotated domain item (e.g., a gene) that is annotated by ontology terms */
    T annotatedItem();

    /**
     * @return an arraylist of all ontology Ids to which this item is directly annotated
     */
    List<TermId> getAnnotatingTermIds();

    /**
     * @return an arraylist of all {@link TermAnnotation} objects to which this item is directly annotated
     */
    List<TermAnnotation> getAnnotations();

    /**
     * Returns the iterator to iterate over all {@link TermAnnotation} objects associated with this item.
     */
    Iterator<TermAnnotation> iterator();


    /**
     * Returns whether the given term id is associated.
     *
     * @param tid the id of the term that should be checked.
     * @return whether tid is contained in this mapping.
     */
    boolean containsAnnotation(TermId tid);
}
