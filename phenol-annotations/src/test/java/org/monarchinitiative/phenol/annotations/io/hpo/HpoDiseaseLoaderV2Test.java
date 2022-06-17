package org.monarchinitiative.phenol.annotations.io.hpo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.annotations.TestBase;
import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.Age;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseases;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HpoDiseaseLoaderV2Test {

  private static final Path HPOA = TestBase.TEST_BASE.resolve("annotations").resolve("phenotype.fake.hpoa");
  private static final Path HPO_PATH = TestBase.TEST_BASE.resolve("hpo_toy.json");

  private static Ontology HPO;

  private HpoDiseaseLoaderV2 instance;

  @BeforeAll
  public static void beforeAll() {
    HPO = OntologyLoader.loadOntology(HPO_PATH.toFile());
  }

  @BeforeEach
  public void setUp() {
    instance = new HpoDiseaseLoaderV2(HPO, HpoDiseaseLoaderOptions.defaultOptions());
  }

  @Test
  public void testPresenceOfExpectedDiseaseIds() throws Exception {
    HpoDiseases hpoDiseases = instance.load(HPOA);

    assertThat(hpoDiseases.diseaseIds(), hasSize(2));
    assertThat(hpoDiseases.diseaseIds(), hasItems(TermId.of("OMIM:987654"), TermId.of("ORPHA:123456")));
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
    assertThat(first.ratio(), equalTo(Ratio.of(5, 13)));
    assertThat(first.references(), hasSize(2));
    assertThat(first.references(), hasItems(AnnotationReference.of(TermId.of("PMID:20375004"), EvidenceCode.PCS),
      AnnotationReference.of(TermId.of("PMID:22736615"), EvidenceCode.PCS)));
    assertThat(first.earliestOnset().get(), equalTo(Age.postnatal(29)));
    assertThat(first.latestOnset().get(), equalTo(Age.postnatal(1, 0, 0)));

    HpoDiseaseAnnotation second = annotations.get(1);
    assertThat(second.id().getValue(), equalTo("HP:0001238"));
    assertThat(second.ratio(), equalTo(Ratio.of(0, 50)));
    assertThat(second.references(), hasSize(1));
    assertThat(second.references(), hasItem(AnnotationReference.of(TermId.of("PMID:20375004"), EvidenceCode.PCS)));
    assertThat(second.earliestOnset().isEmpty(), equalTo(true));
    assertThat(second.latestOnset().isEmpty(), equalTo(true));
  }
}
