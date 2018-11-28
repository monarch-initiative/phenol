package org.monarchinitiative.demo.goenrichment;

import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.io.obo.go.GoOboParser;
import org.monarchinitiative.phenol.io.obo.go.GoGeneAnnotationParser;
import org.monarchinitiative.phenol.formats.go.GoOntology;
import org.monarchinitiative.phenol.formats.go.GoGaf21Annotation;
import org.monarchinitiative.phenol.analysis.AssociationContainer;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.*;
import com.google.common.collect.ImmutableSet;
import org.monarchinitiative.phenol.stats.*;


public final class App {
  /** Path to the go.obo file */
  private String pathGoObo;
  /** Path to the GoGaf file. */
  private String pathGoGaf;
  /** We will create a study set that has 1/3 of the genes associated with this term
   * and three times as many other terms. GO:0070997 is 'neuron death'.*/
  private final static TermId DEFAULT_GO_ID = TermId.of("GO:0070997");
  /** Term Id of a GO term we will investigate */
  private TermId targetGoTerm=DEFAULT_GO_ID;


  public App(String [] args) {
    parseArgs(args);
  }


  private void run() {
    try {
      System.out.println("[INFO] parsing  " + pathGoObo);
      GoOboParser parser = new GoOboParser(pathGoObo);
      GoOntology gontology = parser.parse();
      int n_terms=gontology.countAllTerms();
      System.out.println("[INFO] parsed " + n_terms + " GO terms.");
      System.out.println("[INFO] parsing  " + pathGoGaf);
      final GoGeneAnnotationParser annotparser = new GoGeneAnnotationParser(pathGoGaf);
      List<GoGaf21Annotation> goAnnots = annotparser.getAnnotations();
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
          System.err.println("[ERROR] Could not retrieve term for " + tid.getIdWithPrefix());
          continue;
        }
        String label = term.getName();
        if (pval.p_adjusted > ALPHA) {
          continue;
        }
        n_sig++;
        System.out.println(String.format("%s [%s]: %.8f (adjusted %.5f)", label, tid.getIdWithPrefix(), pval.p, pval.p_adjusted));
      }
      System.out.println(String.format("%d of %d terms were significant at alpha %.7f",
        n_sig,pvalmap.size(),ALPHA));
    } catch (Exception e) {
      e.printStackTrace(); // just wimp out, we cannot recover here.
    }
  }



  private Set<TermId> getFocusedStudySet(List<GoGaf21Annotation> annots, TermId focus) {
    Set<TermId> genes = new HashSet<>();
    for (GoGaf21Annotation ann : annots) {
      if (focus.equals(ann.getGoId())) {
        TermId geneId = ann.getDbObjectTermId();
        genes.add(geneId);
      }
    }

    int N=genes.size();
    System.out.println(String.format("[INFO] Total genes annotated to %s is %d",focus.getIdWithPrefix(),N));
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
    for (GoGaf21Annotation ann : annots) {
      TermId gene = ann.getDbObjectTermId();
      if (! genes.contains(gene)) {
        finalGenes.add(gene);
        i++;
      }
      if (i>M) break;
    }

    return ImmutableSet.copyOf(finalGenes);
  }


  private Set<TermId> getPopulationSet(List<GoGaf21Annotation> annots) {
    Set<TermId> st = new HashSet<>();
    for (GoGaf21Annotation ann : annots) {
      TermId geneId = ann.getDbObjectTermId();
      st.add(geneId);
    }
    return ImmutableSet.copyOf(st);
  }



  /**
   * Parse command line arguments.
   */
  private void parseArgs(String [] args) {
    if (args.length <2) {
      printUsageError(String.format("Invalid argument count (%d)! %s",args.length,String.join(";",args)));
    }

    pathGoObo = args[0];
    pathGoGaf = args[1];
    if (args.length==3) {
      targetGoTerm = TermId.of(args[2]);
    }
  }

  /**
   * Print error and usage, then exit.
   */
  private void printUsageError(String string) {
    System.err.println("ERROR: " + string + "\n");
    System.err.println("Usage: java -jar app.jar go.obo gene_to_pheno.tsv IN.tsv");
    System.exit(1);
  }

  /**
   * Program entry point.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    new App(args).run();
  }

}
