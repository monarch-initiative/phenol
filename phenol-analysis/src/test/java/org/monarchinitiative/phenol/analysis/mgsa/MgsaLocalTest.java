package org.monarchinitiative.phenol.analysis.mgsa;

import com.google.common.collect.ImmutableSet;

import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MgsaLocalTest {

  private final String goTermId = "GO:0070997";
  private final TermId targetGoTerm = TermId.of(goTermId);

  /**
   * Get a list of all of the labeled genes in the population set.
   * @param annots List of annotations of genes/diseases to GO/HPO terms etc
   * @return an immutable set of TermIds representing the labeled genes/diseases
   */
  private Set<TermId> getPopulationSet(List<TermAnnotation> annots) {
    Set<TermId> st = new HashSet<>();
    for (TermAnnotation ann : annots) {
      TermId geneId = ann.getLabel();
      st.add(geneId);
    }
    return ImmutableSet.copyOf(st);
  }



  private Set<TermId> getFocusedStudySet(List<TermAnnotation> annots, TermId focus) {
    Set<TermId> genes = new HashSet<>();
    for (TermAnnotation ann : annots) {
      if (focus.equals(ann.getTermId())) {
        TermId geneId = ann.getLabel();
        genes.add(geneId);
      }
    }

    int N = genes.size();
    System.out.println(String.format("[INFO] Genes annotated to %s: n=%d",focus.getValue(),N));
    int M = N;
    if (N>20) {
      M = N/3;
    }
    Set<TermId> finalGenes=new HashSet<>();
    int i=0;
    for (TermId tid: genes) {
      if (i++>M) break;
      finalGenes.add(tid);
    }
    i = 0;
    M *= 3;
    for (TermAnnotation ann : annots) {
      TermId gene = ann.getLabel();
      if (! genes.contains(gene)) {
        finalGenes.add(gene);
        i++;
      }
      if (i>M) break;
    }

    return ImmutableSet.copyOf(finalGenes);
  }

/*

  @Test @Ignore
  void testLoadMgsa() {
    String goPath = "/CHANGE/robinp/data/go/go.obo";
    String gafPath = "/home/robinp/data/go/goa_human.gaf";

    System.out.println("[INFO] parsing  " + goPath);
    Ontology gontology = OntologyLoader.loadOntology(new File(goPath), "GO");
    int n_terms = gontology.countAllTerms();
    System.out.println("[INFO] parsed " + n_terms + " GO terms.");
    System.out.println("[INFO] parsing  " + gafPath);
    // final GoGeneAnnotationParser annotparser = new GoGeneAnnotationParser(pathGoGaf);
    List<TermAnnotation> goAnnots = GoGeneAnnotationParser.loadTermAnnotations(gafPath);
    System.out.println("[INFO] parsed " + goAnnots.size() + " GO annotations.");

    AssociationContainer associationContainer = new AssociationContainer(goAnnots);
    System.out.println("[INFO] AssociationContainer=" + associationContainer.toString());
    Set<TermId> populationGenes = getPopulationSet(goAnnots);

    Set<TermId> studyGenes = getFocusedStudySet(goAnnots,targetGoTerm);
    Map<TermId, DirectAndIndirectTermAnnotations> studyAssociations = associationContainer.getAssociationMap(studyGenes, gontology);
    StudySet studySet = new StudySet(studyGenes,"study", studyAssociations);
    Map<TermId, DirectAndIndirectTermAnnotations> populationAssociations = associationContainer.getAssociationMap(populationGenes, gontology);
    StudySet populationSet = new PopulationSet(populationGenes, populationAssociations);
    MgsaCalculation mgsa = new MgsaCalculation(gontology, associationContainer, 100000);

    mgsa.calculateStudySet(studySet);


    assertEquals(1,1);
  }

 */

}
