package org.monarchinitiative.phenol.io.obo.go;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.monarchinitiative.phenol.formats.go.GoOntology;
import org.monarchinitiative.phenol.io.obo.OboOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public class GoOboParser {

  private final File oboFile;

  private final boolean debug;

  public GoOboParser(File oboFile, boolean debug) {
    this.oboFile = oboFile;
    this.debug = debug;
  }
  public GoOboParser(File oboFile) {
    this(oboFile,false);
  }


  public Optional<GoOntology> parse() {
    Ontology ontology;
    final OboOntologyLoader loader = new OboOntologyLoader(oboFile);
    Optional<Ontology> optOnto = loader.load();
    if (! optOnto.isPresent()) {
      System.err.println("[ERROR] Failed to load GO ontology");
      return Optional.empty();
    } else {
      ontology = optOnto.get();
    }
    if (debug) {
      System.err.println(String.format("Parsed a total of %d MP terms",ontology.countAllTerms()));
    }


    GoOntology onto =  new GoOntology(
      (ImmutableSortedMap<String, String>) ontology.getMetaInfo(),
      ontology.getGraph(),
      ontology.getRootTermId(),
      ontology.getNonObsoleteTermIds(),
      ontology.getObsoleteTermIds(),
      (ImmutableMap<TermId, Term>) ontology.getTermMap(),
      (ImmutableMap<Integer, Relationship>) ontology.getRelationMap());
    return Optional.of(onto);
  }

}
