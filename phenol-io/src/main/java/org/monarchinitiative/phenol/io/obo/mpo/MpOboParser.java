package org.monarchinitiative.phenol.io.obo.mpo;


import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.obo.OboOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class MpOboParser {

  private final InputStream obo;

  private final boolean debug;

  public MpOboParser(File oboFile, boolean debug) throws FileNotFoundException {
      this.obo = new FileInputStream(oboFile);
      this.debug = debug;
  }
  
  public MpOboParser(File oboFile) throws FileNotFoundException {
    this(oboFile,false);
  }
  
  public MpOboParser(InputStream obo, boolean debug) {
    this.obo = obo;
    this.debug = debug;
  }
  
  public MpOboParser(InputStream obo) {
    this(obo,false);
  }

  public Ontology parse() throws PhenolException {
    final OboOntologyLoader loader = new OboOntologyLoader(obo);
    Ontology ontology = loader.load();
    if (debug) {
        System.err.println(String.format("Parsed a total of %d MP terms",ontology.countAllTerms()));
    }
    return ontology;
  }
}
