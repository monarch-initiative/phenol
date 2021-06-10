package org.monarchinitiative.phenol.cli.demo;


import org.monarchinitiative.phenol.analysis.mgsa.MgsaCalculation;
import org.monarchinitiative.phenol.analysis.mgsa.MgsaGOTermsResultContainer;
import org.monarchinitiative.phenol.annotations.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import org.monarchinitiative.phenol.analysis.*;

import java.io.File;
import java.util.*;

import com.google.common.collect.ImmutableSet;
import org.monarchinitiative.phenol.stats.*;
import org.monarchinitiative.phenol.stats.mtc.Bonferroni;
import org.monarchinitiative.phenol.stats.mtc.MultipleTestingCorrection;
import picocli.CommandLine;

/**
 * This demo app shows how Gene Ontology enrichment analysis is performed using the
 * Fisher Exact test. The app inputs the GO file and the GAF annotation file and the
 * name of a GO term (default: GO:0070997, 'neuron death'). It then creates a study set
 * that has 1/3 of the genes associated with this term
 * and three times as many other terms. The enrichment analysis should identify this term
 * and may identify a few related terms.
 *
 * @author Manuel Holtgrewe
 * @author Peter Robinson
 */


@CommandLine.Command(name = "download", aliases = {"D"},
  mixinStandardHelpOptions = true,
  description = "Download files for fenominal")
public final class GoEnrichmentDemo {


  /**
   * Term Id of a GO term we will investigate
   */
  private final TermId targetGoTerm;

  private final Ontology gontology;

  private final List<GoGaf21Annotation> goAnnots;

  private final Set<TermId> populationGenes;

  private final StudySet studySet;

  private final StudySet populationSet;

  private final int popsize;

  private final int studysize;

  private final GoAssociationContainer associationContainer;

  private static final double ALPHA = 0.05;

  private static final int STUDYSET_SIZE = 400;


  public GoEnrichmentDemo(String pathGoObo, String pathGoGaf, String goTermId) {
    this.targetGoTerm = TermId.of(goTermId);
    System.out.println("[INFO] parsing  " + pathGoObo);
    gontology = OntologyLoader.loadOntology(new File(pathGoObo), "GO");
    int n_terms = gontology.countAllTerms();
    System.out.println("[INFO] parsed " + n_terms + " GO terms.");
    System.out.println("[INFO] parsing  " + pathGoGaf);
    associationContainer = GoAssociationContainer.loadGoGafAssociationContainer(pathGoGaf, gontology);
    goAnnots = associationContainer.getRawAssociations();
    populationGenes = getPopulationGenes(goAnnots);
    Set<TermId> studyGenes = getFocusedStudyGenes(goAnnots, targetGoTerm);
    Map<TermId, DirectAndIndirectTermAnnotations> studyAssociations = associationContainer.getAssociationMap(studyGenes);
    studySet = associationContainer.fromGeneIds(studyGenes, "study");
    Map<TermId, DirectAndIndirectTermAnnotations> populationAssociations = associationContainer.getAssociationMap(populationGenes);
    populationSet = associationContainer.fromGeneIds(populationGenes, "population");
    popsize = populationGenes.size();
    studysize = studyGenes.size();
  }



  private void performMgsaAnalysis() {
    System.out.println();
    System.out.println("[INFO] Demo: MGSA analysis");
    System.out.println();
    int mcmcSteps = 500000;
    MgsaCalculation mgsa = new MgsaCalculation(this.gontology, this.associationContainer, mcmcSteps);
    MgsaGOTermsResultContainer result = mgsa.calculateStudySet(studySet);
    result.dumpToShell();
  }



  private void performTermForTermAnalysis() {
    System.out.println();
    System.out.println("[INFO] Demo: Term-for-term analysis");
    System.out.println();

    MultipleTestingCorrection bonf = new Bonferroni();
    TermForTermPValueCalculation tftpvalcal = new TermForTermPValueCalculation(gontology,
      populationSet,
      studySet,
      bonf);
    List<GoTerm2PValAndCounts> pvals = tftpvalcal.calculatePVals();
    System.out.println("[INFO] Total number of retrieved p values: " + pvals.size());
    int n_sig = 0;
    System.out.printf("[INFO] Target term %s [%s]\n",
      gontology.getTermMap().get(targetGoTerm).getName(), targetGoTerm.getValue());
    System.out.printf("[INFO] Study set: %d genes. Population set: %d genes\n", studysize, popsize);
    for (GoTerm2PValAndCounts item : pvals) {
      double pval = item.getRawPValue();
      double pval_adj = item.getAdjustedPValue();
      TermId tid = item.getItem();
      Term term = gontology.getTermMap().get(tid);
      if (term == null) {
        System.err.println("[ERROR] Could not retrieve term for " + tid.getValue());
        continue;
      }
      String label = term.getName();
      if (pval_adj > ALPHA) {
        continue;
      }
      n_sig++;
      double studypercentage = 100.0 * (double) item.getAnnotatedStudyGenes() / studysize;
      double poppercentage = 100.0 * (double) item.getAnnotatedPopulationGenes() / popsize;
      System.out.printf("%s [%s]: %.2e (adjusted %.2e). Study: n=%d (%.1f%%); population: N=%d (%.1f%%)\n",
        label, tid.getValue(), pval, pval_adj, item.getAnnotatedStudyGenes(), studypercentage,
        item.getAnnotatedPopulationGenes(), poppercentage);
    }
    System.out.printf("%d of %d terms were significant at alpha %.7f\n", n_sig, pvals.size(), ALPHA);
  }


  private void performParentChildIntersectionAnalysis() {
    System.out.println();
    System.out.println("[INFO] Demo: parent child intersection analysis");
    System.out.println();
    MultipleTestingCorrection bonf = new Bonferroni();
    ParentChildPValuesCalculation pcPvalCalc = new ParentChildIntersectionPValueCalculation(gontology,
      populationSet,
      studySet,
      bonf);

    List<GoTerm2PValAndCounts> pvals = pcPvalCalc.calculatePVals();
    System.err.println("Total number of retrieved p values: " + pvals.size());
    int n_sig = 0;
    System.out.printf("GO Parent Child Intersection Enrichment Demo for target term %s [%s]\n",
      gontology.getTermMap().get(targetGoTerm).getName(), targetGoTerm.getValue());
    System.out.printf("Study set: %d genes. Population set: %d genes\n",
      studysize, popsize);
    for (GoTerm2PValAndCounts item : pvals) {
      double pval = item.getRawPValue();
      double pval_adj = item.getAdjustedPValue();
      TermId tid = item.getItem();
      Term term = gontology.getTermMap().get(tid);
      if (term == null) {
        System.err.println("[ERROR] Could not retrieve term for " + tid.getValue());
        continue;
      }
      String label = term.getName();
      if (pval_adj > ALPHA) {
        continue;
      }
      n_sig++;
      double studypercentage = 100.0 * (double) item.getAnnotatedStudyGenes() / studysize;
      double poppercentage = 100.0 * (double) item.getAnnotatedPopulationGenes() / popsize;
      System.out.printf("PCI: %s [%s]: %.2e (adjusted %.2e). Study: n=%d (%.1f%%); population: N=%d (%.1f%%)\n",
        label, tid.getValue(), pval, pval_adj, item.getAnnotatedStudyGenes(), studypercentage,
        item.getAnnotatedPopulationGenes(), poppercentage);
    }
    System.out.printf("PCI: %d of %d terms were significant at alpha %.7f\n", n_sig, pvals.size(), ALPHA);
  }



  private Set<TermId> getFocusedStudyGenes(List<GoGaf21Annotation> annots, TermId focus) {
    return getFocusedStudyGenes(annots, focus, 0.5); // default proportion of 50%
  }

  private Set<TermId> getFocusedStudyGenes(List<GoGaf21Annotation> annots, TermId focus, double proportion) {
    Set<TermId> targetGenes = new HashSet<>();
    for (TermAnnotation ann : annots) {
      if (focus.equals(ann.getTermId())) {
        TermId geneId = ann.getLabel();
        targetGenes.add(geneId);
      }
    }

    int N = targetGenes.size();
    System.out.printf("[INFO] Genes annotated to %s: n=%d\n", focus.getValue(), N);
    int M = (int)( (proportion*N) / 2.0); // take one third of the target genes

    Set<TermId> finalGenes = new HashSet<>();
    int i = 0;
    for (TermId tid : targetGenes) {
      if (i++ > M) break;
      finalGenes.add(tid);
    }
    for (TermAnnotation ann : annots) {
      TermId gene = ann.getLabel();
      if (!targetGenes.contains(gene)) {
        finalGenes.add(gene);
      }
      if (finalGenes.size() > STUDYSET_SIZE) break;
    }

    return ImmutableSet.copyOf(finalGenes);
  }

  /**
   * Get a list of all of the labeled genes in the population set.
   *
   * @param annots List of annotations of genes/diseases to GO/HPO terms etc
   * @return an immutable set of TermIds representing the labeled genes/diseases
   */
  private Set<TermId> getPopulationGenes(List<GoGaf21Annotation> annots) {
    Set<TermId> st = new HashSet<>();
    for (TermAnnotation ann : annots) {
      TermId geneId = ann.getLabel();
      st.add(geneId);
    }
    return ImmutableSet.copyOf(st);
  }



  public void run()  {
    System.out.println("[INFO] Target term: " + this.gontology.getTermMap().get(targetGoTerm).getName());
    performTermForTermAnalysis();
    performParentChildIntersectionAnalysis();
    performMgsaAnalysis();
  }


}
