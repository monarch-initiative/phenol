package org.monarchinitiative.phenol.io.obo.mpo;


import org.monarchinitiative.phenol.io.obo.OboOntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;
import java.util.Optional;

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
    Ontology ontology;
    final OboOntologyLoader loader = new OboOntologyLoader(oboFile);
    Optional<Ontology> optOnto = loader.load();
    if (! optOnto.isPresent()) {
      System.err.println("[ERROR] Failed to load ontology");
      return null; // TODO return optional.
    } else {
      ontology = optOnto.get();
    }
    if (debug) {
        System.err.println(String.format("Parsed a total of %d MP terms",ontology.countAllTerms()));
    }
    return ontology;
  }


}
