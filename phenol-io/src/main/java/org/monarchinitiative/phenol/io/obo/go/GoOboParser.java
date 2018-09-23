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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class GoOboParser {

  private final InputStream obo;

  private final boolean debug;

  public GoOboParser(File oboFile, boolean debug) throws FileNotFoundException {
    this.obo = new FileInputStream(oboFile);
    this.debug = debug;
  }

  public GoOboParser(File oboFile) throws FileNotFoundException {
    this(oboFile,false);
  }

  public GoOboParser(InputStream obo, boolean debug) {
    this.obo = obo;
    this.debug = debug;
  }

  public GoOboParser(String path) throws FileNotFoundException {
    this(new File(path),false);
  }

  public GoOboParser(InputStream obo) {
    this(obo,false);
  }

  public GoOntology parse() throws PhenolException {
    final OboOntologyLoader loader = new OboOntologyLoader(obo);
    Ontology ontology = loader.load();
    if (debug) {
      System.err.println(String.format("Parsed a total of %d GO terms",ontology.countAllTerms()));
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
