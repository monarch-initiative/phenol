package org.monarchinitiative.phenol.annotations.io.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.TestBase;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Note that each {@link HpoDisease} test only loads phenotype annotations that are present in small HPO file
 * - the ancestors of <em>Arachnodactyly</em>.
 */
public class HpoDiseaseLoaderTest {

  private static final Path HPOA = TestBase.TEST_BASE.resolve("annotations").resolve("phenotype.fake.hpoa");
  private static final Path HPO_PATH = TestBase.TEST_BASE.resolve("hpo_toy.json");

  private static Ontology HPO;

  private HpoDiseaseLoader instance;

  @BeforeAll
  public static void beforeAll() throws Exception {
    HPO = OntologyLoader.loadOntology(HPO_PATH.toFile());
  }

  @BeforeEach
  public void setUp() {
    instance = HpoDiseaseLoader.of(HPO);
  }

  @Test
  public void testPresenceOfExpectedDiseaseIds() throws Exception {
    HpoDiseases hpoDiseases = instance.load(HPOA);

    assertThat(hpoDiseases.diseaseIds(), hasSize(2));
    assertThat(hpoDiseases.diseaseIds(), hasItems(TermId.of("OMIM:987654"), TermId.of("ORPHA:123456")));
  }

  @Test
  public void testPresenceOfExpectedPhenotypesInOmimSyndrome() throws Exception {
    HpoDiseases hpoDiseases = instance.load(HPOA);
    Optional<HpoDisease> omimSyndromeOptional = hpoDiseases.diseaseById(TermId.of("OMIM:987654"));

    assertThat(omimSyndromeOptional.isPresent(), equalTo(true));

    HpoDisease omimSyndrome = omimSyndromeOptional.get();

    assertThat(omimSyndrome.phenotypicAbnormalitiesCount(), equalTo(2));
    // TODO - check phenotype terms
  }

}
