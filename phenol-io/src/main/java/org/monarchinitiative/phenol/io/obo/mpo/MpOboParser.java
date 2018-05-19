package org.monarchinitiative.phenol.io.obo.mpo;


import org.monarchinitiative.phenol.io.owl.OwlImmutableOntologyLoader;
import org.monarchinitiative.phenol.io.owl.generic.GenericOwlFactory;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.io.IOException;

public class MpOboParser {

  private final File oboFile;

  private final boolean debug;

  public MpOboParser(File oboFile, boolean debug) {
    this.oboFile = oboFile;
    this.debug = debug;
  }
  public MpOboParser(File oboFile) {
    this(oboFile,false);
  }


  public Ontology parse() {
    Ontology ontology=null;
    final OwlImmutableOntologyLoader loader =
      new OwlImmutableOntologyLoader(oboFile);
    final GenericOwlFactory cof = new GenericOwlFactory();
    try {
      ontology= loader.load(cof);
      if (debug) {
        System.err.println(String.format("Parsed a total of %d MP terms",ontology.countAllTerms()));
      }
    } catch (OWLOntologyCreationException | IOException e) {
       e.printStackTrace();
    }
    return ontology;
  }


}
