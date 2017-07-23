package com.github.phenomics.ontolib.formats.hpo;

import com.github.phenomics.ontolib.ontology.data.TermAnnotation;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.google.common.collect.ComparisonChain;

import java.util.Optional;

// TODO: discuss with Sebastian whether this is the correct naming and description
// TODO: use more enumeration types?
// TODO: make synonym a list?
// TODO: properly parse date?

/**
 * Annotation of a {@link HpoTerm} (via its {@link TermId}) with clinical disease information.
 *
 * <p>
 * Currently, the corresponding file link HPO term Ids with clinical disease information. Objects of
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
 * The <b>label</b> of this {@link TermAnnotation} is the {@link #getDbObjectId()} (the Id of the
 * disease).
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class HpoDiseaseAnnotation implements TermAnnotation {

  /**
   * Enumeration for describing {@link HpoDiseaseAnnotation#getDb()}.
   */
  public enum DatabaseSource {
    /** Decipher. */
    DECIPHER,
    /** OMIM. */
    OMIM,
    /** Orphanet. */
    ORPHANET,
    /** Other database. */
    OTHER;
  }

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /** Database source, e.g., <code>"MIM"</code>. */
  private final String db;

  /** Id in database, e.g., <code>"154700"</code>. */
  private final String dbObjectId;

  /** Name in database, e.g., <code>"Achondrogenesis, type IB"</code>. */
  private final String dbName;

  /** Optional, e.g., <code>"NOT"</code>; optional, <code>null</code> when missing. */
  private final String qualifier;

  /** Term Id into HPO. */
  private final TermId hpoId;

  /** Reference of entry, e.g., <code>OMIM:154700</code> or <code>PMId:15517394</code>. */
  private final String dbReference;

  /** Evidence description. */
  private final String evidenceDescription;

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
   * @param dbObjectId Id in database, e.g., <code>"154700"</code>.
   * @param dbName Name in database, e.g., <code>"Achondrogenesis, type IB"</code>.
   * @param qualifier Qualifier, e.g., <code>"NOT"</code>; optional, <code>null</code> when missing.
   * @param hpoId Term Id into HPO.
   * @param dbReference Reference of entry, e.g., <code>OMIM:154700</code> or
   *        <code>PMId:15517394</code>.
   * @param evidenceDescription Evidence description.
   * @param onsetModifier Onset modifier; optional, <code>null</code> when missing.
   * @param frequencyModifier Usually from the subontology Frequency or "70%" or "12 of 30";
   *        optional, <code>null</code> when missing.
   * @param with Not applicable, always <code>null</code>.
   * @param aspect The annotated ontology, always <code>"O"</code>.
   * @param synonym with synonym text; optional, <code>null</code> when missing.
   * @param date Date of creation/modification.
   * @param assignedBy Name of author.
   */
  public HpoDiseaseAnnotation(String db, String dbObjectId, String dbName, String qualifier,
      TermId hpoId, String dbReference, String evidenceDescription, String onsetModifier,
      String frequencyModifier, String with, String aspect, String synonym, String date,
      String assignedBy) {
    this.db = db;
    this.dbObjectId = dbObjectId;
    this.dbName = dbName;
    this.qualifier = qualifier;
    this.hpoId = hpoId;
    this.dbReference = dbReference;
    this.evidenceDescription = evidenceDescription;
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
   * @return Id in database, e.g., <code>"154700"</code>.
   */
  public String getDbObjectId() {
    return dbObjectId;
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
   * @return Term Id into HPO.
   */
  public TermId getHpoId() {
    return hpoId;
  }

  /**
   * @return Reference of entry, e.g., <code>OMIM:154700</code> or <code>PMId:15517394</code>.
   */
  public String getDbReference() {
    return dbReference;
  }

  /**
   * @return Evidence description.
   */
  public String getEvidenceDescription() {
    return evidenceDescription;
  }

  @Override
  public Optional<String> getEvidenceCode() {
    return Optional.of(evidenceDescription);
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
  public TermId getTermId() {
    return hpoId;
  }

  @Override
  public String getLabel() {
    return dbObjectId;
  }

  @Override
  public int compareTo(TermAnnotation o) {
    if (!(o instanceof HpoDiseaseAnnotation)) {
      throw new RuntimeException("Can only compare HPODiseaseAnnotation with objects of same type");
    }
    HpoDiseaseAnnotation that = (HpoDiseaseAnnotation) o;

    return ComparisonChain.start().compare(this.db, that.db)
        .compare(this.dbObjectId, that.dbObjectId).compare(this.dbName, that.dbName)
        .compare(this.qualifier, that.qualifier).compare(this.hpoId, that.hpoId)
        .compare(this.dbReference, that.dbReference)
        .compare(this.evidenceDescription, that.evidenceDescription)
        .compare(this.onsetModifier, that.onsetModifier).compare(this.with, that.with)
        .compare(this.aspect, that.aspect).compare(this.synonym, that.synonym)
        .compare(this.date, that.date).compare(this.assignedBy, that.assignedBy).result();
  }

  @Override
  public String toString() {
    return "HPODiseaseAnnotation [db=" + db + ", dbObjectId=" + dbObjectId + ", dbName=" + dbName
        + ", qualifier=" + qualifier + ", hpoId=" + hpoId + ", dbReference=" + dbReference
        + ", evidenceDescription=" + evidenceDescription + ", onsetModifier=" + onsetModifier
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
    result = prime * result + ((dbObjectId == null) ? 0 : dbObjectId.hashCode());
    result = prime * result + ((dbReference == null) ? 0 : dbReference.hashCode());
    result = prime * result + ((evidenceDescription == null) ? 0 : evidenceDescription.hashCode());
    result = prime * result + ((frequencyModifier == null) ? 0 : frequencyModifier.hashCode());
    result = prime * result + ((hpoId == null) ? 0 : hpoId.hashCode());
    result = prime * result + ((onsetModifier == null) ? 0 : onsetModifier.hashCode());
    result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
    result = prime * result + ((synonym == null) ? 0 : synonym.hashCode());
    result = prime * result + ((with == null) ? 0 : with.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    HpoDiseaseAnnotation other = (HpoDiseaseAnnotation) obj;
    if (aspect == null) {
      if (other.aspect != null) {
        return false;
      }
    } else if (!aspect.equals(other.aspect)) {
      return false;
    }
    if (assignedBy == null) {
      if (other.assignedBy != null) {
        return false;
      }
    } else if (!assignedBy.equals(other.assignedBy)) {
      return false;
    }
    if (date == null) {
      if (other.date != null) {
        return false;
      }
    } else if (!date.equals(other.date)) {
      return false;
    }
    if (db == null) {
      if (other.db != null) {
        return false;
      }
    } else if (!db.equals(other.db)) {
      return false;
    }
    if (dbName == null) {
      if (other.dbName != null) {
        return false;
      }
    } else if (!dbName.equals(other.dbName)) {
      return false;
    }
    if (dbObjectId == null) {
      if (other.dbObjectId != null) {
        return false;
      }
    } else if (!dbObjectId.equals(other.dbObjectId)) {
      return false;
    }
    if (dbReference == null) {
      if (other.dbReference != null) {
        return false;
      }
    } else if (!dbReference.equals(other.dbReference)) {
      return false;
    }
    if (evidenceDescription == null) {
      if (other.evidenceDescription != null) {
        return false;
      }
    } else if (!evidenceDescription.equals(other.evidenceDescription)) {
      return false;
    }
    if (frequencyModifier == null) {
      if (other.frequencyModifier != null) {
        return false;
      }
    } else if (!frequencyModifier.equals(other.frequencyModifier)) {
      return false;
    }
    if (hpoId == null) {
      if (other.hpoId != null) {
        return false;
      }
    } else if (!hpoId.equals(other.hpoId)) {
      return false;
    }
    if (onsetModifier == null) {
      if (other.onsetModifier != null) {
        return false;
      }
    } else if (!onsetModifier.equals(other.onsetModifier)) {
      return false;
    }
    if (qualifier == null) {
      if (other.qualifier != null) {
        return false;
      }
    } else if (!qualifier.equals(other.qualifier)) {
      return false;
    }
    if (synonym == null) {
      if (other.synonym != null) {
        return false;
      }
    } else if (!synonym.equals(other.synonym)) {
      return false;
    }
    if (with == null) {
      if (other.with != null) {
        return false;
      }
    } else if (!with.equals(other.with)) {
      return false;
    }
    return true;
  }

}
