package org.monarchinitiative.phenol.io.obo.hpo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.OboOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;
import java.util.Optional;

public class HpOboParser {

  private final File oboFile;

  private final boolean debug;

  public HpOboParser(File oboFile, boolean debug) {
    this.oboFile = oboFile;
    this.debug = debug;
  }
  public HpOboParser(File oboFile) {
    this(oboFile,false);
  }


  public Optional<HpoOntology> parse() {
    Ontology ontology;


    final OboOntologyLoader loader = new OboOntologyLoader(oboFile);
    Optional<Ontology> optOnto = loader.load();
    if (! optOnto.isPresent()) {
      System.err.println("[ERROR] Failed to load HPO ontology");
      return Optional.empty();
    } else {
      ontology = optOnto.get();
    }
    if (debug) {
      System.err.println(String.format("Parsed a total of %d HP terms",ontology.countAllTerms()));
    }


    HpoOntology onto =  new HpoOntology(
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
