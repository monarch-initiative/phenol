package org.monarchinitiative.phenol.stats;


import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.ItemAssociations;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.analysis.TermAnnotations;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.getAncestorTerms;

/**
 * Abstract base class for methods that calculate a p-values for ontology term overrepresentation.
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public abstract class PValueCalculation {

    protected Ontology ontology;

    protected AssociationContainer goAssociations;
    /** The population of items (e.g., genes) investigated in some study. */
    protected StudySet populationSet;
    /** The genes or items identified as special (e.g., differentially expressed) in some study. */
    protected StudySet studySet;
    protected Hypergeometric hyperg;

    protected MultipleTestingCorrection testCorrection;

    /**
     * Key: a GO id; value: a {@link TermAnnotations} object with the annotations of the GO Term.
     */
    protected Map<TermId, TermAnnotations> annotationMap;


    protected PValueCalculation() {
    }


    public PValueCalculation(Ontology graph,
                             AssociationContainer goAssociations,
                             StudySet populationSet,
                             StudySet studySet,
                             Hypergeometric hyperg,MultipleTestingCorrection mtc) {
        this.ontology = graph;
        this.goAssociations = goAssociations;
        this.populationSet = populationSet;
        this.studySet=studySet;
        this.hyperg = hyperg;
        this.testCorrection=mtc;
        initAssociationMap(goAssociations, graph);
    }

    public abstract List<GoTerm2PValAndCounts> calculatePVals();


    /**
     * Initialize the {@link #annotationMap}, whose key is a GO Id and whose value is a
     * {@link TermAnnotations} object that contains the genes that have
     * GO annotations to the term. Note that the function also adds counts for direct and
     * direct and total (including propagated) annotations.
     *
     * @param associationContainer associations for all the Ontology terms
     * @param ontology reference to the ontology
     */
    private void initAssociationMap(AssociationContainer associationContainer, Ontology ontology) {
        Set<TermId> genes = associationContainer.getAllAnnotatedGenes();
        annotationMap = new HashMap<>();
        for (TermId geneId : genes) {
            try {
                //int idx = associationContainer.getIndex(geneId);
                ItemAssociations assocs = associationContainer.get(geneId);
                for (TermAnnotation goAnnotation : assocs) {
                    /* At first add the direct counts and remember the terms */
                    TermId goId = goAnnotation.getTermId();
                    // check if the term is in the ontology (sometimes, obsoletes are used in the bla32 files)
                    Term term = ontology.getTermMap().get(goId);
                    if (term == null) {
                        System.err.println("Unable to retrieve term for id=" + goId.getValue());
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

}
