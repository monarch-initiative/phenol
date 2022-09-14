package org.monarchinitiative.phenol.annotations.io.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.TestBase;
import org.monarchinitiative.phenol.annotations.base.temporal.PointInTime;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoOnset;
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
  private static final HpoDiseaseLoaderOptions OPTIONS = HpoDiseaseLoaderOptions.defaultOptions();

  private static Ontology HPO;

  private HpoDiseaseLoaderDefault instance;

  @BeforeAll
  public static void beforeAll() {
    HPO = OntologyLoader.loadOntology(HPO_PATH.toFile());
  }

  @BeforeEach
  public void setUp() {
    instance = new HpoDiseaseLoaderDefault(HPO, OPTIONS);
  }

  @Test
  public void testPresenceOfExpectedDiseaseIds() throws Exception {
    HpoDiseases hpoDiseases = instance.load(HPOA);

    assertThat(hpoDiseases.diseaseIds(), hasSize(3));
    assertThat(hpoDiseases.diseaseIds(), hasItems(TermId.of("OMIM:987654"), TermId.of("ORPHA:123456"), TermId.of("OMIM:111111")));
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Test
  public void testPresenceOfExpectedPhenotypesInOmimSyndrome() throws Exception {
    HpoDiseases hpoDiseases = instance.load(HPOA);
    Optional<HpoDisease> omimSyndromeOptional = hpoDiseases.diseaseById(TermId.of("OMIM:987654"));

    assertThat(omimSyndromeOptional.isPresent(), equalTo(true));

    HpoDisease omimSyndrome = omimSyndromeOptional.get();

    List<HpoDiseaseAnnotation> annotations = omimSyndrome.annotationStream()
      .sorted()
      .collect(Collectors.toList());

    assertThat(annotations, hasSize(2));
    HpoDiseaseAnnotation first = annotations.get(0);
    assertThat(first.id().getValue(), equalTo("HP:0001167"));
    assertThat(first.ratio().numerator(), equalTo(5));
    assertThat(first.ratio().denominator(), equalTo(13));
    assertThat(first.references(), hasSize(2));
    assertThat(first.references(), hasItems(
      AnnotationReference.of(TermId.of("PMID:20375004"), EvidenceCode.PCS),
      AnnotationReference.of(TermId.of("PMID:22736615"), EvidenceCode.PCS))
    );
    PointInTime earliestOnset = first.earliestOnset().get();
    assertThat(earliestOnset.days(), equalTo(29));
    PointInTime latestOnset = first.latestOnset().get();
    assertThat(latestOnset.completeYears(), equalTo(1));
    assertThat(first.modifiers(), hasItems(TermId.of("HP:0012832"), TermId.of("HP:0012828")));

    HpoDiseaseAnnotation second = annotations.get(1);
    assertThat(second.id().getValue(), equalTo("HP:0001238"));
    assertThat(second.isAbsent(), equalTo(true));
    assertThat(second.ratio().numerator(), equalTo(0));
    assertThat(second.ratio().denominator(), equalTo(OPTIONS.cohortSize()));
    assertThat(second.references(), hasSize(1));
    assertThat(second.references(), hasItem(AnnotationReference.of(TermId.of("PMID:20375004"), EvidenceCode.PCS)));
    assertThat(second.earliestOnset().isEmpty(), equalTo(true));
    assertThat(second.latestOnset().isEmpty(), equalTo(true));
  }

  @Test
  public void globalOnset() throws Exception {
    HpoDiseases hpoDiseases = instance.load(HPOA);

    Optional<HpoDisease> diseaseOptional = hpoDiseases.diseaseById(TermId.of("OMIM:111111"));

    assertThat(diseaseOptional.isPresent(), equalTo(true));

    HpoDisease syndrome = diseaseOptional.get();

    Optional<TemporalInterval> onsetOpt = syndrome.diseaseOnset();
    assertThat(onsetOpt.isPresent(), equalTo(true));

    TemporalInterval onset = onsetOpt.get();
    assertThat(onset.start().days(), equalTo(HpoOnset.CHILDHOOD_ONSET.start().days()));
    assertThat(onset.end().days(), equalTo(HpoOnset.CHILDHOOD_ONSET.end().days()));
  }
}
