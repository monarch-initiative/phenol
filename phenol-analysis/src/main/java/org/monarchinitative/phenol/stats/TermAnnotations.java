package org.monarchinitative.phenol.stats;


import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashSet;
import java.util.Set;

/**
 * Instanced of this class represent items that are annotated to
 * the same term without any reference to that term.
 * TODO - replace corresponding class in phenol
 *
 * @author Sebastian Bauer
 */
public class TermAnnotations
{
    /** List of directly annotated genes TODO: Make private */
    private Set<TermId> directAnnotated = new HashSet<>();

    /** List of genes annotated at whole TODO: Make private */
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

