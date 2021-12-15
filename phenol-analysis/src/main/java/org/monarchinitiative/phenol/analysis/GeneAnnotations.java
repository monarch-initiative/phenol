package org.monarchinitiative.phenol.analysis;

import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class contains an annotated gene (represented as a {@link TermId}) as well as
 * the explicit (direct) Ontology Terms that annotate the gene.
 */
public class GeneAnnotations implements ItemAnnotations<TermId> {


    /** TermId of the item (e.g., gene) for which this object stores 0 - n Associations (e.g., GO associations). */
    private final TermId annotatedGene;

    /** List of annotations (associations of the annotatedItem with Ontology Terms. */
    private final List<TermAnnotation> annotations;

    /**
     *
     * @param itemId name of the gene or other item being annotated
     */
    public GeneAnnotations(TermId itemId) {
        annotations = new ArrayList<>();
        annotatedGene = itemId;
    }

    /**
     * Add a new {@link TermAnnotation} to the item.
     *
     * @param a defines the {@link TermAnnotation} to be added.
     */
    public void addAnnotation(TermAnnotation a) {
        annotations.add(a);
    }

    public TermId annotatedItem() {
        return annotatedGene;
    }

    /**
     * Get an arraylist of all GO Ids to which this gene is directly
     * annotated by extracting the information from the Association object(s)
     * belonging to the gene.
     */
    public List<TermId> getAnnotatingTermIds() {
        return this.annotations.stream().map(TermAnnotation::getTermId).collect(Collectors.toList());
    }

    @Override
    public List<TermAnnotation> getAnnotations() {
        return this.annotations;
    }

    /**
     * Returns the iterator to iterate over all {@link TermAnnotation} objects associated with this item.
     */
    public Iterator<TermAnnotation> iterator()
    {
        return annotations.iterator();
    }

    /**
     * Returns whether the given term id is associated.
     *
     * @param tid the id of the term that should be checked.
     * @return whether tid is contained in this mapping.
     */
    public boolean containsAnnotation(TermId tid) {
        return annotations.stream().anyMatch(annot -> annot.getTermId().equals(tid) );
    }

}
