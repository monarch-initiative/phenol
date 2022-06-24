package org.monarchinitiative.phenol.annotations;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.annotations.io.hpo.HpoDiseaseAnnotationFactory;
import org.monarchinitiative.phenol.annotations.io.hpo.KnowsRatioAndMaybeTemporalInterval;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

public class HpoDiseaseExamples {

  private static final HpoDiseaseAnnotationFactory FACTORY = HpoDiseaseAnnotationFactory.defaultInstance();

  private HpoDiseaseExamples() {
  }

  public static HpoDisease marfanSyndrome() {
    String diseaseName = "Marfan Syndrome";
    TermId databaseId = TermId.of("OMIM:154700");
    List<TermId> noModifiersForNow = List.of();
    List<HpoDiseaseAnnotation> annotations = List.of(
      FACTORY.create(TermId.of("HP:0000483"), // Astigmatism
        List.of(KnowsRatioAndMaybeTemporalInterval.of(Ratio.of(3, 53), null)), noModifiersForNow,
        List.of(HpoDiseaseAnnotationTest.createAnnotationReference("PMID:33436942"))),
      FACTORY.create(TermId.of("HP:0001377"), // Limited elbow extension
        List.of(KnowsRatioAndMaybeTemporalInterval.of(Ratio.of(29, 199), null)), List.of(TermId.of("HP:0012832")), // bilateral
        List.of(HpoDiseaseAnnotationTest.createAnnotationReference("PMID:28050285"))),
      FACTORY.create(TermId.of("HP:0032934"), // Spontaneous cerebrospinal fluid leak
        List.of(KnowsRatioAndMaybeTemporalInterval.of(HpoFrequency.VERY_RARE, TemporalInterval.openEnd(HpoOnset.YOUNG_ADULT_ONSET.start()))), noModifiersForNow,
        List.of(HpoDiseaseAnnotationTest.createAnnotationReference("PMID:8530937")))
    );
    List<TermId> modesOfInheritance = List.of(TermId.of("HP:0000006")); // Autosomal dominant inheritance

    return HpoDisease.of(databaseId,
      diseaseName,
      TemporalInterval.openEnd(HpoOnset.YOUNG_ADULT_ONSET.start()),
      annotations,
      modesOfInheritance
    );
  }
}
