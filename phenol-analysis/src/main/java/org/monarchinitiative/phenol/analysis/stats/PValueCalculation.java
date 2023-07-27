package org.monarchinitiative.phenol.analysis.stats;


import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.analysis.DirectAndIndirectTermAnnotations;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.analysis.stats.mtc.MultipleTestingCorrection;


import java.util.List;

/**
 * Abstract base class for methods that calculate a p-values for ontology term overrepresentation.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public abstract class PValueCalculation {

  protected MinimalOntology ontology;
  /**
   * The population of items (e.g., genes) investigated in some study.
   */
  protected StudySet populationSet;
  /**
   * The genes or items identified as special (e.g., differentially expressed) in some study.
   */
  protected StudySet studySet;
  protected Hypergeometric hyperg;

  protected boolean verbose = false;

  protected MultipleTestingCorrection testCorrection;

  /**
   * Key: a GO id; value: a {@link DirectAndIndirectTermAnnotations} object with the annotations of the GO Term.
   */
 // protected Map<TermId, DirectAndIndirectTermAnnotations> annotationMap;


  protected PValueCalculation() {
  }


  public PValueCalculation(MinimalOntology graph,
                           StudySet populationSet,
                           StudySet studySet,
                           MultipleTestingCorrection mtc) {
    this.ontology = graph;
    this.populationSet = populationSet;
    this.studySet = studySet;
    this.hyperg = new Hypergeometric();
    this.testCorrection = mtc;
   // initAssociationMap(graph);
  }

  public abstract List<GoTerm2PValAndCounts> calculatePVals();


//  /**
//   * Initialize the {@link #annotationMap}, whose key is a GO Id and whose value is a
//   * {@link DirectAndIndirectTermAnnotations} object that contains the genes that have
//   * GO annotations to the term. Note that the function also adds counts for direct and
//   * direct and total (including propagated) annotations.
//   *
//   * @param associationContainer associations for all the Ontology terms
//   * @param ontology             reference to the ontology
//   */
//  private void initAssociationMap(AssociationContainer associationContainer, Ontology ontology) {
//    Set<TermId> genes = associationContainer.getAllAnnotatedGenes();
//    annotationMap = new HashMap<>();
//    int n_notfound = 0;
//    for (TermId geneId : genes) {
//      try {
//        //int idx = associationContainer.getIndex(geneId);
//        ItemAssociations assocs = associationContainer.get(geneId);
//        for (TermAnnotation goAnnotation : assocs) {
//          /* At first add the direct counts and remember the terms */
//          TermId goId = goAnnotation.getTermId();
//          // check if the term is in the ontology (sometimes, obsoletes are used in the bla32 files)
//          Term term = ontology.getTermMap().get(goId);
//          if (term == null) {
//            n_notfound++;
//            if (verbose) {
//              System.err.println("Unable to retrieve term for id=" + goId.getValue());
//            }
//            continue;
//          }
//          // replace an alt_id with the primary id.
//          // if we already have the primary id, nothing is changed.
//          TermId primaryGoId = term.getId();
//          annotationMap.putIfAbsent(goId, new DirectAndIndirectTermAnnotations());
//          DirectAndIndirectTermAnnotations termAnnots = annotationMap.get(primaryGoId);
//          termAnnots.addGeneAnnotationDirect(geneId);
//          // In addition to the direct annotation, the gene is also indirectly annotated to all of the
//          // GO Term's ancestors
//          Set<TermId> ancs = getAncestorTerms(ontology, primaryGoId, true);
//          for (TermId ancestor : ancs) {
//            annotationMap.putIfAbsent(ancestor, new DirectAndIndirectTermAnnotations());
//            DirectAndIndirectTermAnnotations termAncAnnots = annotationMap.get(ancestor);
//            termAncAnnots.addGeneAnnotationTotal(geneId);
//          }
//        }
//      } catch (PhenolException e) {
//        e.printStackTrace();// TODO are there exceptions
//      }
//    }
//    if (n_notfound > 0) {
//      System.err.printf("[WARNING] Unable to find data for %d GO ids. Are data sources in synch?\n", n_notfound);
//    }
//  }

}
