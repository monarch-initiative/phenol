package org.monarchinitiative.phenol.analysis;


import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashSet;
import java.util.Set;

/**
 * Instances of this class represent items (genes, represented by TermId objects) that are annotated to
 * the same term without any reference to that term.
 *
 * @author Sebastian Bauer
 */
public class TermAnnotations
{
    /** List of directly annotated genes */
    private Set<TermId> directAnnotated = new HashSet<>();

    /** List of genes annotated in total (direct or via annotation propagation) */
    private Set<TermId> totalAnnotated = new HashSet<>();

    
    public void addGeneAnnotationDirect(TermId geneId) {
        directAnnotated.add(geneId);
    }

    public void addGeneAnnotationTotal(TermId geneId) {
        totalAnnotated.add(geneId);
    }


    public int directAnnotatedCount()
    {
        return directAnnotated.size();
    }

    public int totalAnnotatedCount()
    {
        return totalAnnotated.size();
    }
}

