package org.monarchinitiative.phenol.io.obo.go;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.monarchinitiative.phenol.base.PhenolException;
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


  public GoOntology parse() throws PhenolException {
    final OboOntologyLoader loader = new OboOntologyLoader(oboFile);
    Ontology ontology = loader.load();
    if (debug) {
      System.err.println(String.format("Parsed a total of %d MP terms",ontology.countAllTerms()));
    }

    return new GoOntology(
      (ImmutableSortedMap<String, String>) ontology.getMetaInfo(),
      ontology.getGraph(),
      ontology.getRootTermId(),
      ontology.getNonObsoleteTermIds(),
      ontology.getObsoleteTermIds(),
      (ImmutableMap<TermId, Term>) ontology.getTermMap(),
      (ImmutableMap<Integer, Relationship>) ontology.getRelationMap());
  }

}
