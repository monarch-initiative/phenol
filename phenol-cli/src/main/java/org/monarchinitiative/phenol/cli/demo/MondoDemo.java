package org.monarchinitiative.phenol.cli.demo;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Dbxref;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.util.*;

/**
 * A demonstration of how to access data in MONDO. We will extract a list of all OMIM diseases that are members of
 * a given phenoseries
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class MondoDemo {
  private final String mondoPath;

  private Ontology mondo;
  private List<Term> phenoseriesRoots;
  private Multimap<Term,TermId> phenoseries2omimMap;


  public MondoDemo(MondoDemo.Options options){
    mondoPath = options.getMondoPath();
    phenoseriesRoots = new ArrayList<>();
    phenoseries2omimMap = ArrayListMultimap.create();
  }


  // todo  -- also check if is exact match
  boolean isOmimEntry(Term term) {
    for (Dbxref xref : term.getXrefs()) {
      if (xref.getName().startsWith("OMIM") )
        return true;
    }
    return false;
  }


  public void run() {
    File file = new File(mondoPath);
    if (! file.exists()) {
      System.err.print("[ERROR] Could not find mondo.obo at " + mondoPath);
      return;
    }
    mondo = OntologyLoader.loadOntology(file);
    Set<TermId> obsolete = mondo.getObsoleteTermIds();
    for (Map.Entry<TermId,Term> entry  : mondo.getTermMap().entrySet()) {
      if (obsolete.contains(entry.getKey())) {
        continue;
      }
      Term term = entry.getValue();
      for (Dbxref xref : term.getXrefs()) {
        if (xref.getName().startsWith("OMIMPS"))
          phenoseriesRoots.add(term);

      }
    }
    for (Term psterm : phenoseriesRoots) {
      Set<TermId> members = OntologyAlgorithm.getDescendents(mondo,psterm.getId());
      for (TermId member : members) {
        Term candidate = mondo.getTermMap().get(member);
        if (isOmimEntry(candidate)) {
          phenoseries2omimMap.put(psterm,candidate.getId());
        }
      }
    }
    for (Term psterm :  phenoseries2omimMap.keySet()) {
      Collection<TermId> coll = phenoseries2omimMap.get(psterm);
      for (TermId tid : coll) {
        Term omimentry = mondo.getTermMap().get(tid);
        System.out.println(psterm.getName() + " -> " + omimentry.getName());
      }
    }
  }

  @Parameters(commandDescription = "MONDO demo")
  public static class Options {
    @Parameter(names = {"-o","--obo"}, description = "path to mondo.obo file", required = true)
    private String mondoPath;


    String getMondoPath() {
      return mondoPath;
    }

  }
}
