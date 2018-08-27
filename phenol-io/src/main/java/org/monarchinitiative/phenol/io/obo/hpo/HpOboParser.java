package org.monarchinitiative.phenol.io.obo.hpo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.OboOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

public class HpOboParser {

  private final InputStream obo;

  private final boolean debug;

  public HpOboParser(File oboFile, boolean debug) throws FileNotFoundException {
    this.obo = new FileInputStream(oboFile);
    this.debug = debug;
  }
  
  public HpOboParser(File oboFile) throws FileNotFoundException {
    this(oboFile,false);
  }
  
  public HpOboParser(InputStream obo, boolean debug) {
    this.obo = obo;
    this.debug = debug;
  }
  
  public HpOboParser(InputStream obo) {
    this(obo,false);
  }

  public HpoOntology parse() throws PhenolException {
    Ontology ontology;

    final OboOntologyLoader loader = new OboOntologyLoader(obo);
    ontology = loader.load();
    if (debug) {
      System.err.println(String.format("Parsed a total of %d HP terms",ontology.countAllTerms()));
    }

    return new HpoOntology(
      (ImmutableSortedMap<String, String>) ontology.getMetaInfo(),
      ontology.getGraph(),
      ontology.getRootTermId(),
      ontology.getNonObsoleteTermIds(),
      ontology.getObsoleteTermIds(),
      (ImmutableMap<TermId, Term>) ontology.getTermMap(),
      (ImmutableMap<Integer, Relationship>) ontology.getRelationMap());
  }



}
