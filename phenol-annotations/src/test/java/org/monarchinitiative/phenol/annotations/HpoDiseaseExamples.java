package org.monarchinitiative.phenol.annotations;

import org.monarchinitiative.phenol.annotations.base.Ratio;
import org.monarchinitiative.phenol.annotations.base.temporal.TemporalInterval;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.annotations.formats.hpo.*;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

public class HpoDiseaseExamples {

  private HpoDiseaseExamples() {
  }

  public static HpoDisease marfanSyndrome() {
    String diseaseName = "Marfan Syndrome";
    TermId databaseId = TermId.of("OMIM:154700");
    List<TermId> noModifiersForNow = List.of();
      List<HpoDiseaseAnnotation> annotations = List.of(
      HpoDiseaseAnnotation.of(TermId.of("HP:0000483"), // Astigmatism
        List.of(HpoDiseaseAnnotationRecord.of(Ratio.of(3, 53), null,
          List.of(AnnotationReference.of(TermId.of("PMID:33436942"), EvidenceCode.PCS)), null, noModifiersForNow))),

      HpoDiseaseAnnotation.of(TermId.of("HP:0001377"), // Limited elbow extension
        List.of(HpoDiseaseAnnotationRecord.of(Ratio.of(29, 199), null,
          List.of(AnnotationReference.of(TermId.of("PMID:28050285"), EvidenceCode.PCS)), null,
          List.of(TermId.of("HP:0012832")) // bilateral
        ))),

      HpoDiseaseAnnotation.of(TermId.of("HP:0032934"), // Spontaneous cerebrospinal fluid leak
        List.of(HpoDiseaseAnnotationRecord.of(HpoFrequency.VERY_RARE, TemporalInterval.openEnd(HpoOnset.YOUNG_ADULT_ONSET.start()),
          List.of(AnnotationReference.of(TermId.of("PMID:8530937"), EvidenceCode.PCS)), null, noModifiersForNow)))
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
