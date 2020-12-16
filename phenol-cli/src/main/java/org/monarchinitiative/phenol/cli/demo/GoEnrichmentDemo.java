package org.monarchinitiative.phenol.cli.demo;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.monarchinitiative.phenol.analysis.mgsa.MgsaCalculation;
import org.monarchinitiative.phenol.analysis.mgsa.MgsaGOTermsResultContainer;
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
public final class GoEnrichmentDemo {
  /**
   * Path to the go.obo file
   */
  private final String pathGoObo;
  /**
   * Path to the GoGaf file.
   */
  private final String pathGoGaf;
  /**
   * Term Id of a GO term we will investigate
   */
  private final TermId targetGoTerm;

  private Ontology gontology;

  private final List<TermAnnotation> goAnnots;

  private final Set<TermId> populationGenes;

  private final StudySet studySet;

  private final StudySet populationSet;

  private final int popsize;

  private final int studysize;

  private final TermAssociationContainer associationContainer;

  private static final double ALPHA = 0.05;

  private static final int STUDYSET_SIZE = 400;



  public GoEnrichmentDemo(GoEnrichmentDemo.Options options) {
    this.pathGoObo = options.getGoPath();
    this.pathGoGaf = options.getGafPath();
    this.targetGoTerm = TermId.of(options.getGoTermId());
    System.out.println("[INFO] parsing  " + pathGoObo);
    gontology = OntologyLoader.loadOntology(new File(pathGoObo), "GO");
    int n_terms = gontology.countAllTerms();
    System.out.println("[INFO] parsed " + n_terms + " GO terms.");
    System.out.println("[INFO] parsing  " + pathGoGaf);
    associationContainer = TermAssociationContainer.loadGoGafAssociationContainer(pathGoGaf, gontology);
    goAnnots = associationContainer.getRawAssociations();
    populationGenes = getPopulationSet(goAnnots);
    Set<TermId> studyGenes = getFocusedStudySet(goAnnots, targetGoTerm);
    Map<TermId, DirectAndIndirectTermAnnotations> studyAssociations = associationContainer.getAssociationMap(studyGenes);
    studySet = new StudySet(studyGenes, "study", studyAssociations);
    Map<TermId, DirectAndIndirectTermAnnotations> populationAssociations = associationContainer.getAssociationMap(populationGenes);
    populationSet = StudySet.populationSet(populationGenes, populationAssociations);
    popsize = populationGenes.size();
    studysize = studyGenes.size();
  }


  public void run() {
    System.out.println("[INFO] Target term: " + this.gontology.getTermMap().get(targetGoTerm).getName());
    performTermForTermAnalysis();
    performParentChildIntersectionAnalysis();
    performMgsaAnalysis();
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
      associationContainer,
      populationSet,
      studySet,
      bonf);
    List<GoTerm2PValAndCounts> pvals = tftpvalcal.calculatePVals();
    System.out.println("[INFO] Total number of retrieved p values: " + pvals.size());
    int n_sig = 0;
    System.out.println(String.format("[INFO] Target term %s [%s]",
      gontology.getTermMap().get(targetGoTerm).getName(), targetGoTerm.getValue()));
    System.out.println(String.format("[INFO] Study set: %d genes. Population set: %d genes",
      studysize, popsize));
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
      System.out.println(String.format("%s [%s]: %.2e (adjusted %.2e). Study: n=%d (%.1f%%); population: N=%d (%.1f%%)",
        label, tid.getValue(), pval, pval_adj, item.getAnnotatedStudyGenes(), studypercentage,
        item.getAnnotatedPopulationGenes(), poppercentage));
    }
    System.out.println(String.format("%d of %d terms were significant at alpha %.7f", n_sig, pvals.size(), ALPHA));
  }


  private void performParentChildIntersectionAnalysis() {
    System.out.println();
    System.out.println("[INFO] Demo: parent child intersection analysis");
    System.out.println();
    MultipleTestingCorrection bonf = new Bonferroni();
    ParentChildPValuesCalculation pcPvalCalc = new ParentChildIntersectionPValueCalculation(gontology,
      associationContainer,
      populationSet,
      studySet,
      bonf);

    List<GoTerm2PValAndCounts> pvals = pcPvalCalc.calculatePVals();
    System.err.println("Total number of retrieved p values: " + pvals.size());
    int n_sig = 0;
    System.out.println(String.format("GO Parent Child Intersection Enrichment Demo for target term %s [%s]",
      gontology.getTermMap().get(targetGoTerm).getName(), targetGoTerm.getValue()));
    System.out.println(String.format("Study set: %d genes. Population set: %d genes",
      studysize, popsize));
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
      System.out.println(String.format("PCI: %s [%s]: %.2e (adjusted %.2e). Study: n=%d (%.1f%%); population: N=%d (%.1f%%)",
        label, tid.getValue(), pval, pval_adj, item.getAnnotatedStudyGenes(), studypercentage,
        item.getAnnotatedPopulationGenes(), poppercentage));
    }
    System.out.println(String.format("PCI: %d of %d terms were significant at alpha %.7f", n_sig, pvals.size(), ALPHA));
  }



  private Set<TermId> getFocusedStudySet(List<TermAnnotation> annots, TermId focus) {
    return getFocusedStudySet(annots, focus, 0.5); // default proportion of 50%
  }

  private Set<TermId> getFocusedStudySet(List<TermAnnotation> annots, TermId focus, double proportion) {
    Set<TermId> targetGenes = new HashSet<>();
    for (TermAnnotation ann : annots) {
      if (focus.equals(ann.getTermId())) {
        TermId geneId = ann.getLabel();
        targetGenes.add(geneId);
      }
    }

    int N = targetGenes.size();
    System.out.println(String.format("[INFO] Genes annotated to %s: n=%d", focus.getValue(), N));
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
  private Set<TermId> getPopulationSet(List<TermAnnotation> annots) {
    Set<TermId> st = new HashSet<>();
    for (TermAnnotation ann : annots) {
      TermId geneId = ann.getLabel();
      st.add(geneId);
    }
    return ImmutableSet.copyOf(st);
  }


  @Parameters(commandDescription = "Gene Ontology Enrichment Analysis (demo)")
  public static class Options {
    @Parameter(names = {"-o", "--obo"}, description = "path to go.obo file", required = true)
    private String goPath;

    @Parameter(names = {"-a", "--annot"}, description = "path to go association file (e.g., goa_human.gaf", required = true)
    private String gafPath;
    /**
     * For the demo, We will create a study set that has 1/3 of the genes associated with this term
     * and three times as many other terms. The default GO:0070997 is 'neuron death'.
     */
    @Parameter(names = {"-i", "--id"}, description = "term ID to search for enrichment")
    private String goTermId = "GO:0097190";

    String getGoPath() {
      return goPath;
    }

    String getGafPath() {
      return gafPath;
    }

    String getGoTermId() {
      return goTermId;
    }
  }


}
