package org.monarchinitiative.phenol.cli.demo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.annotations.io.hpo.DiseaseDatabase;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseAnnotationLoader;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.getAncestorTerms;

/**
 * A demonstration of how to access data in the human phenotype ontology and its annotation files.
 */
public class HpDemo {



  /** Path to the hp.obo file */
  private final String hpoPath;
  /** Path to the phenotyoe.hpoa file. */
  private final String annotPath;



  public HpDemo(String hpoPath, String annotPath) {
    this.hpoPath = hpoPath;
    this.annotPath = annotPath;
  }

  private void showAncestorsOfTerm(Ontology hpo, TermId tid1, TermId tid2) {
    boolean includeOriginalTerm = true;
    Set<TermId> ancestorsOfTid1 = getAncestorTerms(hpo, tid1, includeOriginalTerm);
    Optional<String> opt = hpo.getTermLabel(tid1);
    if (opt.isEmpty()) {
      System.err.println("Could not find label for " + tid1.getValue());
      return;
    }
    System.out.printf("Ancestors of %s (%s)", opt.get(), tid1.getValue());
    for (TermId anc: ancestorsOfTid1) {
      opt = hpo.getTermLabel(anc);
      if (opt.isEmpty()) {
        System.err.println("COuld not find label for " + anc.getValue());
        continue;
      }
      String label = opt.get();
      System.out.printf("\t%s (%s)\n", label, anc.getValue());
    }
    // TODO , show getting all ancestors of two terms
  }


  private void dumpHpoStats(Ontology hpo) {
    TermId rootTermId = hpo.getRootTermId();
    String rootLabel = hpo.getTermMap().get(rootTermId).getName();
    System.out.println("root term: " + rootLabel + " (" + rootTermId.getValue() + ")");
    TermId lungMorphId = TermId.of("HP:0002088");
    TermId abnBloodGasId = TermId.of("HP:0012415");
    showAncestorsOfTerm(hpo, lungMorphId, abnBloodGasId);



    TermId tid = TermId.of("HP:0001807");
  }


  private void dumpTermStats(Ontology hpo, TermId tid) {
    Term ridgedNail = hpo.getTermMap().get(tid);
    System.out.println("Data contained in term: " + ridgedNail.getName() +" ("+tid.getValue()+")");
    String definition = ridgedNail.getDefinition();
    System.out.println("Definition: "+definition);
    List<TermSynonym> synonyms = ridgedNail.getSynonyms();
    System.out.println("Synonyms:");
    for (TermSynonym syn : synonyms) {
      String val = syn.getValue();
      String scope = syn.getScope().name();
      List<TermXref> xrefs = syn.getTermXrefs();
      String xrefstring = xrefs.stream().map(TermXref::getDescription).collect(Collectors.joining("; "));
      System.out.println("\tval:" + val + ", scope: " + scope + ", xrefs: " + xrefstring);
    }
    System.out.println("Alternative IDs:");
    List<TermId> alternateIds = ridgedNail.getAltTermIds();
    for (TermId altId : alternateIds) {
      System.out.println("\t" + altId.getValue());
    }
    String comment = ridgedNail.getComment();
    System.out.println("Comment: " + comment);
    List<Dbxref> xrefs = ridgedNail.getXrefs();
    System.out.println("Cross references:");
    for (Dbxref xref: xrefs) {
      System.out.println("\t" + xref.getName());
    }
    System.out.println("Database Cross references:");
    List<SimpleXref> databaseXrefs = ridgedNail.getDatabaseXrefs();
    for (SimpleXref dbxref : databaseXrefs) {
      System.out.println("\t" + dbxref.toString());
    }
    List<SimpleXref> pmids = ridgedNail.getPmidXrefs();
    System.out.println("PubMed ids:");
    for (SimpleXref pmid : pmids) {
      System.out.println("\t" + pmid.toString());
    }
    List<String> subsets = ridgedNail.getSubsets();
    System.out.println("Subsets:");
    for (String sset : subsets) {
      System.out.println("\t" + sset);
    }
  }



  public void run() throws IOException {
    Ontology hpo = OntologyLoader.loadOntology(new File(hpoPath));
    dumpHpoStats(hpo);




    HpoDiseases hpoDiseases = HpoDiseaseAnnotationLoader.loadHpoDiseases(Paths.get(annotPath), hpo, Set.of(DiseaseDatabase.OMIM, DiseaseDatabase.ORPHANET, DiseaseDatabase.DECIPHER));
    System.out.println("Imported " + hpoDiseases.size() + " disease models");

  }

}
