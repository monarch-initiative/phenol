package de.charite.compbio.ontolib.formats.hpo;

import com.google.common.collect.ComparisonChain;
import de.charite.compbio.ontolib.ontology.data.TermAnnotation;
import de.charite.compbio.ontolib.ontology.data.TermID;

// TODO: discuss with Sebastian whether this is the correct naming and description
// TODO: use more enumeration types?
// TODO: make synonym a list?
// TODO: properly parse date?

/**
 * Annotation of a {@link HPOTerm} (via its {@link TermID}) with clinical disease information.
 *
 * <p>
 * Currently, the corresponding file link HPO term IDs with clinical disease information. Objects of
 * this class represent records from the following files:
 * </p>
 *
 * <ul>
 * <li><code>negative_phenotype_annotation.tab</code></li>
 * <li><code>phenotype_annotation.tab</code></li>
 * <li><code>phenotype_annotation_hpoteam.tab</code></li>
 * </ul>
 *
 * <p>
 * The <b>label</b> of this {@link TermAnnotation} is the {@link #getDbObjectID()} (the ID of the
 * disease).
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class HPODiseaseAnnotation implements TermAnnotation {

  /**
   * Enumeration for describing {@link HPODiseaseAnnotation#getDb()}.
   */
  public enum DatabaseSource {
    /** OMIM. */
    OMIM,
    /** Orphanet. */
    ORPHANET,
    /** Other database. */
    OTHER;
  }

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** Database source, e.g., <code>"MIM"</code>. */
  private final String db;

  /** ID in database, e.g., <code>"154700"</code>. */
  private final String dbObjectID;

  /** Name in database, e.g., <code>"Achondrogenesis, type IB"</code>. */
  private final String dbName;

  /** Optional, e.g., <code>"NOT"</code>; optional, <code>null</code> when missing. */
  private final String qualifier;

  /** Term ID into HPO. */
  private final TermID hpoID;

  /** Reference of entry, e.g., <code>OMIM:154700</code> or <code>PMID:15517394</code>. */
  private final String dbReference;

  // TODO: What does that mean?
  /** Evidence code. */
  private final String evidenceCode;

  /** Onset modifier; optional, <code>null</code> when missing. */
  private final String onsetModifier;

  /**
   * Usually from the subontology Frequency or "70%" or "12 of 30"; optional, <code>null</code> when
   * missing.
   */
  private final String frequencyModifier;

  /** Currently not applicable, always empty; optional, <code>null</code> when missing. */
  private final String with;

  /** The annotated ontology, always <code>"O"</code>. */
  private final String aspect;

  /** String with synonym text; optional, <code>null</code> when missing. */
  private final String synonym;

  /** Date of creation/modification. */
  private final String date;

  /** Name of assignment author. */
  private final String assignedBy;

  /**
   * Constructor.
   * 
   * @param db Database source, e.g., <code>"MIM"</code>.
   * @param dbObjectID ID in database, e.g., <code>"154700"</code>.
   * @param dbName Name in database, e.g., <code>"Achondrogenesis, type IB"</code>.
   * @param qualifier Qualifier, e.g., <code>"NOT"</code>; optional, <code>null</code> when missing.
   * @param hpoID Term ID into HPO.
   * @param dbReference Reference of entry, e.g., <code>OMIM:154700</code> or
   *        <code>PMID:15517394</code>.
   * @param evidenceCode Evidence code.
   * @param onsetModifier Onset modifier; optional, <code>null</code> when missing.
   * @param frequencyModifier Usually from the subontology Frequency or "70%" or "12 of 30";
   *        optional, <code>null</code> when missing.
   * @param with Not applicable, always <code>null</code>.
   * @param aspect The annotated ontology, always <code>"O"</code>.
   * @param synonym with synonym text; optional, <code>null</code> when missing.
   * @param date Date of creation/modification.
   * @param assignedBy Name of author.
   */
  public HPODiseaseAnnotation(String db, String dbObjectID, String dbName, String qualifier,
      TermID hpoID, String dbReference, String evidenceCode, String onsetModifier,
      String frequencyModifier, String with, String aspect, String synonym, String date,
      String assignedBy) {
    this.db = db;
    this.dbObjectID = dbObjectID;
    this.dbName = dbName;
    this.qualifier = qualifier;
    this.hpoID = hpoID;
    this.dbReference = dbReference;
    this.evidenceCode = evidenceCode;
    this.onsetModifier = onsetModifier;
    this.frequencyModifier = frequencyModifier;
    this.with = with;
    this.aspect = aspect;
    this.synonym = synonym;
    this.date = date;
    this.assignedBy = assignedBy;
  }

  /**
   * @return Database source, e.g., <code>"MIM"</code>.
   */
  public String getDb() {
    return db;
  }

  /**
   * @return ID in database, e.g., <code>"154700"</code>.
   */
  public String getDbObjectID() {
    return dbObjectID;
  }

  /**
   * @return Name in database, e.g., <code>"Achondrogenesis, type IB"</code>.
   */
  public String getDbName() {
    return dbName;
  }

  /**
   * @return Qualifier, e.g., <code>"NOT"</code>; optional, <code>null</code> when missing.
   */
  public String getQualifier() {
    return qualifier;
  }

  /**
   * @return Term ID into HPO.
   */
  public TermID getHpoID() {
    return hpoID;
  }

  /**
   * @return Reference of entry, e.g., <code>OMIM:154700</code> or <code>PMID:15517394</code>.
   */
  public String getDbReference() {
    return dbReference;
  }

  /**
   * @return Evidence code.
   */
  public String getEvidenceCode() {
    return evidenceCode;
  }

  /**
   * @return Onset modifier; optional, <code>null</code> when missing.
   */
  public String getOnsetModifier() {
    return onsetModifier;
  }

  /**
   * @return Frequency modifier; optional, <code>null</code> when missing.
   */
  public String getFrequencyModifier() {
    return frequencyModifier;
  }

  /**
   * @return Currently not applicable, always empty; optional, <code>null</code> when missing.
   */
  public String getWith() {
    return with;
  }

  /**
   * @return The annotated ontology, always <code>"O"</code>.
   */
  public String getAspect() {
    return aspect;
  }

  /**
   * @return String with synonym text; optional, <code>null</code> when missing.
   */
  public String getSynonym() {
    return synonym;
  }

  /**
   * @return Date of creation/modification.
   */
  public String getDate() {
    return date;
  }

  /**
   * @return Name of author.
   */
  public String getAssignedBy() {
    return assignedBy;
  }

  @Override
  public TermID getTermID() {
    return hpoID;
  }

  @Override
  public String getLabel() {
    return dbObjectID;
  }


  @Override
  public int compareTo(TermAnnotation o) {
    if (!(o instanceof HPODiseaseAnnotation)) {
      throw new RuntimeException("Can only compare HPODiseaseAnnotation with objects of same type");
    }
    HPODiseaseAnnotation that = (HPODiseaseAnnotation) o;

    return ComparisonChain.start().compare(this.db, that.db)
        .compare(this.dbObjectID, that.dbObjectID).compare(this.dbName, that.dbName)
        .compare(this.qualifier, that.qualifier).compare(this.hpoID, that.hpoID)
        .compare(this.dbReference, that.dbReference).compare(this.evidenceCode, that.evidenceCode)
        .compare(this.onsetModifier, that.onsetModifier).compare(this.with, that.with)
        .compare(this.aspect, that.aspect).compare(this.synonym, that.synonym)
        .compare(this.date, that.date).compare(this.assignedBy, that.assignedBy).result();
  }

  @Override
  public String toString() {
    return "HPODiseaseAnnotation [db=" + db + ", dbObjectID=" + dbObjectID + ", dbName=" + dbName
        + ", qualifier=" + qualifier + ", hpoID=" + hpoID + ", dbReference=" + dbReference
        + ", evidenceCode=" + evidenceCode + ", onsetModifier=" + onsetModifier
        + ", frequencyModifier=" + frequencyModifier + ", with=" + with + ", aspect=" + aspect
        + ", synonym=" + synonym + ", date=" + date + ", assignedBy=" + assignedBy + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((aspect == null) ? 0 : aspect.hashCode());
    result = prime * result + ((assignedBy == null) ? 0 : assignedBy.hashCode());
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + ((db == null) ? 0 : db.hashCode());
    result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
    result = prime * result + ((dbObjectID == null) ? 0 : dbObjectID.hashCode());
    result = prime * result + ((dbReference == null) ? 0 : dbReference.hashCode());
    result = prime * result + ((evidenceCode == null) ? 0 : evidenceCode.hashCode());
    result = prime * result + ((frequencyModifier == null) ? 0 : frequencyModifier.hashCode());
    result = prime * result + ((hpoID == null) ? 0 : hpoID.hashCode());
    result = prime * result + ((onsetModifier == null) ? 0 : onsetModifier.hashCode());
    result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
    result = prime * result + ((synonym == null) ? 0 : synonym.hashCode());
    result = prime * result + ((with == null) ? 0 : with.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    HPODiseaseAnnotation other = (HPODiseaseAnnotation) obj;
    if (aspect == null) {
      if (other.aspect != null) return false;
    } else if (!aspect.equals(other.aspect)) return false;
    if (assignedBy == null) {
      if (other.assignedBy != null) return false;
    } else if (!assignedBy.equals(other.assignedBy)) return false;
    if (date == null) {
      if (other.date != null) return false;
    } else if (!date.equals(other.date)) return false;
    if (db == null) {
      if (other.db != null) return false;
    } else if (!db.equals(other.db)) return false;
    if (dbName == null) {
      if (other.dbName != null) return false;
    } else if (!dbName.equals(other.dbName)) return false;
    if (dbObjectID == null) {
      if (other.dbObjectID != null) return false;
    } else if (!dbObjectID.equals(other.dbObjectID)) return false;
    if (dbReference == null) {
      if (other.dbReference != null) return false;
    } else if (!dbReference.equals(other.dbReference)) return false;
    if (evidenceCode == null) {
      if (other.evidenceCode != null) return false;
    } else if (!evidenceCode.equals(other.evidenceCode)) return false;
    if (frequencyModifier == null) {
      if (other.frequencyModifier != null) return false;
    } else if (!frequencyModifier.equals(other.frequencyModifier)) return false;
    if (hpoID == null) {
      if (other.hpoID != null) return false;
    } else if (!hpoID.equals(other.hpoID)) return false;
    if (onsetModifier == null) {
      if (other.onsetModifier != null) return false;
    } else if (!onsetModifier.equals(other.onsetModifier)) return false;
    if (qualifier == null) {
      if (other.qualifier != null) return false;
    } else if (!qualifier.equals(other.qualifier)) return false;
    if (synonym == null) {
      if (other.synonym != null) return false;
    } else if (!synonym.equals(other.synonym)) return false;
    if (with == null) {
      if (other.with != null) return false;
    } else if (!with.equals(other.with)) return false;
    return true;
  }

}
