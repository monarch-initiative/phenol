package org.monarchinitiative.phenol.annotations.io.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.TestBase;
import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalPoint;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Note that each {@link HpoDisease} test only loads phenotype annotations that are present in small HPO file
 * - the ancestors of <em>Arachnodactyly</em>.
 */
public class HpoDiseaseLoaderDefaultTest {

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
    instance = HpoDiseaseLoaders.defaultLoader(HPO, HpoDiseaseLoaderOptions.defaultOptions());
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

    assertThat(omimSyndrome.annotationCount(), equalTo(2));
  }

  @Test
  public void checkPhenotypeTerms() throws Exception {
    HpoDiseases hpoDiseases = instance.load(HPOA);

    Optional<HpoDisease> omimSyndromeOptional = hpoDiseases.diseaseById(TermId.of("OMIM:987654"));
    assertThat(omimSyndromeOptional.isPresent(), equalTo(true));

    HpoDisease disease = omimSyndromeOptional.get();
    List<HpoDiseaseAnnotation> annotations = disease.annotationStream()
      .sorted(HpoDiseaseAnnotation::compareById)
      .collect(Collectors.toList());

    // first HpoDiseaseAnnotation
    HpoDiseaseAnnotation first = annotations.get(0);
    assertThat(first.id().getValue(), equalTo("HP:0001167"));
    assertThat(first.isPresent(), equalTo(true));
    assertThat(first.ratio(), equalTo(Ratio.of(5, 13)));

    Optional<TemporalPoint> earliestOnset = first.earliestOnset();
    assertThat(earliestOnset.isPresent(), equalTo(true));
    TemporalPoint onset = earliestOnset.get();
    assertThat(onset.days(), equalTo(29));
    assertThat(onset.isPostnatal(), equalTo(true));

    assertThat(first.references(), hasSize(2));
    String referenceString = first.references().stream().map(AnnotationReference::id)
      .map(TermId::getValue)
      .sorted()
      .collect(Collectors.joining(";"));
    assertThat(referenceString, equalTo("PMID:20375004;PMID:22736615"));

    // second HpoDiseaseAnnotation
    HpoDiseaseAnnotation second = annotations.get(1);
    assertThat(second.id().getValue(), equalTo("HP:0001238"));
    assertThat(second.isPresent(), equalTo(false));
    assertThat(second.ratio(), equalTo(Ratio.of(0, 50)));

    earliestOnset = second.earliestOnset();
    assertThat(earliestOnset.isPresent(), equalTo(false));

    assertThat(second.references(), hasSize(1));
    assertThat(second.references().get(0).id().getValue(), equalTo("PMID:20375004"));
  }
}
