package org.monarchinitiative.phenol.annotations.io.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoAnnotation;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoOnset;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Note that each {@link HpoDisease} test only loads phenotype annotations that are present in small HPO file
 * - the ancestors of <em>Arachnodactyly</em>.
 */
public class HpoDiseaseAnnotationLoaderTest {

  private static HpoDiseases hpoDiseases;

  @BeforeAll
  public static void beforeAll() throws Exception {
    Path hpoaPath = Path.of(HpoDiseaseAnnotationLoaderTest.class.getResource("/annotations/phenotype.excerpt.hpoa").getPath());

    Path hpoPath = Path.of(HpoDiseaseAnnotationLoaderTest.class.getResource("/hpo_toy.json").getPath());
    Ontology hpoOntology = OntologyLoader.loadOntology(hpoPath.toFile());

    hpoDiseases = HpoDiseaseAnnotationLoader.loadHpoDiseases(hpoaPath, hpoOntology, DiseaseDatabase.allKnownDiseaseDatabases());
  }

  @Test
  public void testPresenceOfExpectedDiseaseIds() {
    Map<TermId, HpoDisease> diseaseById = hpoDiseases.diseaseById();
    hpoDiseases.hpoDiseases().forEach(System.err::println);

    assertThat(diseaseById.keySet(), hasSize(13));
    assertThat(diseaseById.keySet(), hasItems(expectedDiseaseIds()));
  }

  @Test
  public void testPresenceOfExpectedPhenotypesInMarfanSyndrome() {
    HpoDisease marfanSyndrome = hpoDiseases.diseaseById().get(TermId.of("OMIM:154700"));

    // Only the annotations with term IDs present in the small HPO file are parsed.
    assertThat(marfanSyndrome.getNumberOfPhenotypeAnnotations(), equalTo(1));
    HpoAnnotation expected = HpoAnnotation.builder(TermId.of("HP:0001166"))
      .frequency(0.6294416243654822)
      .onset(HpoOnset.UNKNOWN)
      .build();
    assertThat(marfanSyndrome.getPhenotypicAbnormalities(), hasItem(expected));

  }

  private static TermId[] expectedDiseaseIds() {
    return new TermId[]{
      TermId.of("OMIM:142900"),
      TermId.of("OMIM:540000"),
      TermId.of("OMIM:608328"),
      TermId.of("OMIM:154700"),
      TermId.of("OMIM:604308"),
      TermId.of("OMIM:614185"),
      TermId.of("OMIM:143890"),
      TermId.of("OMIM:100300"),
      TermId.of("OMIM:102370"),
      TermId.of("OMIM:616914"),
      TermId.of("OMIM:129600"),
      TermId.of("OMIM:184900"),
      TermId.of("ORPHA:166035"),
    };
  }
}
