package org.monarchinitative.phenol;


import org.monarchinitative.phenol.stats.TermAnnotations;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.getAncestorTerms;

/**
 * This class holds all gene names of a study and their associated
 * (optional) descriptions. The names are extracted directly by the
 * constructor given study file.
 *
 * The class implements the Iterable interface so you can
 * conveniently iterate over all includes gene names.
 *
 * @author Peter Robinson, Sebastian Bauer
 */
public class StudySet {
    private final static Logger logger = Logger.getLogger(StudySet.class.getName());

    /** Key: a GO id; value: a {@link TermAnnotations} object with the annotations of the Go Term. */
    private Map<TermId, TermAnnotations> annotationMap;
    /** List containing genes (or other items) of this set. These TermIds are not the same as for {@link #annotationMap},
     * instead, the geneIds are input into the {@link TermAnnotations} objects to indicate which genes are annotated
     * to the GO term forming the key of {@link #annotationMap}. */
    private final Set<TermId> geneIds;
    /** The name of the study set */
    private final String name;

    /**
     */
    public StudySet(Set<TermId> genes, String name, AssociationContainer associationContainer, Ontology ontology)
    {
        this.geneIds = genes;
        this.name=name;
        initAssociationMap(associationContainer,ontology);
    }

    public Map<TermId, TermAnnotations> getAnnotationMap() {
        return annotationMap;
    }

    public Set<TermId> getAllGeneIds() {
        return this.geneIds;
    }

    public Set<TermId> getAllAnnotatingTerms() {
        return this.annotationMap.keySet();
    }

    /**
     * @param goId a GeneOntology id
     * @return TermAnnotations object with all of the genes that annotate to goId
     */
    public TermAnnotations getAnnotatedGenes(TermId goId) {
       return this.annotationMap.get(goId);
    }

    private void initAssociationMap(AssociationContainer associationContainer, Ontology ontology) {
        Set<TermId> genes = associationContainer.getAllAnnotatedGenes();
        this.annotationMap = new HashMap<>();
        for (TermId geneId : genes) {
            try {
                //int idx = associationContainer.getIndex(geneId);
                ItemAssociations assocs = associationContainer.get(geneId);
                for (GoGaf21Annotation goAnnotation : assocs) {
                    /* At first add the direct counts and remember the terms */
                    TermId goId = goAnnotation.getGoId();
                    // check if the term is in the ontology (sometimes, obsoletes are used in the bla32 files)
                    Term term = ontology.getTermMap().get(goId);
                    if (term == null) {
                        System.err.println("Unable to retrieve term for id=" + goId.getIdWithPrefix());
                        continue;
                    }
                    // replace an alt_id with the primary id.
                    // if we already have the primary id, nothing is changed.
                    TermId primaryGoId = term.getId();
                    annotationMap.putIfAbsent(goId, new TermAnnotations());
                    TermAnnotations termAnnots = annotationMap.get(primaryGoId);
                    termAnnots.addGeneAnnotationDirect(geneId);
                    // In addition to the direct annotation, the gene is also indirectly annotated to all of the
                    // GO Term's ancestors
                    Set<TermId> ancs = getAncestorTerms(ontology, primaryGoId, true);
                    for (TermId ancestor : ancs) {
                        annotationMap.putIfAbsent(ancestor, new TermAnnotations());
                        TermAnnotations termAncAnnots = annotationMap.get(ancestor);
                        termAncAnnots.addGeneAnnotationTotal(geneId);
                    }
                }
            } catch (PhenolException e) {
                e.printStackTrace();// TODO are there exceptions
            }
        }
    }



    /**
     * Obtain the number of genes or gene products within this studyset.
     *
     * @return the desired count.
     */
    public int getGeneCount() {
        return geneIds.size();
    }

    /**
     * @return name of the Study
     */
    public String getName() {
        return name;
    }

    /* for debugging */
    public String toString()
    {
        String str = name + " (n=" + (getGeneCount()) + ")";
        return str;
    }


    /**
     * Checks whether study set contains the given gene.
     *
     * @param geneName
     * 		  the name of the gene which inclusion should be checked.
     * @return true if study contains gene, else false.
     */
    public boolean contains(TermId geneName) {
        return geneIds.contains(geneName);
    }







  }
