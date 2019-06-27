package org.monarchinitiative.phenol.cli.demo;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Dbxref;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A demonstration of how to access data in MONDO. We will extract a list of all OMIM diseases that are members of
 * a given phenoseries
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class MondoDemo {
  private final String mondoPath;

  private Ontology mondo;
  private List<Term> phenoseriesRoots;


  public MondoDemo(MondoDemo.Options options){
    mondoPath = options.getMondoPath();
    phenoseriesRoots = new ArrayList<>();
  }

  boolean isOmimEntry(Term term) {
    for (Dbxref xref : term.getXrefs()) {
      if (xref.getName().startsWith("OMIM") )
        System.out.print("OMIM " + xref.toString());
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

        }
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
