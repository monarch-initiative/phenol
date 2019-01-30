package org.monarchinitiative.phenol.stats;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.analysis.TermAnnotations;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;


import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * A specific term-for-term p-value calculation.
 *
 * @author Sebastian Bauer
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class TermForTermPValueCalculation extends PValueCalculation
{
  private final static Logger logger = Logger.getLogger(StudySet.class.getName());
    public TermForTermPValueCalculation(Ontology graph,
                                        AssociationContainer goAssociations,
                                        StudySet populationSet,
                                        StudySet studySet,
                                        Hypergeometric hyperg,
                                        MultipleTestingCorrection mtc) {
        super(graph,goAssociations,populationSet,studySet,hyperg,mtc);
    }



  public List<Item2PValue<TermId>> calculatePVals(){
    Map<TermId, TermAnnotations> studySetAnnotationMap =this.studySet.getAnnotationMap();
   // ImmutableMap.Builder<TermId, Item2PValue<TermId>> builder=new ImmutableMap.Builder<>();

    ImmutableList.Builder<Item2PValue<TermId>> listbuilder = new ImmutableList.Builder<>();
    int popGeneCount = populationSet.getAnnotatedItemCount();
    int studyGeneCount = studySet.getAnnotatedItemCount();
    for (Map.Entry<TermId,TermAnnotations> entry : studySetAnnotationMap.entrySet() ) {
      if (entry.getValue().totalAnnotatedCount()<2) {
        continue; // only a single annotated entry -- do not perform a statistical test
      }
      TermId goId = entry.getKey();
      if (! this.annotationMap.containsKey(goId)) {
        System.err.println("ERROR -- study set contains ID but pop set does not: "+ goId.getValue());
      }
      int goidAnnotatedPopGeneCount = this.annotationMap.get(goId).totalAnnotatedCount();
      int goidAnnotatedStudyGeneCount = studySetAnnotationMap.get(goId).totalAnnotatedCount();
      if (goidAnnotatedStudyGeneCount != 0) {
        /* Imagine the following...
         *
         * In an urn you put popGeneCount number of balls where a color of a
         * ball can be white or black. The number of balls having white color
         * is goidAnnontatedPopGeneCount (all genes of the population which
         * are annotated by the current GOID).
         *
         * You choose to draw studyGeneCount number of balls without replacement.
         * How big is the probability, that you got goidAnnotatedStudyGeneCount
         * white balls after the whole drawing process?
         */
        double raw_pval=hyperg.phypergeometric(popGeneCount,
           (double)goidAnnotatedPopGeneCount / (double)popGeneCount,
             studyGeneCount,
           goidAnnotatedStudyGeneCount);
        Item2PValue<TermId> item = new Item2PValue<>(goId,raw_pval);
        listbuilder.add(item);
      }
      // If desired we could record the SKIPPED TESTS (Terms) HERE
    }
    // Now do multiple testing correction
    List<Item2PValue<TermId>> mylist = listbuilder.build();
    this.testCorrection.adjustPvals(mylist);
    return mylist;
  }

}

