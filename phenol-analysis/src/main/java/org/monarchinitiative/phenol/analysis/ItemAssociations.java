package org.monarchinitiative.phenol.analysis;


import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <P>
 * ItemAssociations objects store all the annotations for one single item.
 * For instance, we might store all of the GO annotations for one gene/protein.
 * </P>
 * <P>
 * Note that duplicate entries are possible in the bla32 files. For this
 * reason, we make sure there is only one entry for each GO:id number. We do
 * this by storing a list of all goIDs seen in the arrayList goIDs.
 * </P>
 * <P>
 * This class implements the Iterable interface, so you easily can iterate
 * over the goAssociations to this gene.
 *
 * @author Peter Robinson, Sebastian Bauer
 */

public class ItemAssociations implements Iterable<TermAnnotation> {

    /** TermId of the item (e.g., gene) for which this object stores 0 - n Associations (e.g., GO associations). */
    private final TermId  gene;

    /** List of GO functional annotations */
    private final List<TermAnnotation> associations;

    /**
     *
     * @param itemId name of the gene or other item being annotated
     */
    public ItemAssociations(TermId itemId) {
        associations = new ArrayList<>();
        gene = itemId;
    }

    /**
     * Add a new {@link TermAnnotation} to the item.
     *
     * @param a defines the {@link TermAnnotation} to be added.
     */
    public void add(TermAnnotation a) {
        associations.add(a);
    }

    public TermId item()
    {
        return gene;
    }

    /**
     * Get an arraylist of all GO Ids to which this gene is directly
     * annotated by extracting the information from the Association object(s)
     * belonging to the gene.
     */
    public List<TermId> getAssociations() {
       return this.associations.stream().map(TermAnnotation::getTermId).collect(Collectors.toList());
    }

    /**
     * Returns the iterator to iterate over all {@link TermAnnotation} objects associated with this item.
     */
    public Iterator<TermAnnotation> iterator()
    {
        return associations.iterator();
    }

    /**
     * Returns whether the given term id is associated.
     *
     * @param tid the id of the term that should be checked.
     * @return whether tid is contained in this mapping.
     */
    public boolean containsID(TermId tid) {
       return associations.stream().anyMatch(annot -> annot.getTermId().equals(tid) );
    }
}
