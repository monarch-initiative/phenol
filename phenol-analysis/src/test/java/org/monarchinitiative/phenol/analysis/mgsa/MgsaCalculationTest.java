package org.monarchinitiative.phenol.analysis.mgsa;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.DirectAndIndirectTermAnnotations;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.io.obo.go.GoGeneAnnotationParser;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MgsaCalculationTest {




    StudySet getFakeStudySet(AssociationContainer associationContainer, Ontology ontology ) {
        Set<TermId> allAnnotatedGenes = associationContainer.getAllAnnotatedGenes();
        Set<TermId> fakeStudyGenes = new HashSet<>();
        Random r = new Random();
        for (TermId tid : allAnnotatedGenes) {
            double rd = r.nextDouble();
            if (rd < 0.05) {
                fakeStudyGenes.add(tid);
            }
        }
        Map<TermId, DirectAndIndirectTermAnnotations> assocs = associationContainer.getAssociationMap(fakeStudyGenes, ontology);
        return new StudySet(fakeStudyGenes, "fake", assocs, ontology);
    }

    @Test
    void testMgsa() throws PhenolException {
        String localPathToGoObo = "/home/robinp/data/go/go.obo";
        localPathToGoObo = "/Users/peterrobinson/Documents/data/go/go.obo";
        Ontology ontology = OntologyLoader.loadOntology(new File(localPathToGoObo));
        System.out.printf("[INFO] loaded ontology with %d terms.\n", ontology.countNonObsoleteTerms());
        String localPathToGoGaf = "/home/robinp/data/go/goa_human.gaf";
        localPathToGoGaf = "/Users/peterrobinson/Documents/data/go/goa_human.gaf";
        final GoGeneAnnotationParser annotparser = new GoGeneAnnotationParser(localPathToGoGaf);
        List<TermAnnotation> goAnnots = annotparser.getTermAnnotations();
        System.out.println("[INFO] parsed " + goAnnots.size() + " GO annotations.");
        AssociationContainer associationContainer = new AssociationContainer(goAnnots);

        int mcmcSteps = 50000;
        MgsaCalculation mgsa = new MgsaCalculation(ontology, associationContainer, mcmcSteps);
        StudySet study = getFakeStudySet(associationContainer,ontology);
        mgsa.calculateStudySet(study);
        //b2gCalc.calculateStudySet(ontology, associationContainer, populationSet, study);
        assertTrue(true);
    }
}
