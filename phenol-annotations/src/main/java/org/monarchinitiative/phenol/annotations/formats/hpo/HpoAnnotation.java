package org.monarchinitiative.phenol.annotations.formats.hpo;

import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represent an HPO Term together with a Frequency and an Onset and modifiers. This is intended to
 * be used to represent a disease annotation.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.1.3 (2018-03-12)
 */
public class HpoAnnotation implements Identified {
  /**
   * The annotated {@link TermId}.
   */
  private final TermId termId;

  private final AnnotationFrequency annotationFrequency;
  /**
   * The characteristic age of onset of a feature in a certain disease.
   */
  private final HpoOnset onset;
  /**
   * List of modifiers of this annotation. List can be empty but cannot be null
   */
  private final List<TermId> modifiers;
  /**
   * List of citations that support this annotation.
   */
  private final List<String> citations;
  /**
   * Evidence code for this annotation.
   */
  private final EvidenceCode evidence;
  /**
   * Phenotype associated with sex.
   */
  private final Sex sex;

  public static HpoAnnotation of(TermId termId,
                                 AnnotationFrequency annotationFrequency,
                                 HpoOnset onset,
                                 List<TermId> modifiers,
                                 List<String> cites,
                                 EvidenceCode evidenceCode,
                                 Sex sex) {
    return new HpoAnnotation(termId, annotationFrequency, onset, modifiers, cites, evidenceCode, sex);
  }

  /**
   * @param termId              Annotated {@link TermId}
   * @param annotationFrequency Annotation frequency
   * @param onset               The onset of the feature in the disease
   * @param modifiers           list of modifiers (list can be empty but not null)
   * @param cites               List of publications (e.g., PMID or OMIM) that support this annotation
   * @param evidenceCode        Evidence code for this annotation
   * @param sex                 Phenotype associated with sex
   */
  private HpoAnnotation(TermId termId,
                        AnnotationFrequency annotationFrequency,
                        HpoOnset onset,
                        List<TermId> modifiers,
                        List<String> cites,
                        EvidenceCode evidenceCode,
                        Sex sex) {
    this.termId = Objects.requireNonNull(termId, "Term ID must not be null");
    this.annotationFrequency = Objects.requireNonNull(annotationFrequency, "Annotation frequency must not be null");
    this.onset = onset; // nullable
    this.modifiers = Objects.requireNonNull(modifiers, "Modifiers must not be null");
    this.citations = Objects.requireNonNull(cites, "Citations must not be null");
    this.evidence = Objects.requireNonNull(evidenceCode, "Evidence code must not be null");
    this.sex = sex; // nullable
  }

  /**
   * @return The annotated {@link TermId}.
   */
  @Override
  public TermId id() {
    return termId;
  }

  /**
   * @return The annotating {@link HpoFrequency}.
   */
  public AnnotationFrequency annotationFrequency() {
    return annotationFrequency;
  }

  /**
   * @return the {@link HpoOnset} object representing the age of onset of the feature (or null if not onset is available)
   */
  public Optional<HpoOnset> onset() {
    return Optional.ofNullable(onset);
  }

  public List<TermId> modifiers() {
    return modifiers;
  }

  public List<String> citations() {
    return citations;
  }

  public EvidenceCode evidence() {
    return evidence;
  }

  public boolean isPCS() {
    return this.evidence == EvidenceCode.PCS;
  }

  public boolean isIEA() {
    return this.evidence == EvidenceCode.IEA;
  }

  public boolean isTAS() {
    return this.evidence == EvidenceCode.TAS;
  }

  public Sex sex() {
    return sex;
  }

  @Override
  public int hashCode() {
    return Objects.hash(termId, annotationFrequency, onset, modifiers, citations, evidence, sex);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoAnnotation that = (HpoAnnotation) o;
    return Objects.equals(termId, that.termId) && Objects.equals(annotationFrequency, that.annotationFrequency) && onset == that.onset && Objects.equals(modifiers, that.modifiers) && Objects.equals(citations, that.citations) && evidence == that.evidence && sex == that.sex;
  }

  @Override
  public String toString() {
    return "HpoAnnotation{" +
      "termId=" + termId +
      ", annotationFrequency=" + annotationFrequency +
      ", onset=" + onset +
      ", modifiers=" + modifiers +
      ", citations=" + citations +
      ", evidence=" + evidence +
      ", sex=" + sex +
      '}';
  }
}
