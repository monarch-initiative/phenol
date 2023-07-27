package org.monarchinitiative.phenol.cli.demo;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * A demonstration of how to access data in MONDO. We will extract a list of all OMIM diseases that are members of
 * a given phenoseries
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class MondoDemo {
  private final String mondoPath;
  private final String outPath;

  private MinimalOntology mondo;
  private List<Term> phenoseriesRoots;
  private Map<Term, Collection<TermId>> phenoseries2omimMap;


  public MondoDemo(String mondoPath, String out) {
    this.mondoPath = mondoPath;
    this.outPath = out;
    phenoseriesRoots = new ArrayList<>();
    phenoseries2omimMap = new HashMap<>();
  }


  // todo  -- also check if is exact match
  private boolean isOmimEntry(Term term) {
    for (Dbxref xref : term.getXrefs()) {
      if (xref.getName().startsWith("OMIM"))
        return true;
    }
    return false;
  }

  private Optional<TermId> getOMIMid(Term term) {
    for (Dbxref xref : term.getXrefs()) {
      if (xref.getName().startsWith("OMIM") && !xref.getName().startsWith("OMIMPS"))
        return Optional.of(TermId.of(xref.getName()));
    }
    return Optional.empty();
  }


  public void run() {
    File file = new File(mondoPath);
    if (!file.exists()) {
      System.err.print("[ERROR] Could not find mondo.obo at " + mondoPath);
      return;
    }
    mondo = OntologyLoader.loadOntology(file);
    for (Term term : mondo.getTerms()) {
      for (Dbxref xref : term.getXrefs()) {
        if (xref.getName().startsWith("OMIMPS"))
          phenoseriesRoots.add(term);

      }
    }
    for (Term psterm : phenoseriesRoots) {
      for (TermId member : mondo.graph().getDescendants(psterm.id(), true)) {
        Optional<Term> candidate = mondo.termForTermId(member);
        if (candidate.isPresent() && isOmimEntry(candidate.get())) {
          phenoseries2omimMap.computeIfAbsent(psterm, key -> new HashSet<>())
            .add(candidate.get().id());
        }
      }
    }
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(this.outPath));

      for (Term psterm : phenoseries2omimMap.keySet()) {
        Collection<TermId> coll = phenoseries2omimMap.get(psterm);
        for (TermId tid : coll) {
          Optional<Term> omimentry = mondo.termForTermId(tid);
          if (omimentry.isEmpty())
            continue;

          Optional<TermId> omimIdopt = getOMIMid(omimentry.get());
          if (omimIdopt.isPresent()) {
            TermId omimId = omimIdopt.get();
            String line = psterm.id().getValue() + "\t" +
              psterm.getName() + "\t" +
              omimId.getValue() + "\t" +
              omimentry.get().getName();
            System.out.println(line);
            writer.write(line + "\n");
          }
        }
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
