package org.monarchinitiative.phenol.annotations;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

public class HpoDiseaseExamples {

  private HpoDiseaseExamples() {
  }

  public static HpoDisease marfanSyndrome() {
    String diseaseName = "Marfan Syndrome";
    TermId databaseId = TermId.of("OMIM:154700");
    List<HpoDiseaseAnnotation> annotations = List.of(
      HpoDiseaseAnnotation.of(TermId.of("HP:0000483"), // Astigmatism
        List.of(
          HpoDiseaseAnnotationMetadata.of(HpoDiseaseAnnotationTest.createAnnotationReference("PMID:33436942"), null, AnnotationFrequency.of(Ratio.of(3, 53)), List.of(), null)
        )),
      HpoDiseaseAnnotation.of(TermId.of("HP:0001377"), // Limited elbow extension
        List.of(
          HpoDiseaseAnnotationMetadata.of(HpoDiseaseAnnotationTest.createAnnotationReference("PMID:28050285"), null, AnnotationFrequency.of(Ratio.of(29, 199)), List.of(), null)
        )),
      HpoDiseaseAnnotation.of(TermId.of("HP:0032934"), // Spontaneous cerebrospinal fluid leak
        List.of(
          HpoDiseaseAnnotationMetadata.of(HpoDiseaseAnnotationTest.createAnnotationReference("PMID:8530937"), TemporalInterval.openEnd(HpoOnset.YOUNG_ADULT_ONSET.start()), HpoFrequency.VERY_RARE, List.of(), null)
        ))
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
