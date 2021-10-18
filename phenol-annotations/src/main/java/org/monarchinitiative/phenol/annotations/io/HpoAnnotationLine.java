package org.monarchinitiative.phenol.annotations.io;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.annotations.formats.Sex;
import org.monarchinitiative.phenol.annotations.formats.hpo.DiseaseAnnotationFrequency;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoOnset;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This POJO represents the state of a well-formatted line of the V2 (post 2018) HPO annotation file
 * (a.k.a. "big file", phenotype.hpoa).
 * <p>
 * The class is intended to be used as part of the processing of the big file.
 * It is a convenience class, that allows us to collect the annotation lines for each disease that we
 * want to parse; from these data, we will construct the {@link HpoDisease}.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
class HpoAnnotationLine {

  /** 1. The diseaseId. For instance, "OMIM:300200". */
  private final TermId diseaseId;
  /** 3. The disease name, e.g., Marfan syndrome . */
  private final String dbObjectName;
  /** 4. true is this is a negated annotation, i.e., some phenotype is not present in some disease.*/
  private final boolean isNegated;
  /** 5. The phenotype term id (not null) */
  private final TermId phenotypeId;
  /** 6. Publication (not null) */
  private final String publication;
  /** 7. Evidence about assertion */
  private final EvidenceCode evidence;
  /** 8. The onset (can be null) */
  private final HpoOnset onset;
  /** 9. The frequency (can be null) */
  private final DiseaseAnnotationFrequency diseaseAnnotationFrequency;
  /** 10. Male, female */
  private final Sex sex;
  /** 11. Modifier terms (0..n) */
  private final List<TermId> modifiers;
  /** 12. aspect (not null) */
  private final Aspect aspect;
  /** 13. the biocurator/date, e.g., HPO:skoehler[2018-02-17] (not null) */
  private final String biocuration;

  static HpoAnnotationLine of(TermId databaseId,
                              String dbObjectName,
                              boolean isNegated,
                              TermId phenotypeId,
                              String publication,
                              EvidenceCode evidence,
                              HpoOnset onset,
                              DiseaseAnnotationFrequency diseaseAnnotationFrequency,
                              Sex sex,
                              List<TermId> modifierList,
                              Aspect aspect,
                              String biocuration) {
    return new HpoAnnotationLine(databaseId, dbObjectName, isNegated, phenotypeId, publication, evidence, onset, diseaseAnnotationFrequency, sex, modifierList, aspect, biocuration);
  }

  private HpoAnnotationLine(TermId databaseId,
                            String dbObjectName,
                            boolean isNegated,
                            TermId phenotypeId,
                            String publication,
                            EvidenceCode evidence,
                            HpoOnset onset,
                            DiseaseAnnotationFrequency diseaseAnnotationFrequency,
                            Sex sex,
                            List<TermId> modifiers,
                            Aspect aspect,
                            String biocuration) {
    this.diseaseId = Objects.requireNonNull(databaseId);
    this.dbObjectName = Objects.requireNonNull(dbObjectName);
    this.isNegated = isNegated;
    this.phenotypeId = Objects.requireNonNull(phenotypeId);
    this.publication = Objects.requireNonNull(publication);
    this.evidence = Objects.requireNonNull(evidence);
    this.onset = onset;
    this.diseaseAnnotationFrequency = diseaseAnnotationFrequency;
    this.sex = Objects.requireNonNull(sex);
    this.modifiers = Objects.requireNonNull(modifiers);
    this.aspect = Objects.requireNonNull(aspect);
    this.biocuration = Objects.requireNonNull(biocuration);
  }


  /**
   * @return The TermId representing this disease, e.g., OMIM:600100.
   */
  TermId diseaseId() {
    return diseaseId;
  }

  String getDbObjectName() {
    return dbObjectName;
  }

  /** @return true if this annotation is negated. */
  boolean isNegated() {
    return isNegated;
  }

  TermId phenotypeId() {
    return phenotypeId;
  }

  List<String> publication() {
    ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
    if (publication.isEmpty()) return builder.build();
    String[] A = publication.split(";") ;
    for (String a : A ){
      builder.add(a.trim());
    }
    return builder.build();
  }

  EvidenceCode evidence() {
    return evidence;
  }

  Optional<HpoOnset> onset() {
    return Optional.ofNullable(onset);
  }

  Optional<DiseaseAnnotationFrequency> frequency() {
    return Optional.ofNullable(diseaseAnnotationFrequency);
  }

  Sex sex() {
    return sex;
  }

  List<TermId> modifiers() {
    return modifiers;
  }

  Aspect aspect() {
    return aspect;
  }

  String biocuration() {
    return biocuration;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HpoAnnotationLine that = (HpoAnnotationLine) o;
    return isNegated == that.isNegated && Objects.equals(diseaseId, that.diseaseId) && Objects.equals(dbObjectName, that.dbObjectName) && Objects.equals(phenotypeId, that.phenotypeId) && Objects.equals(publication, that.publication) && evidence == that.evidence && onset == that.onset && Objects.equals(diseaseAnnotationFrequency, that.diseaseAnnotationFrequency) && sex == that.sex && Objects.equals(modifiers, that.modifiers) && aspect == that.aspect && Objects.equals(biocuration, that.biocuration);
  }

  @Override
  public int hashCode() {
    return Objects.hash(diseaseId, dbObjectName, isNegated, phenotypeId, publication, evidence, onset, diseaseAnnotationFrequency, sex, modifiers, aspect, biocuration);
  }

  @Override
  public String toString() {
    return "HpoAnnotationLine{" +
      "diseaseId=" + diseaseId +
      ", dbObjectName='" + dbObjectName + '\'' +
      ", isNegated=" + isNegated +
      ", phenotypeId=" + phenotypeId +
      ", publication='" + publication + '\'' +
      ", evidence=" + evidence +
      ", onset=" + onset +
      ", frequency=" + diseaseAnnotationFrequency +
      ", sex=" + sex +
      ", modifiers=" + modifiers +
      ", aspect=" + aspect +
      ", biocuration='" + biocuration + '\'' +
      '}';
  }
}
