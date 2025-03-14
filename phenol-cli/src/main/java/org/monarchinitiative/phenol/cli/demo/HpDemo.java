package org.monarchinitiative.phenol.cli.demo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.annotations.io.hpo.DiseaseDatabase;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoader;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoaderOptions;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseLoaders;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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


  public void run() throws IOException {
    Ontology hpo = OntologyLoader.loadOntology(new File(hpoPath));
    TermId rootTermId = hpo.getRootTermId();
    String rootLabel = hpo.termForTermId(rootTermId).map(Term::getName).orElse(null);

    System.out.println("root term: " + rootLabel + " (" + rootTermId.getValue() + ")");

    TermId tid = TermId.of("HP:0001807");
    Optional<Term> ridgedNail = hpo.termForTermId(tid);
    System.out.println("Data contained in term: " + ridgedNail.map(Term::getName).orElse(null) +" ("+tid.getValue()+")");
    String definition = ridgedNail.map(Term::getDefinition).orElse(null);
    System.out.println("Definition: "+definition);
    List<TermSynonym> synonyms = ridgedNail.map(Term::getSynonyms).orElse(List.of());
    System.out.println("Synonyms:");
    for (TermSynonym syn : synonyms) {
      String val = syn.getValue();
      String scope = syn.getScope().name();
      List<TermXref> xrefs = syn.getTermXrefs();
      String xrefstring = xrefs.stream().map(TermXref::getDescription).collect(Collectors.joining("; "));
      System.out.println("\tval:" + val + ", scope: " + scope + ", xrefs: " + xrefstring);
    }
    System.out.println("Alternative IDs:");
    List<TermId> alternateIds = ridgedNail.map(Term::getAltTermIds).orElse(List.of());
    for (TermId altId : alternateIds) {
      System.out.println("\t" + altId.getValue());
    }
    String comment = ridgedNail.map(Term::getComment).orElse(null);
    System.out.println("Comment: " + comment);
    List<Dbxref> xrefs = ridgedNail.map(Term::getXrefs).orElse(List.of());
    System.out.println("Cross references:");
    for (Dbxref xref: xrefs) {
      System.out.println("\t" + xref.getName());
    }
    System.out.println("Database Cross references:");
    List<SimpleXref> databaseXrefs = ridgedNail.map(Term::getDatabaseXrefs).orElse(List.of());
    for (SimpleXref dbxref : databaseXrefs) {
      System.out.println("\t" + dbxref.toString());
    }
    List<SimpleXref> pmids = ridgedNail.map(Term::getPmidXrefs).orElse(List.of());
    System.out.println("PubMed ids:");
    for (SimpleXref pmid : pmids) {
      System.out.println("\t" + pmid.toString());
    }
    List<String> subsets = ridgedNail.map(Term::getSubsets).orElse(List.of());
    System.out.println("Subsets:");
    for (String sset : subsets) {
      System.out.println("\t" + sset);
    }

    HpoDiseaseLoader loader = HpoDiseaseLoaders.defaultLoader(hpo, HpoDiseaseLoaderOptions.of(Set.of(DiseaseDatabase.OMIM, DiseaseDatabase.ORPHANET, DiseaseDatabase.DECIPHER), true, HpoDiseaseLoaderOptions.DEFAULT_COHORT_SIZE));
    HpoDiseases hpoDiseases = loader.load(Paths.get(annotPath));
    System.out.println("Imported " + hpoDiseases.size() + " disease models");

  }

}
