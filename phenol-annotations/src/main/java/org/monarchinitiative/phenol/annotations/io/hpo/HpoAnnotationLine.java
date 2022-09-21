package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.base.Sex;
import org.monarchinitiative.phenol.annotations.formats.AnnotationReference;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoFrequency;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoOnset;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Identified;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represent an HPO Term together with a Frequency and an Onset and modifiers. This is intended to
 * be used to represent a disease annotation.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @version 0.1.3 (2018-03-12)
 */
public class HpoAnnotationLine implements Identified {

  /**
   * The annotated {@link TermId}.
   */
  private final TermId diseaseId;
  private final String diseaseName;
  private final boolean isNegated;
  private final TermId phenotypeTermId;
  private final List<AnnotationReference> annotationReferences;
  /**
   * The characteristic age of onset of a feature in a certain disease.
   */
  private final HpoOnset onset;
  private final String frequency;
  /**
   * Phenotype associated with sex.
   */
  private final Sex sex;
  /**
   * List of modifiers of this annotation. List can be empty but cannot be null
   */
  private final List<TermId> modifiers;
  private final Aspect aspect;
  /**
   * A list of curators that created/updated this line, e.g. {@code HPO:probinson[2017-06-16];HPO:iea[2009-02-17]}.
   */
  private final List<String> curators;

  public static HpoAnnotationLine of(TermId diseaseId,
                                     String diseaseName,
                                     boolean isNegated,
                                     TermId phenotypeTermId,
                                     List<AnnotationReference> annotationReferences,
                                     HpoOnset onset,
                                     String frequency,
                                     Sex sex,
                                     List<TermId> modifiers,
                                     Aspect aspect,
                                     List<String> curators) {
    return new HpoAnnotationLine(diseaseId,
      diseaseName,
      isNegated,
      phenotypeTermId,
      annotationReferences,
      onset,
      frequency,
      sex,
      modifiers,
      aspect,
      curators);
  }

  public static HpoAnnotationLine of(String line) throws PhenolException {
    try {
      String[] fields = line.split("\t");
      TermId diseaseId = TermId.of(fields[0]);
      String diseaseName = fields[1];
      boolean isNegated = fields[2].equalsIgnoreCase("NOT");
      TermId phenotype = TermId.of(fields[3]);

      EvidenceCode code = EvidenceCode.parse(fields[5]);
      List<AnnotationReference> annotationReferences = Arrays.stream(fields[4].split(";"))
        .filter(token -> !token.isBlank())
        .map(reference -> AnnotationReference.of(TermId.of(reference), code))
        .collect(Collectors.toList());

      HpoOnset onsetId = HpoOnset.fromHpoIdString(fields[6])
        .orElse(null);
      String frequency = fields[7];

      Sex sex = Sex.parse(fields[8]).orElse(null);

      List<TermId> modifiers = Arrays.stream(fields[9].split(";"))
        .filter(token -> !token.isBlank())
        .map(TermId::of)
        .collect(Collectors.toList());

      Aspect aspect = Aspect.parse(fields[10]).orElse(null);

      List<String> curators = Arrays.stream(fields[11].split(";"))
        .map(String::trim)
        .collect(Collectors.toList());

      return of(diseaseId, diseaseName, isNegated, phenotype, annotationReferences, onsetId, frequency, sex, modifiers, aspect, curators);
    } catch (PhenolRuntimeException e) {
      throw new PhenolException(String.format("Exception [%s] parsing line: %s",e.getMessage(), line));
    }
  }

  /**
   * @param diseaseId            Disease identifier, e.g. OMIM:123456
   * @param diseaseName          Name of the disease, e.g. "LEIGH SYNDROME; LS"
   * @param isNegated            Is the HPO ID negated?
   * @param phenotypeTermId      Hpo ID of the line {@link TermId}
   * @param annotationReferences List of references (e.g., PMID or OMIM) that support this annotation
   * @param onset                The onset of the feature in the disease
   * @param frequency            Annotation frequency
   * @param sex                  Phenotype associated with sex
   * @param modifiers            list of modifiers (list can be empty but not null)
   * @param aspect               The type of the phenotype (P,I,M,C)
   * @param curators             The curators and dates of curation of this annotation
   */
  private HpoAnnotationLine(TermId diseaseId,
                            String diseaseName,
                            boolean isNegated,
                            TermId phenotypeTermId,
                            List<AnnotationReference> annotationReferences,
                            HpoOnset onset,
                            String frequency,
                            Sex sex,
                            List<TermId> modifiers,
                            Aspect aspect,
                            List<String> curators) {
    this.diseaseId = Objects.requireNonNull(diseaseId);
    this.diseaseName = Objects.requireNonNull(diseaseName);
    this.isNegated = isNegated;
    this.phenotypeTermId = Objects.requireNonNull(phenotypeTermId, "Term ID must not be null");
    this.frequency = Objects.requireNonNull(frequency, "Annotation frequency must not be null");
    this.onset = onset; // nullable
    this.modifiers = Objects.requireNonNull(modifiers, "Modifiers must not be null");
    this.annotationReferences = Objects.requireNonNull(annotationReferences);
    this.sex = sex; // nullable
    this.aspect = Objects.requireNonNull(aspect);
    this.curators = Objects.requireNonNull(curators);
  }

  /**
   * @return {@link TermId} corresponding to the {@code HPO_ID} column (the same as {@link #phenotypeTermId()})
   * and <em>not</em> the {@link #diseaseId()}.
   */
  @Override
  public TermId id() {
    return phenotypeTermId();
  }

  public TermId diseaseId() {
    return diseaseId;
  }

  public String diseaseName() {
    return diseaseName;
  }

  public boolean isNegated() {
    return isNegated;
  }

  public TermId phenotypeTermId() {
    return phenotypeTermId;
  }

  /**
   * @return The annotating {@link HpoFrequency}.
   */
  public String frequency() {
    return frequency;
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

  public List<AnnotationReference> annotationReferences() {
    return annotationReferences;
  }

  public Sex sex() {
    return sex;
  }

  public Aspect aspect() {
    return aspect;
  }

  public List<String> curators() {
    return curators;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoAnnotationLine that = (HpoAnnotationLine) o;
    return isNegated == that.isNegated && Objects.equals(diseaseId, that.diseaseId) && Objects.equals(diseaseName, that.diseaseName) && Objects.equals(phenotypeTermId, that.phenotypeTermId) && Objects.equals(annotationReferences, that.annotationReferences) && onset == that.onset && Objects.equals(frequency, that.frequency) && sex == that.sex && Objects.equals(modifiers, that.modifiers) && aspect == that.aspect && Objects.equals(curators, that.curators);
  }

  @Override
  public int hashCode() {
    return Objects.hash(diseaseId, diseaseName, isNegated, phenotypeTermId, annotationReferences, onset, frequency, sex, modifiers, aspect, curators);
  }

  @Override
  public String toString() {
    return "HpoAnnotationLine{" +
      "diseaseId=" + diseaseId +
      ", diseaseName='" + diseaseName + '\'' +
      ", isNegated=" + isNegated +
      ", phenotypeTermId=" + phenotypeTermId +
      ", annotationReferences=" + annotationReferences +
      ", onset=" + onset +
      ", frequency='" + frequency + '\'' +
      ", sex=" + sex +
      ", modifiers=" + modifiers +
      ", aspect=" + aspect +
      ", curators=" + curators +
      '}';
  }
}
