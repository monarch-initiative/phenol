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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HpOboParser {

  private static final Logger logger = LoggerFactory.getLogger(HpOboParser.class);

  private final InputStream obo;

  public HpOboParser(File oboFile) throws FileNotFoundException {
    this.obo = new FileInputStream(oboFile);
  }

  public HpOboParser(InputStream obo) {
    this.obo = obo;
  }

  public HpoOntology parse() throws PhenolException {
    Ontology ontology;

    logger.debug("Loading HPO");
    final OboOntologyLoader loader = new OboOntologyLoader(obo);
    ontology = loader.load();

    logger.debug("Parsed a total of {} HP terms", ontology.countAllTerms());

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
