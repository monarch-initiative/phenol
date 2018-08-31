package org.monarchinitative.phenol;


import org.monarchinitiative.phenol.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <P>
 * ItemAssociations objects store all the goAssociations for one single item.
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

public class ItemAssociations implements Iterable<GoGaf21Annotation>, Serializable
{
    private static final long serialVersionUID = 1L;

    /** TermId of the item (e.g., gene) for which this object stores 0 - n Associations (e.g., GO associations). */
    private TermId  gene;

    /** List of GO functional annotations */
    private ArrayList<GoGaf21Annotation> associations;

    /**
     *
     * @param itemId name of the gene or other item being annotated
     */
    public ItemAssociations(TermId itemId)
    {
        associations = new ArrayList<GoGaf21Annotation>();
        gene = itemId;
    }

    /**
     * Add a new bla32 to the gene.
     *
     * @param a defines the bla32 to be added.
     */
    public void add(GoGaf21Annotation a) {
        associations.add(a);
    }

    public TermId name()
    {
        return gene;
    }

    /**
     * Get an arraylist of all GO Ids to which this gene is directly
     * annotated by extracting the information from the Association object(s)
     * belonging to the gene.
     */
    public List<TermId> getAssociations() {
       return this.associations.stream().map(GoGaf21Annotation::getGoId).collect(Collectors.toList());
    }

    /**
     * Returns the iterator to iterate over all goAssociations.
     */
    public Iterator<GoGaf21Annotation> iterator()
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
       return associations.stream().anyMatch(annot -> annot.getGoId().equals(tid) );
    }
}
