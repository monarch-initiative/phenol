package org.monarchinitiative.phenol.io.obo.mpo;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.generic.Relationship;
import org.monarchinitiative.phenol.formats.generic.Term;
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


  public Ontology<Term, Relationship> parse() {
    Ontology<Term, Relationship> ontology=null;
    final OwlImmutableOntologyLoader<Term, Relationship> loader =
      new OwlImmutableOntologyLoader<>(oboFile);
    final GenericOwlFactory cof = new GenericOwlFactory();
    try {
      ontology= loader.load(cof);
    } catch (OWLOntologyCreationException | IOException e) {
       e.printStackTrace();
    }
    return ontology;
  }


}
