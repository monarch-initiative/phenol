package org.monarchinitiative.phenol.io.obo.hpo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.io.obo.OboOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.File;

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


  public HpoOntology parse() throws PhenolException {
    Ontology ontology;

    final OboOntologyLoader loader = new OboOntologyLoader(oboFile);
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
