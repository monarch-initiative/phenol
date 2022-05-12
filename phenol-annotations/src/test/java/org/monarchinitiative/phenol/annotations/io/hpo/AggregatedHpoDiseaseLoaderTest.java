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

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class AggregatedHpoDiseaseLoaderTest {

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
    instance = new AggregatedHpoDiseaseLoader(HPO, HpoDiseaseLoaderOptions.defaultOptions());
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

    assertThat(annotations, hasSize(3));
    HpoDiseaseAnnotation first = annotations.get(0);
    assertThat(first.id().getValue(), equalTo("HP:0001167"));
    assertThat(first.ratio(), equalTo(Ratio.of(1, 8)));
    assertThat(first.references(), hasSize(1));
    assertThat(first.references(), hasItem(AnnotationReference.of(TermId.of("PMID:20375004"), EvidenceCode.PCS)));
    assertThat(first.earliestOnset().get(), equalTo(Age.postnatal(1, 0, 0)));
    assertThat(first.latestOnset().get(), equalTo(Age.postnatal(5, 0, 0)));

    HpoDiseaseAnnotation second = annotations.get(1);
    assertThat(second.id().getValue(), equalTo("HP:0001167"));
    assertThat(second.ratio(), equalTo(Ratio.of(4, 5)));
    assertThat(second.references(), hasSize(1));
    assertThat(second.references(), hasItem(AnnotationReference.of(TermId.of("PMID:22736615"), EvidenceCode.PCS)));
    assertThat(second.earliestOnset().get(), equalTo(Age.postnatal(29)));
    assertThat(second.latestOnset().get(), equalTo(Age.postnatal(16, 0, 0)));

    HpoDiseaseAnnotation third = annotations.get(2);
    assertThat(third.id().getValue(), equalTo("HP:0001238"));
    assertThat(third.ratio(), equalTo(Ratio.of(0, 50)));
    assertThat(third.references(), hasSize(1));
    assertThat(third.references(), hasItem(AnnotationReference.of(TermId.of("PMID:20375004"), EvidenceCode.PCS)));
    assertThat(third.earliestOnset().isEmpty(), equalTo(true));
    assertThat(third.latestOnset().isEmpty(), equalTo(true));
  }
}
