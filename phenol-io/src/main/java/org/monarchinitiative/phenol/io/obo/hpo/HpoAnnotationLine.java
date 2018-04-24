package org.monarchinitiative.phenol.io.obo.hpo;

import org.monarchinitiative.phenol.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * This class represents one line of the V2 (post 2018) HPO annotation line from the "big file"
 * (phenotype.hpoa). It is intended to be use as part of the processing of the big file. A
 * convenience class that will allow us to collect the annotation lines for each disease that we
 * want to parse; from these data, we will construct the {@link HpoDisease}. Note that
 * this class performs only the first part of the parse. The class {@link
 * HpoDiseaseAnnotationParser} will complete the input of the lines once all of the lines for some
 * disease have been input into individual {@link HpoAnnotationLine} objects. The reason for this is
 * that in some cases, we may have multiple lines for some annotation and we will want to combine
 * them into one annotation for some computational disease models. The class checks whether the
 * number of fields is correct, and this can be got with {@link #hasValidNumberOfFields}
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
class HpoAnnotationLine {

  private static final int DB_IDX = 0;
  private static final int DB_OBJECT_ID_IDX = 1;
  private static final int DB_NAME_IDX = 2;
  private static final int QUALIFIER_IDX = 3;
  private static final int PHENOTYPE_ID_IDX = 4;
  private static final int DB_REFERENCE_IDX = 5;
  private static final int EVIDENCE_IDX = 6;
  private static final int ONSET_ID_IDX = 7;
  private static final int FREQUENCY_IDX = 8;
  private static final int SEX_IDX = 9;
  private static final int MODIFIER_IDX = 10;
  private static final int ASPECT_IDX = 11;
  private static final int ASSIGNED_BY_IDX = 12;
  private static final int DATE_CREATED_IDX = 13;

  private boolean valid_number_of_fields=true;

  /**
   * Names of the header fields. Every valid small file should have a header line with exactly these
   * fields.
   */
  private static final String[] headerFields = {
    "#DB",
    "DB_Object_ID",
    "DB_Name",
    "Qualifier",
    "HPO_ID",
    "DB_Reference",

    "Evidence",
    "Onset",
    "Frequency",
    "Sex",
    "Modifier",
    "Aspect",
    "Date_Created",
    "Assigned_By"
  };

  /** 1. The database portion of the diseaseId. For instance, "OMIM" for "OMIM:300200". */
  private String database;
  /** 2. The accession number part of the disease Id. For instance, "300200" for "OMIM:300200". */
  private String DBObjectId;
  /** 3. The disease name, e.g., Marfan syndrome . */
  private String DbObjectName;
  /** 4. true is this is a negated annotation, i.e., some phenotype is not present in some disease.*/
  private boolean NOT = false;
  /** 5. The phenotype term id */
  private TermId phenotypeId;
  /** 6. Publication */
  private String publication;
  /** 7. Evidence about assertion */
  private String evidence;
  /** 8. The onset (can be null) */
  private TermId onsetId = null;
  /** 9. The frequency (can be null) */
  private String frequency;
  /** 10. Male, female */
  private String sex;
  /** 11. Modifier terms (0..n) */
  private String modifierList;
  /** 12. aspect */
  @SuppressWarnings("unused")
  private String aspect;
  /** 13. the date */
  private String dateCreated;
  /** 14. the biocurator */
  private String assignedBy;

  HpoAnnotationLine(String line) {
    String F[] = line.split("\t");
   if (F.length != headerFields.length) {
     valid_number_of_fields=true;
   }
    this.database = F[DB_IDX];
    this.DBObjectId = F[DB_OBJECT_ID_IDX];
    DbObjectName = F[DB_NAME_IDX];
    String phenoId = F[PHENOTYPE_ID_IDX];
    this.phenotypeId = ImmutableTermId.constructWithPrefix(phenoId);
    String onset = F[ONSET_ID_IDX];
    if (onset != null && onset.startsWith("HP:")) {
      onsetId = ImmutableTermId.constructWithPrefix(onset);
    }
    this.frequency = F[FREQUENCY_IDX];
    this.sex = F[SEX_IDX];
    String neg = F[QUALIFIER_IDX];
    this.NOT =  (neg != null && neg.equalsIgnoreCase("NOT"));
    this.aspect = F[ASPECT_IDX];
    this.modifierList = F[MODIFIER_IDX];
    this.publication = F[DB_REFERENCE_IDX];
    this.evidence = F[EVIDENCE_IDX];
    this.assignedBy = F[ASSIGNED_BY_IDX];
    this.dateCreated = F[DATE_CREATED_IDX];
  }

  /**
   * @param line The header line of a V2 small file
   * @return true iff the fields of the line exactly match {@link #headerFields}.
   */
  static boolean isValidHeaderLine(String line) {
    String F[] = line.split("\t");
    if (F.length != headerFields.length) return false;
    for (int i = 0; i < headerFields.length; i++) {
      if (!F[i].equals(headerFields[i])) return false;
    }
    return true;
  }

  final String getDiseaseId() {
    return String.format("%s:%s", database, DBObjectId);
  }

  TermId getPhenotypeId() {
    return phenotypeId;
  }

  TermId getOnsetId() {
    return onsetId;
  }

  String getFrequency() {
    return frequency;
  }

  String getSex() {
    return sex;
  }

  String getModifierList() {
    return modifierList;
  }

  String getPublication() {
    return publication;
  }

  String getEvidence() {
    return evidence;
  }

  String getAssignedBy() {
    return assignedBy;
  }

  String getDateCreated() {
    return dateCreated;
  }

  String getDatabase() {
    return database;
  }

  String getDBObjectId() {
    return DBObjectId;
  }

  String getDbObjectName() {
    return DbObjectName;
  }

  /** @return true if this annotation is negated. */
  boolean isNOT() {
    return NOT;
  }

  boolean hasValidNumberOfFields() {
    return valid_number_of_fields;
  }
}
