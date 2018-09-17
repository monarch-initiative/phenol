package org.monarchinitiative.phenol.io.obo.hpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;

import java.util.List;

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

  private static final int DatabaseId_IDX = 0;
  private static final int DB_NAME_IDX = 1;
  private static final int QUALIFIER_IDX = 2;
  private static final int PHENOTYPE_ID_IDX = 3;
  private static final int DB_REFERENCE_IDX = 4;
  private static final int EVIDENCE_IDX = 5;
  private static final int ONSET_ID_IDX = 6;
  private static final int FREQUENCY_IDX = 7;
  private static final int SEX_IDX = 8;
  private static final int MODIFIER_IDX = 9;
  private static final int ASPECT_IDX = 10;
  private static final int BIOCURATION_IDX = 11;

//  private static final TermPrefix OMIM_PREFIX = new TermPrefix("OMIM");
//  private static final TermPrefix ORPHANET_PREFIX = new TermPrefix("ORPHA");
//  private static final TermPrefix DECIPHER_PREFIX = new TermPrefix("DECIPHER");


  private boolean valid_number_of_fields=true;

  /**
   * Names of the header fields. Every valid small file should have a header line with exactly these
   * fields.
   */
  private static final String[] headerFields = {
    "DatabaseID",
    "DiseaseName",
    "Qualifier",
    "HPO_ID",
    "Reference",
    "Evidence",
    "Onset",
    "Frequency",
    "Sex",
    "Modifier",
    "Aspect",
    "Biocuration"
  };

  /** 1. The diseaseId. For instance, "OMIM:300200". */
  private String databaseId;
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
  /** 13. the biocurator/date, e.g., HPO:skoehler[2018-02-17] */
  private String biocuration;

  private HpoAnnotationLine(String line) throws PhenolException {
    String F[] = line.split("\t");
   if (F.length != headerFields.length) {
     valid_number_of_fields=false;
     throw new PhenolException("[phenol:ERROR] Annotation line with " + F.length + "fields: \""+line+"\"");
   }
    this.databaseId = F[DatabaseId_IDX];
    this.DbObjectName = F[DB_NAME_IDX];
    String phenoId = F[PHENOTYPE_ID_IDX];
    this.phenotypeId = TermId.constructWithPrefix(phenoId);
    String onset = F[ONSET_ID_IDX];
    if (onset != null && onset.startsWith("HP:")) {
      onsetId = TermId.constructWithPrefix(onset);
    }
    this.frequency = F[FREQUENCY_IDX];
    this.sex = F[SEX_IDX];
    String neg = F[QUALIFIER_IDX];
    this.NOT =  (neg != null && neg.equalsIgnoreCase("NOT"));
    this.aspect = F[ASPECT_IDX];
    this.modifierList = F[MODIFIER_IDX];
    this.publication = F[DB_REFERENCE_IDX];
    this.evidence = F[EVIDENCE_IDX];
    this.biocuration = F[BIOCURATION_IDX];
  }

  public static HpoAnnotationLine constructFromString(String line) throws PhenolException {
    try {
      return new HpoAnnotationLine(line);
    } catch (PhenolException e) {
      throw new PhenolException(String.format("Exception [%s] parsing line: %s",e.getMessage(),line));
    }
  }



  /**
   * @param line The header line of a V2 small file
   * @return true iff the fields of the line exactly match {@link #headerFields}.
   */
  static boolean isValidHeaderLine(String line) throws PhenolException {
    String F[] = line.split("\t");
    if (F.length != headerFields.length) {
      String msg = String.format("Expected %d fields in header line but got %d",headerFields.length,F.length);
      throw new PhenolException(msg);
    }
    for (int i = 0; i < headerFields.length; i++) {
      if (!F[i].equals(headerFields[i])) {
        String msg = String.format("Expected header field %d to be %s but got %s",i,headerFields[i],F[i]);
        throw new PhenolException(msg);
      }
    }
    return true;
  }

  final String getDiseaseId() {
    return databaseId;
  }

  /**
   * This function creates a TermId form the Curie-type identifier for the disease, e.g., OMIM:600100.
   * @return The TermId representing this disease.
   * @throws PhenolException if the {@link #databaseId} field is invalid (not OMIM, ORPHA, or DECIPHER)
   */
  TermId getDiseaseTermId() throws PhenolException {
    if (databaseId.startsWith("OMIM") ||
      databaseId.startsWith("ORPHA") ||
      databaseId.startsWith("DECIPHER")
      )
        return TermId.constructWithPrefix(databaseId);
    // we should never get here
    throw new PhenolException("Invalid disease id (not OMIM/ORPHA/DECIPHER: "+databaseId);
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

  List<String> getPublication() {
    ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
    if (publication==null || publication.isEmpty()) return builder.build();
    String A[] = publication.split(";") ;
    for (String a : A ){
      builder.add(a.trim());
    }
    return builder.build();
  }

  String getEvidence() {
    return evidence;
  }

  String getBiocuration() {
    return biocuration;
  }

  String getDbObjectName() {
    return DbObjectName;
  }

  String getAspect() { return this.aspect;}

  /** @return true if this annotation is negated. */
  boolean isNOT() {
    return NOT;
  }

  boolean hasValidNumberOfFields() {
    return valid_number_of_fields;
  }

  @Override
  public String toString() {
    return this.databaseId + "\t" +
      this.DbObjectName + "\t" +
      this.phenotypeId.getIdWithPrefix() + "\t" +
      this.onsetId+ "\t" +
      this.frequency + "\t" +
      this.sex + "\t" +
      this.NOT + "\t" +
      this.aspect + "\t" +
      this.modifierList + "\t" +
      this.publication + "\t" +
      this.evidence + "\t" +
      this.biocuration;
  }
}
