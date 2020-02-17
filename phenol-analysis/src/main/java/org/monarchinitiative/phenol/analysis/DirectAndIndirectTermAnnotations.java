package org.monarchinitiative.phenol.analysis;


import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashSet;
import java.util.Set;

/**
 * Instances of this class represent items (genes, represented by TermId objects) that are annotated to
 * the same GO/HPO/etc Term. The GO/HPO/etc Term itself is not represented in this object.
 *
 * @author Sebastian Bauer
 */
public class DirectAndIndirectTermAnnotations
{
    /** List of directly annotated genes */
    private Set<TermId> directAnnotated = new HashSet<>();

    /** List of genes annotated in total (direct or via annotation propagation) */
    private Set<TermId> totalAnnotated = new HashSet<>();


    public void addGeneAnnotationDirect(TermId geneId) {
        directAnnotated.add(geneId);
        totalAnnotated.add(geneId);
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

