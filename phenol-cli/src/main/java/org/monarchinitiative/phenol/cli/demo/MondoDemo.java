package org.monarchinitiative.phenol.cli.demo;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.algo.OntologyAlgorithm;
import org.monarchinitiative.phenol.ontology.data.Dbxref;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

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

  private Ontology mondo;
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
    Set<TermId> obsolete = mondo.getObsoleteTermIds();
    for (Map.Entry<TermId, Term> entry : mondo.getTermMap().entrySet()) {
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
      Set<TermId> members = OntologyAlgorithm.getDescendents(mondo, psterm.getId());
      for (TermId member : members) {
        Term candidate = mondo.getTermMap().get(member);
        if (isOmimEntry(candidate)) {
          phenoseries2omimMap.computeIfAbsent(psterm, key -> new HashSet<>())
            .add(candidate.getId());
        }
      }
    }
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(this.outPath));

      for (Term psterm : phenoseries2omimMap.keySet()) {
        Collection<TermId> coll = phenoseries2omimMap.get(psterm);
        for (TermId tid : coll) {
          Term omimentry = mondo.getTermMap().get(tid);
          Optional<TermId> omimIdopt = getOMIMid(omimentry);
          if (omimIdopt.isPresent()) {
            TermId omimId = omimIdopt.get();
            String line = psterm.getId().getValue() + "\t" +
              psterm.getName() + "\t" +
              omimId.getValue() + "\t" +
              omimentry.getName();
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
