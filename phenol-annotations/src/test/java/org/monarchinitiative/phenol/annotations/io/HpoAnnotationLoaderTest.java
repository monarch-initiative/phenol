package org.monarchinitiative.phenol.annotations.io;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDiseaseAnnotation;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoFrequency;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoModeOfInheritanceTermIds;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class HpoAnnotationLoaderTest {

  private static final double ERROR = 1E-12;

  private static final Set<String> DEFAULT_DATABASE_PREFIXES = new HashSet<>(Arrays.asList("OMIM", "ORPHA", "DECIPHER"));

  @Test
  void toHpoAnnotationLine() {
    String line ="OMIM:269150\tSCHINZEL-GIEDION MIDFACE RETRACTION SYNDROME\t\tHP:0030736\tOMIM:269150\tTAS\t\t\t\t\tP\tHPO:skoehler[2017-07-13]";

    Optional<HpoAnnotationLine> lineOpt = HpoAnnotationLoader.toHpoAnnotationLine(DEFAULT_DATABASE_PREFIXES).apply(line);
    assertThat(lineOpt.isPresent(), is(true));

    HpoAnnotationLine annotationLine = lineOpt.get();
    assertThat(annotationLine.diseaseId(), equalTo(TermId.of("OMIM:269150")));
    assertThat(annotationLine.getDbObjectName(), equalTo("SCHINZEL-GIEDION MIDFACE RETRACTION SYNDROME"));
    assertThat(annotationLine.phenotypeId(), equalTo(TermId.of("HP:0030736")));

    assertThat(annotationLine.evidence().get(), equalTo(EvidenceCode.TAS));
    assertThat(annotationLine.aspect(), equalTo(Aspect.PHENOTYPIC_ABNORMALITY));
    assertThat(annotationLine.isNegated(), equalTo(false));
    assertThat(annotationLine.frequency().isPresent(), equalTo(false));
  }

  @Test
  public void load() throws Exception {
    Path path = Paths.get("src", "test", "resources", "small.hpoa");
    Ontology ontology = Mockito.mock(Ontology.class);
    Map<TermId, HpoDisease> termIdHpoDiseaseMap = HpoAnnotationLoader.loadDiseaseMap(path.toFile(), ontology);


    TermId marfanId = TermId.of("OMIM:154700");
    TermId leighId = TermId.of("ORPHA:506");
    assertThat(termIdHpoDiseaseMap.keySet(), hasItems(marfanId, leighId));

    // check Marfan syndrome fields
    HpoDisease marfan = termIdHpoDiseaseMap.get(marfanId);
    assertThat(marfan.diseaseDatabaseTermId(), equalTo(marfanId));
    assertThat(marfan.diseaseName(), equalTo("Marfan syndrome"));
    assertThat(marfan.modesOfInheritance(), hasItems(HpoModeOfInheritanceTermIds.AUTOSOMAL_DOMINANT));

    assertThat(marfan.phenotypicAbnormalities().count(), equalTo(68L));

    assertThat(marfan.negativeAnnotations(), hasItems(TermId.of("HP:0002616")));
    assertThat(marfan.negativeAnnotations(), hasSize(1));

    TermId pectusCarinatumId = TermId.of("HP:0000768");
    Optional<HpoDiseaseAnnotation> pectusCarinatumOptional = marfan.getAnnotation(pectusCarinatumId);
    assertThat(pectusCarinatumOptional.isPresent(), equalTo(true));

    HpoDiseaseAnnotation pectusCarinatum = pectusCarinatumOptional.get();
    assertThat(pectusCarinatum.termId(), equalTo(pectusCarinatumId));
    assertThat(pectusCarinatum.frequency(), closeTo(58./146, ERROR));

    // check Leigh syndrome fields
    HpoDisease leighSyndrome = termIdHpoDiseaseMap.get(leighId);
    assertThat(leighSyndrome.getFrequencyOfTermInDisease(TermId.of("HP:0001324")), closeTo(HpoFrequency.OCCASIONAL.frequency(), ERROR));

    // TODO - write more assertions
  }
}
