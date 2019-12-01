package org.monarchinitiative.phenol.analysis;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.io.obo.go.GoGeneAnnotationParser;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssociationContainerTest {


  @Test
  void localTest() throws PhenolException {
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

    Set<TermId> allAnnotatedGenes = associationContainer.getAllAnnotatedGenes();
    Map<TermId, DirectAndIndirectTermAnnotations> assocs = associationContainer.getAssociationMap(allAnnotatedGenes, ontology);
    StudySet populationSet = new PopulationSet(associationContainer.getAllAnnotatedGenes(),assocs, ontology);

  }
}
