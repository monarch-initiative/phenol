package org.monarchinitiative.phenol.cli.demo;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A demonstration of how to access data in the human phenotype ontology and its annotation files.
 */
public class HpDemo {



  /** Path to the hp.obo file */
  private final String hpoPath;
  /** Path to the phenotyoe.hpoa file. */
  private final String annotPath;



  public HpDemo(HpDemo.Options options) {
    this.hpoPath = options.getHpoPath();
    this.annotPath = options.getAnnotationPath();
  }


  public void run() {
    Ontology hpo = OntologyLoader.loadOntology(new File(hpoPath));
    TermId rootTermId = hpo.getRootTermId();
    String rootLabel = hpo.getTermMap().get(rootTermId).getName();

    System.out.println("root term: " + rootLabel + " (" + rootTermId.getValue() + ")");

    TermId tid = TermId.of("HP:0001807");
    Term ridgedNail = hpo.getTermMap().get(tid);
    System.out.println("Data contained in term: " + ridgedNail.getName() +" ("+tid.getValue()+")");
    String definition = ridgedNail.getDefinition();
    System.out.println("Definition: "+definition);
    List<TermSynonym> synonyms = ridgedNail.getSynonyms();
    System.out.println("Synonyms:");
    for (TermSynonym syn : synonyms) {
      String val =syn.getValue();
      String scope = syn.getScope().name();
      List<TermXref> xrefs = syn.getTermXrefs();
      String xrefstring = xrefs.stream().map(TermXref::getDescription).collect(Collectors.joining("; "));
      System.out.println("\tval:"+val +", scope: "+scope + ", xrefs: " + xrefstring);
    }
    List<TermId> alternateIds = ridgedNail.getAltTermIds();
    String comment = ridgedNail.getComment();
    List<Dbxref> xrefs = ridgedNail.getXrefs();
    List<SimpleXref> databaseXrefs = ridgedNail.getDatabaseXrefs();
    List<SimpleXref> pmids = ridgedNail.getPmidXrefs();
    List<String> subsets = ridgedNail.getSubsets();

  }




  @Parameters(commandDescription = "Human Phenotype Ontology demo")
  public static class Options {
    @Parameter(names = {"-o","--obo"}, description = "path to hp.obo file", required = true)
    private String hpoPath;
    @Parameter(names = {"-a","--annot"}, description = "path to HPO annotation file (phenotyoe.hpoa", required = true)
    private String annotPath;


    String getHpoPath() {
      return hpoPath;
    }

    String getAnnotationPath() {
      return annotPath;
    }

  }

}
