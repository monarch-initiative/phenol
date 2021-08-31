package org.monarchinitiative.phenol.annotations.obo.hpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.annotations.formats.EvidenceCode;
import org.monarchinitiative.phenol.annotations.formats.Sex;
import org.monarchinitiative.phenol.annotations.formats.hpo.Frequency;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoOnset;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Optional;

/**
 * This class represents one line of the V2 (post 2018) HPO annotation line from the "big file"
 * (phenotype.hpoa). It is intended to be used as part of the processing of the big file. A
 * convenience class that will allow us to collect the annotation lines for each disease that we
 * want to parse; from these data, we will construct the {@link HpoDisease}.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
@Deprecated // to remove
class HpoAnnotationLine {

  /** 1. The diseaseId. For instance, "OMIM:300200". */
  private final TermId diseaseId;
  /** 3. The disease name, e.g., Marfan syndrome . */
  private final  String dbObjectName;
  /** 4. true is this is a negated annotation, i.e., some phenotype is not present in some disease.*/
  private final boolean isNegated;
  /** 5. The phenotype term id */
  private final  TermId phenotypeId;
  /** 6. Publication */
  private final String publication;
  /** 7. Evidence about assertion */
  private final EvidenceCode evidence;
  /** 8. The onset (can be null) */
  private final HpoOnset onset;
  /** 9. The frequency (can be null) */
  private final Frequency frequency;
  /** 10. Male, female */
  private final Sex sex;
  /** 11. Modifier terms (0..n) */
  private final List<TermId> modifiers;
  /** 12. aspect */
  private final String aspect;
  /** 13. the biocurator/date, e.g., HPO:skoehler[2018-02-17] */
  private final String biocuration;

  static HpoAnnotationLine of(TermId databaseId,
                              String dbObjectName,
                              boolean isNegated,
                              TermId phenotypeId,
                              String publication,
                              EvidenceCode evidence,
                              HpoOnset onset,
                              Frequency frequency,
                              Sex sex,
                              List<TermId> modifierList,
                              String aspect,
                              String biocuration) {
    return new HpoAnnotationLine(databaseId, dbObjectName, isNegated, phenotypeId, publication, evidence, onset, frequency, sex, modifierList, aspect, biocuration);
  }

  private HpoAnnotationLine(TermId databaseId,
                            String dbObjectName,
                            boolean isNegated,
                            TermId phenotypeId,
                            String publication,
                            EvidenceCode evidence,
                            HpoOnset onset,
                            Frequency frequency,
                            Sex sex,
                            List<TermId> modifiers,
                            String aspect,
                            String biocuration) {
    this.diseaseId = databaseId;
    this.dbObjectName = dbObjectName;
    this.isNegated = isNegated;
    this.phenotypeId = phenotypeId;
    this.publication = publication;
    this.evidence = evidence;
    this.onset = onset;
    this.frequency = frequency;
    this.sex = sex;
    this.modifiers = modifiers;
    this.aspect = aspect;
    this.biocuration = biocuration;
  }


//  /**
//   * @param line The header line of a V2 small file
//   * @return true iff the fields of the line exactly match {@link #headerFields}.
//   */
//  static boolean isValidHeaderLine(String line) throws PhenolException {
//    String[] fields = line.split("\t");
//    if (fields.length != headerFields.length) {
//      String msg = String.format("Expected %d fields in header line but got %d",headerFields.length, fields.length);
//      throw new PhenolException(msg);
//    }
//    for (int i = 0; i < headerFields.length; i++) {
//      if (!fields[i].equals(headerFields[i])) {
//        String msg = String.format("Expected header field %d to be %s but got %s",i, headerFields[i], fields[i]);
//        throw new PhenolException(msg);
//      }
//    }
//    return true;
//  }

  /**
   * @return The TermId representing this disease, e.g., OMIM:600100.
   */
  TermId getDiseaseTermId() {
    return this.diseaseId;
  }

  TermId getPhenotypeId() {
    return phenotypeId;
  }

  Optional<HpoOnset> onset() {
    return Optional.ofNullable(onset);
  }

  Optional<Frequency> frequency() {
    return Optional.ofNullable(frequency);
  }

  Optional<Sex> getSex() {
    return Optional.ofNullable(sex);
  }

  List<TermId> getModifiers() {
    return modifiers;
  }

  List<String> getPublication() {
    ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
    if (publication == null || publication.isEmpty()) return builder.build();
    String[] A = publication.split(";") ;
    for (String a : A ){
      builder.add(a.trim());
    }
    return builder.build();
  }

  Optional<EvidenceCode> getEvidence() {
    return Optional.ofNullable(evidence);
  }

  String getBiocuration() {
    return biocuration;
  }

  String getDbObjectName() {
    return dbObjectName;
  }

  String getAspect() { return this.aspect;}

  /** @return true if this annotation is negated. */
  boolean isNegated() {
    return isNegated;
  }

//  /**
//   * This method can be used to create a full HpoAnnotation object. Note that it is not
//   * intended to be used to parse annotation lines that represent models of inheritance or
//   * negative annotations. For this reason, the method is package-private and should only be
//   * called by {@link HpoAssociationParser}.
//   * @param line AN HpoAnnotationLinbe object representing opne phenotype annotation
//   * @param ontology reference to HPO Ontology object
//   * @return corresponding HpoAnnotation object
//   */
//   static HpoAnnotation toHpoAnnotation(HpoAnnotationLine line, Ontology ontology) {
//    TermId phenoId = line.getPhenotypeId();
//    double frequency = getFrequency(line.getFrequency(), ontology);
//    String frequencyString=line.frequency.isEmpty() ? DEFAULT_FREQUENCY_STRING : line.frequency;
//    HpoOnset onset = getOnset(line.onsetId);
//    return new HpoAnnotation(phenoId,
//      frequency,
//      frequencyString,
//      onset,
//      getModifiers(line.getModifierList()),
//      line.getPublication(),
//      EvidenceCode.fromString(line.evidence));
//  }

//  @Override
//  public String toString() {
//    String [] fields={this.diseaseId,
//      this.dbObjectName,
//      this.phenotypeId.getValue() ,
//      this.onsetId==null? "" :this.onsetId.getValue(),
//      this.frequency ,
//      this.sex ,
//      this.isNegated ?"NOT":"" ,
//      this.aspect,
//      this.modifierList ,
//      this.publication,
//      this.evidence ,
//      this.biocuration};
//    return String.join("\t",fields);
//  }
}
