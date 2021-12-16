package org.monarchinitiative.phenol.io;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * The class serves for extracting terms for a small HP ontology JSON <em>hpo_toy.json</em> containing ancestors of
 * <a href="https://hpo.jax.org/app/browse/term/HP:0001166">Arachnodactyly</a> for the
 * <a href="https://hpo.jax.org/app/browse/term/HP:0000118">Phenotypic abnormality</a> sub-hierarchy, and all the other
 * sub-hierarchies of <em>All</em>, including:
 * <ul>
 *   <li>Clinical modifier</li>
 *   <li>Mode of inheritance</li>
 *   <li>Past medical history</li>
 *   <li>Blood group</li>
 *   <li>Frequency</li>
 * </ul>
 *
 * <p>
 * As Surefire warns regarding disabled tests during build, the JUnit test annotation is commented out to disable
 * the warning. Run the test manually if necessary.
 */
public class PrepareToyHpoJsonTest {

//  @Test
  public void name() throws Exception {
    // this must be replaced with path to a proper HPO json file
    File hpoJsonFile = null;
    Ontology ontology = OntologyLoader.loadOntology(hpoJsonFile);
    Set<TermId> termsOfInterest = new HashSet<>();

    // arachnodactyly
    TermId arachnodactylyTermId = TermId.of("HP:0001166");
    Set<TermId> arachnodactylyAncestors = ontology.getAncestorTermIds(arachnodactylyTermId);
    termsOfInterest.addAll(arachnodactylyAncestors);

    // sub-ontologies
    termsOfInterest.addAll(subOntologyTerms(ontology, TermId.of("HP:0012823"))); // clinical modifier
    termsOfInterest.addAll(subOntologyTerms(ontology, TermId.of("HP:0000005"))); // mode of inheritance
    termsOfInterest.addAll(subOntologyTerms(ontology, TermId.of("HP:0032443"))); // past medical history
    termsOfInterest.addAll(subOntologyTerms(ontology, TermId.of("HP:0032223"))); // blood group
    termsOfInterest.addAll(subOntologyTerms(ontology, TermId.of("HP:0040279"))); // frequency

    // write out the terms
    BufferedWriter writer = Files.newBufferedWriter(Paths.get("terms.tsv"));
    for (TermId termId : termsOfInterest) {
      writer.write(termId.getValue() + '\n');
    }
    writer.close();
  }

  private static Set<TermId> subOntologyTerms(Ontology ontology, TermId subRoot) {
    Ontology subOntology = ontology.subOntology(subRoot);
    return subOntology.getTermMap().keySet();
  }
}
