package org.monarchinitiative.phenol.io.benches;

import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.impl.SimpleMinimalOntology;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@State(Scope.Thread)
public class CsrPolyOntologyGraphSetup extends OntologyGraphSetup {

  private static final Random RANDOM = new Random(500);

  MinimalOntology ontology;
  List<TermId> primaryTermIds;

  @Setup(Level.Trial)
  public void prepare() {
    Path hpoPath = SetupHpo.hpoJsonPath(Constants.HPO_VERSION);

    ontology = loadOntology(hpoPath, SimpleMinimalOntology.Builder.GraphImplementation.POLY);

    primaryTermIds = ontology.getTerms().stream()
      .map(Term::id)
      .collect(Collectors.toList());

    // ensure the primary term IDs are not sorted if that's what an ontology does.
    Collections.shuffle(primaryTermIds, RANDOM);
  }

}
