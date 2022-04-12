package org.monarchinitiative.phenol.cli.demo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.annotations.io.hpo.DiseaseDatabase;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseAnnotationLoader;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm.*;

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

  private enum Relation {
    ANCESTOR("ancestor"),
    CHILD("child"),
    DESCENDENT("descendent"),
    PARENT("parent");

    private final String name;

    Relation(String n) {
      this.name = n;
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  private void showRelationsOfTerm(Ontology hpo, TermId tid, Relation relation) {
    boolean includeOriginalTerm = true;
    Set<TermId> relationsOfTid;
    String descriptor;
    switch (relation) {
      case ANCESTOR:
        descriptor = "Ancestors";
        relationsOfTid = getAncestorTerms(hpo, tid, includeOriginalTerm);
        break;
      case DESCENDENT:
        descriptor = "Descendents";
        relationsOfTid = getDescendents(hpo, tid);
        break;
      case CHILD:
        descriptor = "Children";
        relationsOfTid = getChildTerms(hpo, tid, includeOriginalTerm);
        break;
      case PARENT:
        descriptor = "Parents";
        relationsOfTid = getParentTerms(hpo, tid, includeOriginalTerm);
        break;
      default:
        return;
    }
    Optional<String> opt = hpo.getTermLabel(tid);
    if (opt.isEmpty()) {
      System.err.println("Could not find label for " + tid.getValue());
      return;
    }
    System.out.printf("%s of %s (%s):\n", descriptor, opt.get(), tid.getValue());
    for (TermId rel: relationsOfTid) {
      opt = hpo.getTermLabel(rel);
      if (opt.isEmpty()) {
        System.err.println("Could not find label for " + rel.getValue());
        continue;
      }
      String label = opt.get();
      System.out.printf("\t%s (%s)\n", label, rel.getValue());
    }
  }

  private void showRelationsOfTerms(Ontology hpo, List<TermId> tids, Relation relation) {
    for (TermId tid : tids) {
      showRelationsOfTerm(hpo, tid, relation);
    }
  }

  private void showCommonAncestorsOfTerms(Ontology hpo, TermId tid1, TermId tid2) {
    Set<TermId> commonAncestors = hpo.getCommonAncestors(tid1, tid2);
    Optional<String> opt1 = hpo.getTermLabel(tid1);
    Optional<String> opt2 = hpo.getTermLabel(tid2);
    if (opt1.isEmpty()) {
      System.err.println("Could not find label for " + tid1.getValue());
      return;
    }
    if (opt2.isEmpty()) {
      System.err.println("Could not find label for " + tid2.getValue());
      return;
    }
    System.out.printf("Common Ancestors of %s (%s) and %s (%s):\n", opt1.get(), tid1.getValue(), opt2.get(), tid2.getValue());
    for (TermId anc: commonAncestors) {
      Optional<String> opt = hpo.getTermLabel(anc);
      if (opt.isEmpty()) {
        System.err.println("Could not find label for " + anc.getValue());
        continue;
      }
      String label = opt.get();
      System.out.printf("\t%s (%s)\n", label, anc.getValue());
    }
  }

  private void showAllAncestorsOfTerms(Ontology hpo, List<TermId> tids) {
    Set<TermId> allAncestors = hpo.getAllAncestorTermIds(tids);
    StringBuilder tidStr = new StringBuilder();
    for (TermId tid: tids) {
      Optional<String> opt = hpo.getTermLabel(tid);
      if (opt.isEmpty()) {
        System.err.println("Could not find label for " + tid.getValue());
        return;
      }
      tidStr.append(String.format("%s (%s), ", opt.get(), tid.getValue()));
    }
    System.out.printf("All Ancestors of %s:\n", tidStr.substring(0, tidStr.length()-2));
    for (TermId anc: allAncestors) {
      Optional<String> opt = hpo.getTermLabel(anc);
      if (opt.isEmpty()) {
        System.err.println("Could not find label for " + anc.getValue());
        continue;
      }
      String label = opt.get();
      System.out.printf("\t%s (%s)\n", label, anc.getValue());
    }
  }


  private void dumpHpoStats(Ontology hpo) {
    TermId rootTermId = hpo.getRootTermId();
    String rootLabel = hpo.getTermMap().get(rootTermId).getName();
    System.out.println("root term: " + rootLabel + " (" + rootTermId.getValue() + ")");
    TermId lungMorphId = TermId.of("HP:0002088");
    TermId abnBloodGasId = TermId.of("HP:0012415");
    List<TermId> tidList = new ArrayList<>();
    tidList.add(lungMorphId);
    tidList.add(abnBloodGasId);
    showRelationsOfTerms(hpo, tidList, Relation.ANCESTOR);
    showCommonAncestorsOfTerms(hpo, lungMorphId, abnBloodGasId);
    showAllAncestorsOfTerms(hpo, tidList);
    showRelationsOfTerms(hpo, tidList, Relation.DESCENDENT);
    showRelationsOfTerms(hpo, tidList, Relation.CHILD);
    showRelationsOfTerms(hpo, tidList, Relation.PARENT);



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
