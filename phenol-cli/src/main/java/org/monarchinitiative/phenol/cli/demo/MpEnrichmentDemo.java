package org.monarchinitiative.phenol.cli.demo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.monarchinitiative.phenol.analysis.TermAssociationContainer;
import org.monarchinitiative.phenol.analysis.DirectAndIndirectTermAnnotations;
import org.monarchinitiative.phenol.analysis.StudySet;
import org.monarchinitiative.phenol.annotations.formats.mpo.MpAnnotation;
import org.monarchinitiative.phenol.annotations.formats.mpo.MpGeneticMarker;
import org.monarchinitiative.phenol.annotations.formats.mpo.MpGeneModel;
import org.monarchinitiative.phenol.annotations.formats.mpo.MpoGeneAnnotation;
import org.monarchinitiative.phenol.annotations.obo.mpo.MpAnnotationParser;
import org.monarchinitiative.phenol.annotations.obo.mpo.MpGeneParser;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.stats.*;
import org.monarchinitiative.phenol.stats.mtc.Bonferroni;
import org.monarchinitiative.phenol.stats.mtc.MultipleTestingCorrection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * A simple application that tests for statistically significant enrichment of MP terms for
 * a given set of input genes.
 *
 * @author Peter N Robinson
 */
public class MpEnrichmentDemo {

  private final Ontology ontology;

  private final Map<TermId, MpGeneModel> mpgenemap;

  private Map<String, TermId> symbol2termidMap;

  private final Map<TermId, MpGeneticMarker> markermap;

  private final String targetgenefile;


  public MpEnrichmentDemo(String mpPath, String MGIgenePhenoPath, String targetGeneFile, String markerFile) {
    this.targetgenefile = targetGeneFile;


    mpgenemap = MpAnnotationParser.loadMpGeneModels(MGIgenePhenoPath);
    markermap = MpGeneParser.loadMarkerMap(markerFile);
    System.out.println("[INFO] parsed " + markermap.size() + " MP markers.");
    System.out.println("[INFO] parsed " + mpgenemap.size() + " MP gene models.");

    ontology = OntologyLoader.loadOntology(new File(mpPath));
    System.out.println("[INFO] Parsed phenotyped info associated with " + mpgenemap.size() + " genes.");

  }

  private List<TermAnnotation> getTermAnnotations() {
    ImmutableList.Builder<TermAnnotation> builder = new ImmutableList.Builder<>();
    for (MpGeneModel model : mpgenemap.values()) {
      TermId markerId = model.getMarkerId();
      String symbol;
      if (markermap.containsKey(markerId)) {
        symbol = markermap.get(markerId).getGeneSymbol();
      } else {
        symbol = "n/a"; // should never happen
      }
      for (MpAnnotation annot : model.getPhenotypicAbnormalities()) {
        TermId mpId = annot.getTermId();
        if (mpId == null) {
          System.err.println("[ERROR] mp term id was null");
        }
        try {
          String label = ontology.getTermMap().get(mpId).getName();
          MpoGeneAnnotation mpoGeneAnnotation = new MpoGeneAnnotation(markerId, symbol, label, mpId);
          builder.add(mpoGeneAnnotation);
        } catch (Exception e) {
          System.err.println("[ERROR] copuld not find term for " + mpId);
        }
      }
    }
    return builder.build();
  }

  public void run() {
    int n_terms = ontology.countAllTerms();
    System.out.println("[INFO] parsed " + n_terms + " MP terms.");
    List<TermAnnotation> annots = getTermAnnotations();
    System.out.println("[INFO] parsed " + annots.size() + " MP annotations.");
    TermAssociationContainer associationContainer =  TermAssociationContainer.fromGoTermAnnotations(annots, ontology);
    Set<TermId> populationGenes = getPopulationSet(annots);
    System.out.println("[INFO] size of population set: " + populationGenes.size());
    Set<TermId> studyGenes = getStudySet();

    Map<TermId, DirectAndIndirectTermAnnotations> studyAssociations = associationContainer.getAssociationMap(studyGenes);
    StudySet studySet = new StudySet("study", studyAssociations);
    Map<TermId, DirectAndIndirectTermAnnotations> popAssociations = associationContainer.getAssociationMap(populationGenes);
    StudySet populationSet = new StudySet("population", popAssociations);
    System.out.printf("[INFO] study: %d genes, population: %d genes\n", studyGenes.size(), populationGenes.size());

    MultipleTestingCorrection bonf = new Bonferroni();
    TermForTermPValueCalculation tftpvalcal = new TermForTermPValueCalculation(ontology,
      populationSet,
      studySet,
      bonf);

    int popsize = populationGenes.size();
    int studysize = studyGenes.size();
    List<GoTerm2PValAndCounts> pvals = tftpvalcal.calculatePVals();
    System.err.println("Total number of retrieved p values: " + pvals.size());
    int n_sig = 0;
    double ALPHA = 0.05;
    System.out.println("MPO TFT Enrichment");
    System.out.printf("Study set: %d genes. Population set: %d genes\n",
      studysize, popsize);
    for (GoTerm2PValAndCounts item : pvals) {
      double pval = item.getRawPValue();
      double pval_adj = item.getAdjustedPValue();
      TermId tid = item.getItem();
      Term term = ontology.getTermMap().get(tid);
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
      System.out.printf("%s [%s]: %.2e (adjusted %.2e). Study: n=%d/%d (%.1f%%); population: N=%d/%d (%.1f%%)\n",
        label, tid.getValue(), pval, pval_adj, item.getAnnotatedStudyGenes(), studysize, studypercentage,
        item.getAnnotatedPopulationGenes(), popsize, poppercentage);
    }
    System.out.printf("%d of %d terms were significant at alpha %.7f\n",
      n_sig, pvals.size(), ALPHA);


  }


  private void initSymbol2termidMap() {
    symbol2termidMap = new HashMap<>();
    for (MpGeneticMarker gene : this.markermap.values()) {
      String symbol = gene.getGeneSymbol();
      TermId id = gene.getMgiGeneId();
      symbol2termidMap.put(symbol, id);
    }
  }

  /**
   * Retrieve the study set from a file that has one gene symbol per line.
   *
   * @return Set of TermIds representing the studu set.
   */
  private Set<TermId> getStudySet() {
    initSymbol2termidMap();
    ImmutableSet.Builder<TermId> builder = new ImmutableSet.Builder<>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(targetgenefile));
      String line;
      while ((line = br.readLine()) != null) {
        //System.out.println(line);
        if (symbol2termidMap.containsKey(line.trim())) {
          TermId id = symbol2termidMap.get(line.trim());
          builder.add(id);
        }
      }
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return builder.build();
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


}
