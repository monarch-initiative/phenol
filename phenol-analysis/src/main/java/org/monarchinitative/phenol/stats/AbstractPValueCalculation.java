package org.monarchinitative.phenol.stats;


import com.google.common.collect.ImmutableMap;
import org.monarchinitative.phenol.AssociationContainer;
import org.monarchinitative.phenol.ItemAssociations;
import org.monarchinitative.phenol.StudySet;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.getAncestorTerms;


public abstract class AbstractPValueCalculation implements IPValueCalculation {

    protected Ontology ontology;

    protected AssociationContainer goAssociations;
    /** The population of items (e.g., genes) investigated in some study. */
    protected StudySet populationSet;
    /** The genes or items identified as special (e.g., differentially expressed) in some study. */
    protected StudySet studySet;
    protected Hypergeometric hyperg;

    /**
     * Key: a GO id; value: a {@link TermAnnotations} object with the annotations of the Go Term.
     */
    protected Map<TermId, TermAnnotations> annotationMap;


    protected AbstractPValueCalculation() {
    }


    public AbstractPValueCalculation(Ontology graph,
                                     AssociationContainer goAssociations,
                                     StudySet populationSet,
                                     StudySet studySet,
                                     Hypergeometric hyperg) {
        this.ontology = graph;
        this.goAssociations = goAssociations;
        this.populationSet = populationSet;
        this.studySet=studySet;
        this.hyperg = hyperg;
        initAssociationMap(goAssociations, graph);
    }


    public abstract Map<TermId, PValue> calculatePValues();

    /**
     * Initialize the {@link #annotationMap}, whose key is a GO Id and whose value is a
     * {@link TermAnnotations} object that contains the genes that have
     * GO annotations to the term. Note that the function also adds counts for direct and
     * direct and total (including propagated) annotations.
     *
     * @param associationContainer
     * @param ontology
     */
    private void initAssociationMap(AssociationContainer associationContainer, Ontology ontology) {
        Set<TermId> genes = associationContainer.getAllAnnotatedGenes();
        annotationMap = new HashMap<>();
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
     * Create an annotation map from the AssociationContainer and the study set.
     * Note that the Association container holds
     * @param associationContainer
     * @param ontology
     * @param studySet
     * @return
     */
    public Map<TermId, TermAnnotations> createAnnotationMap(AssociationContainer associationContainer,
                                                            Ontology ontology,
                                                            StudySet studySet) {
        Map<TermId, TermAnnotations> studyMap = new HashMap<>();
        for (TermId geneId : studySet.getAllGeneIds()) {
            try {
                System.err.println("createAnnotationMap gene=" + geneId.getIdWithPrefix());
                ItemAssociations assocs = associationContainer.get(geneId);
                for (GoGaf21Annotation goAnnotation : assocs) {
                    TermId goId = goAnnotation.getGoId();
                    Term term = ontology.getTermMap().get(goId);
                    if (term == null) {
                        System.err.println("Unable to retrieve term for id=" + goId.getIdWithPrefix());
                        continue;
                    }
                    goId = term.getId(); // move to the primary ID in case an alt_id was used!
                    studyMap.putIfAbsent(goId, new TermAnnotations());
                    TermAnnotations termAnnots = studyMap.get(goId);
                    termAnnots.addGeneAnnotationDirect(geneId);
                    // In addition to the direct annotation, the gene is also indirectly annotated to all of the
                    // GO Term's ancestors
                    Set<TermId> ancs = getAncestorTerms(ontology, goId, true);
                    for (TermId ancestor : ancs) {
                        studyMap.putIfAbsent(ancestor, new TermAnnotations());
                        TermAnnotations termAncAnnots = studyMap.get(ancestor);
                        termAncAnnots.addGeneAnnotationTotal(geneId);
                    }
                }
            } catch (PhenolException ee) {
                ee.printStackTrace(); // todo do something sensible here
            }
        }
        return ImmutableMap.copyOf(studyMap);
    }

}
