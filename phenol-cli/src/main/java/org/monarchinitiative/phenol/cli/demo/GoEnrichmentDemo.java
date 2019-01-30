package org.monarchinitiative.phenol.cli.demo;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.io.obo.go.GoGeneAnnotationParser;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import org.monarchinitiative.phenol.analysis.*;

import java.io.File;
import java.util.*;
import com.google.common.collect.ImmutableSet;
import org.monarchinitiative.phenol.stats.*;

/**
 * This demo app shows how Gene Ontology enrichtment analysis is performed using the
 * Fisher Exact test. The app inputs the GO file and the GAF annotation file and the
 * name of a GO term (default: GO:0070997, 'neuron death'). It then creates a study set
 * that has 1/3 of the genes associated with this term
 * and three times as many other terms. The enrichment analysis should identify this term
 * and may identify a few related terms.
 * @author Manuel Holtgrewe
 * @author Peter Robinson
 */
public final class GoEnrichmentDemo {
  /** Path to the go.obo file */
  private String pathGoObo;
  /** Path to the GoGaf file. */
  private String pathGoGaf;
  /** Term Id of a GO term we will investigate */
  private final TermId targetGoTerm;


  public GoEnrichmentDemo(GoEnrichmentDemo.Options options) {
    this.pathGoObo = options.getGoPath();
    this.pathGoGaf = options.getGafPath();
    this.targetGoTerm = TermId.of(options.getGoTermId());
  }


  public void run() {
    try {
      System.out.println("[INFO] parsing  " + pathGoObo);
      Ontology gontology = OntologyLoader.loadOntology(new File(pathGoObo), "GO");
      int n_terms=gontology.countAllTerms();
      System.out.println("[INFO] parsed " + n_terms + " GO terms.");
      System.out.println("[INFO] parsing  " + pathGoGaf);
      final GoGeneAnnotationParser annotparser = new GoGeneAnnotationParser(pathGoGaf);
      List<TermAnnotation> goAnnots = annotparser.getTermAnnotations();
      System.out.println("[INFO] parsed " + goAnnots.size() + " GO annotations.");

      AssociationContainer associationContainer = new AssociationContainer(goAnnots);

      Set<TermId> populationGenes = getPopulationSet(goAnnots);
      StudySet populationSet = new StudySet(populationGenes,"population",associationContainer,gontology);
      Set<TermId> studyGenes = getFocusedStudySet(goAnnots,targetGoTerm);
      StudySet studySet = new StudySet(studyGenes,"study",associationContainer,gontology);
      Hypergeometric hgeo = new Hypergeometric();

      IPValueCalculation tftpvalcal = new TermForTermPValueCalculation(gontology,
        associationContainer,
        populationSet,
        studySet,
        hgeo);
      AbstractTestCorrection bonf = new Bonferroni();
      Map<TermId, PValue> pvalmap = bonf.adjustPValues(tftpvalcal);
      System.err.println("Total number of retrieved p values: " + pvalmap.size());
      int n_sig=0;
      double ALPHA=0.00005;
      for (TermId tid : pvalmap.keySet()) {
        PValue pval = pvalmap.get(tid);
        Term term = gontology.getTermMap().get(tid);
        if (term==null) {
          System.err.println("[ERROR] Could not retrieve term for " + tid.getValue());
          continue;
        }
        String label = term.getName();
        if (pval.getAdjustedPValue() > ALPHA) {
          continue;
        }
        n_sig++;
        System.out.println(String.format("%s [%s]: %.8f (adjusted %.5f)", label, tid.getValue(), pval.getRawPValue(), pval.getAdjustedPValue()));
      }
      System.out.println(String.format("%d of %d terms were significant at alpha %.7f",
        n_sig,pvalmap.size(),ALPHA));
    } catch (Exception e) {
      e.printStackTrace(); // just wimp out, we cannot recover here.
    }
  }



  private Set<TermId> getFocusedStudySet(List<TermAnnotation> annots, TermId focus) {
    Set<TermId> genes = new HashSet<>();
    for (TermAnnotation ann : annots) {
      if (focus.equals(ann.getTermId())) {
        TermId geneId = ann.getLabel();
        genes.add(geneId);
      }
    }

    int N=genes.size();
    System.out.println(String.format("[INFO] Genes annotated to %s: n=%d",focus.getValue(),N));
    int M=N;
    if (N>20) {
      M=N/3;
    }
    Set<TermId> finalGenes=new HashSet<>();
    int i=0;
    for (TermId tid: genes) {
      if (i++>M) break;
      finalGenes.add(tid);
    }
    i=0;
    M *= 3;
    for (TermAnnotation ann : annots) {
      TermId gene = ann.getTermId();
      if (! genes.contains(gene)) {
        finalGenes.add(gene);
        i++;
      }
      if (i>M) break;
    }

    return ImmutableSet.copyOf(finalGenes);
  }

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



  @Parameters(commandDescription = "Gene Ontology Enrichment Analysis (demo)")
  public static class Options {
    @Parameter(names = {"-o","--obo"}, description = "path to go.obo file", required = true)
    private String goPath;

    @Parameter(names = {"-a","--annot"}, description = "path to go association file (e.g., goa_human.gaf", required = true)
    private String gafPath;
    /** For the demo, We will create a study set that has 1/3 of the genes associated with this term
      * and three times as many other terms. The default GO:0070997 is 'neuron death'.*/
    @Parameter(names = {"-i","--id"},description = "term ID to search for enrichment")
    private String goTermId="GO:0070997";

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
