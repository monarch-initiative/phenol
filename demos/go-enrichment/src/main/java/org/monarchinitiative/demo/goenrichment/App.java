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

  private String pathGoObo;
  private String pathGoGaf;
  private String pathInputFile;


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
      TermId focusTerm = TermId.constructWithPrefix("GO:0008219"); // cell death
      Set<TermId> studyGenes = getFocusedStudySet(goAnnots,focusTerm);
      StudySet studySet = new StudySet(studyGenes,"cell death",associationContainer,gontology);
      Hypergeometric hgeo = new Hypergeometric();

      IPValueCalculation tftpvalcal = new TermForTermPValueCalculation(gontology,
        associationContainer,
        populationSet,
        studySet,
        hgeo);
      AbstractTestCorrection bonf = new Bonferroni();
      Map<TermId, PValue> pvalmap = bonf.adjustPValues(tftpvalcal);
      System.err.println("Total number of retrieved p values: " + pvalmap.size());
      for (TermId tid : pvalmap.keySet()) {
        PValue pval = pvalmap.get(tid);
        Term term = gontology.getTermMap().get(tid);
        if (term==null) {
          System.err.println("[ERROR] Could not retrieve term for " + tid.getIdWithPrefix());
          continue;
        }
        String label = term.getName();
        if (pval.p_adjusted > 0.00005) {
          continue;
        }
        System.err.println(String.format("%s [%s]: %.8f (adjusted %.5f)", label, tid.getIdWithPrefix(), pval.p, pval.p_adjusted));
      }

    } catch (Exception e) {
      e.printStackTrace(); // just wimp out, we cannot recover here.
    }
  }



  private Set<TermId> getFocusedStudySet(List<GoGaf21Annotation> annots, TermId focus) {
    Set<TermId> genes = new HashSet<TermId>();
    for (GoGaf21Annotation ann : annots) {
      if (focus.equals(ann.getGoId())) {
        TermId geneId = ann.getDbObjectIdAsTermId();
        genes.add(geneId);
      }
    }

    return ImmutableSet.copyOf(genes);
  }


  private Set<TermId> getPopulationSet(List<GoGaf21Annotation> annots) {
    Set<TermId> st = new HashSet<TermId>();
    for (GoGaf21Annotation ann : annots) {
      TermId geneId = ann.getDbObjectIdAsTermId();
      st.add(geneId);
    }
    return ImmutableSet.copyOf(st);
  }



  /**
   * Parse command line arguments.
   */
  private void parseArgs(String [] args) {
    if (args.length != 3) {
      printUsageError("Invalid argument count!");
    }

    pathGoObo = args[0];
    pathGoGaf = args[1];
    pathInputFile = args[2];
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
