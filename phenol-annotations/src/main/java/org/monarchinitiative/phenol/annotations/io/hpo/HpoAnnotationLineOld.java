package org.monarchinitiative.phenol.annotations.io.hpo;

import org.monarchinitiative.phenol.annotations.formats.hpo.HpoDisease;
import org.monarchinitiative.phenol.annotations.formats.hpo.HpoFrequency;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This class represents one line of the V2 (post 2018) HPO annotation line from the "big file"
 * (phenotype.hpoa). It is intended to be use as part of the processing of the big file. A
 * convenience class that will allow us to collect the annotation lines for each disease that we
 * want to parse; from these data, we will construct the {@link HpoDisease}. Note that
 * this class performs only the first part of the parse. The class {@link
 * HpoDiseaseLoader} will complete the input of the lines once all of the lines for some
 * disease have been input into individual {@link HpoAnnotationLineOld} objects. The reason for this is
 * that in some cases, we may have multiple lines for some annotation and we will want to combine
 * them into one annotation for some computational disease models. The class checks whether the
 * number of fields is correct, and this can be got with {@link #hasValidNumberOfFields}
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
@Deprecated
class HpoAnnotationLineOld {

  private static final String DEFAULT_FREQUENCY_STRING="n/a";

  private static final String EMPTY_STRING="";


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
  private final String databaseId;
  /** 3. The disease name, e.g., Marfan syndrome . */
  private final String databaseObjectName;
  /** 4. true is this is a negated annotation, i.e., some phenotype is not present in some disease.*/
  private final boolean isNegated;
  /** 5. The phenotype term id */
  private final String phenotypeId;
  /** 6. Publication */
  private final String publication;
  /** 7. Evidence about assertion */
  private final String evidence;
  /** 8. The onset (can be null) */
  private final String onsetId;
  /** 9. The frequency (can be null) */
  private final String frequency;
  /** 10. Male, female */
  private final String sex;
  /** 11. Modifier terms (0..n) */
  private final String modifiers;
  /** 12. aspect */
  @SuppressWarnings("unused")
  private final String aspect;
  /** 13. the biocurator/date, e.g., HPO:skoehler[2018-02-17] */
  private final String biocuration;

  private HpoAnnotationLineOld(String line) throws PhenolException {
    String[] fields = line.split("\t");
    if (fields.length != headerFields.length) {
      valid_number_of_fields = false;
      throw new PhenolException("[phenol:ERROR] Annotation line with " + fields.length + "fields: \"" + line + "\"");
    }

    this.databaseId = fields[0];
    this.databaseObjectName = fields[1];
    this.isNegated = fields[2].equalsIgnoreCase("NOT");
    this.phenotypeId = fields[3];
    this.publication = fields[4];
    this.evidence = fields[5];
    this.onsetId = fields[6];
    this.frequency = fields[7];
    this.sex = fields[8];
    this.modifiers = fields[9];
    this.aspect = fields[10];
    this.biocuration = fields[11];
  }

  static HpoAnnotationLineOld constructFromString(String line) throws PhenolException {
    try {
      return new HpoAnnotationLineOld(line);
    } catch (PhenolException e) {
      throw new PhenolException(String.format("Exception [%s] parsing line: %s",e.getMessage(), line));
    }
  }

  /**
   * @param line The header line of a V2 small file
   * @return true iff the fields of the line exactly match {@link #headerFields}.
   */
  static boolean isValidHeaderLine(String line) throws PhenolException {
    String[] fields = line.split("\t");
    if (fields.length != headerFields.length) {
      String msg = String.format("Expected %d fields in header line but got %d",headerFields.length, fields.length);
      throw new PhenolException(msg);
    }
    for (int i = 0; i < headerFields.length; i++) {
      if (!fields[i].equals(headerFields[i])) {
        String msg = String.format("Expected header field %d to be %s but got %s",i, headerFields[i], fields[i]);
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
  Optional<TermId> getDiseaseTermId() {
    if (databaseId.startsWith("OMIM")
      || databaseId.startsWith("ORPHA")
      || databaseId.startsWith("DECIPHER"))
        return Optional.of(TermId.of(databaseId));
    return Optional.empty();
  }

  String getPhenotypeId() {
    return phenotypeId;
  }

  String getOnsetId() {
    return onsetId;
  }

  String getFrequency() {
    return frequency;
  }

  String getSex() {
    return sex;
  }

  String modifiers() {
    return modifiers;
  }

  List<String> getPublication() {
    if (publication == null || publication.isEmpty())
      return List.of();

    String[] tokens = publication.split(";") ;
    List<String> builder = new ArrayList<>(tokens.length);
    for (String a : tokens ){
      builder.add(a.trim());
    }
    return Collections.unmodifiableList(builder);
  }

  String getEvidence() {
    return evidence;
  }

  String getBiocuration() {
    return biocuration;
  }

  String getDatabaseObjectName() {
    return databaseObjectName;
  }

  String getAspect() { return this.aspect;}

  /** @return true if this annotation is negated. */
  boolean isNOT() {
    return isNegated;
  }

  boolean hasValidNumberOfFields() {
    return valid_number_of_fields;
  }

  /**
   * This method can be used to create a full HpoAnnotation object. Note that it is not
   * intended to be used to parse annotation lines that represent models of inheritance or
   * negative annotations. For this reason, the method is package-private and should only be
   * called by {@link org.monarchinitiative.phenol.annotations.assoc.HpoAssociationLoader}.
   * @param line AN HpoAnnotationLinbe object representing opne phenotype annotation
   * @param ontology reference to HPO Ontology object
   * @return corresponding HpoAnnotation object
   */
  @Deprecated
   static HpoAnnotationLine toHpoAnnotation(HpoAnnotationLineOld line, Ontology ontology) {
//    String phenoId = line.getPhenotypeId();
//    double frequency = annotationFrequency(line.annotationFrequency(), ontology);
//    return HpoAnnotation.of(TermId.of(phenoId), frequency)
//      .frequency(frequency)
//      .onset(HpoOnset.fromHpoIdString(line.getOnsetId()).orElse(null))
//      .modifiers(modifiers(line.modifiers()))
//      .citations(line.getPublication())
//      .evidence(line.getEvidence())
//      .build();
    throw new RuntimeException("Not supported anymore!");
  }

  /**
   * @param lst List of semicolon separated HPO term ids from the modifier subontology
   * @return Immutable List of {@link TermId} objects
   */
  private static List<TermId> getModifiers(String lst) {
    if (lst == null || lst.isEmpty())
      return List.of();

    String[] modifierTermStrings = lst.split(";");
    List<TermId> builder = new ArrayList<>(modifierTermStrings.length);
    for (String mt : modifierTermStrings) {
      TermId mtId = TermId.of(mt.trim());
      builder.add(mtId);
    }
    return Collections.unmodifiableList(builder);
  }

  /**
   * Extract the {@link HpoFrequency} object that corresponds to the frequency modifier in an
   * annotation line. Note that we are expecting there to be one of three kinds of frequency
   * information (an HPO term, n/m or x%). If we find nothing or there is some parsing error,
   * return the default frequency (obligate, 100%).
   *
   * @param freq The representation of the frequency, if any, in the {@code
   *             phenotype_annotation.tab} file
   * @return the corresponding {@link HpoFrequency} object or the default {@link HpoFrequency}
   * object (100%).
   */
  private static double getFrequency(String freq, Ontology ontology) {
    if (freq == null || freq.isEmpty()) {
      return HpoFrequency.OBLIGATE.frequency();
    }
    int i = freq.indexOf('%');
    if (i > 0) {
      return 0.01 * Double.parseDouble(freq.substring(0, i));
    }
    i = freq.indexOf('/');
    if (i > 0 && freq.length() > (i + 1)) {
      int n = Integer.parseInt(freq.substring(0, i));
      int m = Integer.parseInt(freq.substring(i + 1));
      return (double) n / (double) m;
    }
    try {
      TermId tid = string2TermId(freq, ontology);
      if (tid != null)
        try {
          return HpoFrequency.fromTermId(tid).frequency();
        } catch (IllegalArgumentException e) {
          return HpoFrequency.OBLIGATE.frequency();
        }
    } catch (Exception e) {
      e.printStackTrace();
    }
    // if we get here we could not parse the Frequency, return the default 100%
    return HpoFrequency.OBLIGATE.frequency();
  }

  /**
   * Go from HP:0000123 to the corresponding TermId
   *
   * @param hp String version of an HPO term id
   * @return corresponding {@link TermId} object
   */
  private static TermId string2TermId(String hp, Ontology ontology) {
    if (!hp.startsWith("HP:")) {
      return null;
    } else {
      TermId tid = TermId.of(hp);
      if (ontology.getTermMap().containsKey(tid)) {
        return ontology
          .getTermMap()
          .get(tid)
          .id(); // replace alt_id with current if necessary
      } else {
        return null;
      }
    }
  }

  @Override
  public String toString() {
    String [] fields={this.databaseId,
      this.databaseObjectName,
      this.phenotypeId ,
      this.onsetId==null?EMPTY_STRING:this.onsetId,
      this.frequency ,
      this.sex ,
      this.isNegated ?"NOT":"" ,
      this.aspect,
      this.modifiers,
      this.publication,
      this.evidence ,
      this.biocuration};
    return String.join("\t",fields);
  }
}
