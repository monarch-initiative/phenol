package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.InProgress;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Collection;
import java.util.Comparator;

/**
 * Represents a single phenotypic annotation for a disease, i.e. <em>Ectopia lentis</em> for Marfan syndrome.
 */
@InProgress
public interface HpoDiseaseAnnotation extends Comparable<HpoDiseaseAnnotation> {

//  public boolean isPCS() { return this.evidence == EvidenceCode.PCS; }
//  public boolean isIEA() { return this.evidence == EvidenceCode.IEA; }
//  public boolean isTAS() { return this.evidence == EvidenceCode.TAS; }
//  public String getEvidenceCodeString() { return this.evidence.toString(); }

  static HpoDiseaseAnnotation of(TermId termId, Collection<HpoDiseaseAnnotationMetadata> onsets) {
    double totalFrequency = onsets.stream()
      .mapToDouble(meta -> meta.frequency().frequency())
      .sum();
    if (totalFrequency > 1.0)
      throw new IllegalArgumentException("Total frequency cannot be greater than 1: " + totalFrequency);

    return HpoDiseaseAnnotationDefault.of(termId, onsets);
  }

  TermId termId();

  // TODO - do we really have to to expose this?

  Collection<HpoDiseaseAnnotationMetadata> metadata();



  // TODO - implement aggregator methods for onset, frequency, etc.

  default double frequency() {
    // TODO - implement getting the frequency based on `onsets()`.
    return metadata().stream()
      .mapToDouble(dfm -> dfm.frequency().frequency())
      .sum();
  }

  default HpoOnset earliestOnset() {
    return metadata().stream()
      .map(HpoDiseaseAnnotationMetadata::onset)
      .min(Comparator.comparingDouble(HpoOnset::lowerBound))
      .orElse(HpoOnset.UNKNOWN);
  }

  default HpoOnset latestOnset() {
    return metadata().stream()
      .map(HpoDiseaseAnnotationMetadata::onset)
      .max(Comparator.comparingDouble(HpoOnset::upperBound))
      .orElse(HpoOnset.UNKNOWN);
  }

  default double lowerOnsetBound() {
    return metadata().stream()
      .map(HpoDiseaseAnnotationMetadata::onset)
      .filter(HpoOnset::available)
      .mapToDouble(HpoOnset::lowerBound)
      .min()
      .orElse(HpoOnset.ONSET_LOWER_BOUND);
  }

  default double upperOnsetBound() {
    return metadata().stream()
      .map(HpoDiseaseAnnotationMetadata::onset)
      .filter(HpoOnset::available)
      .mapToDouble(HpoOnset::upperBound)
      .max()
      .orElse(HpoOnset.ONSET_UPPER_BOUND);
  }

}
