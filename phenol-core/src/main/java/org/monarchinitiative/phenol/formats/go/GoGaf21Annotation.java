package org.monarchinitiative.phenol.formats.go;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Record from GAF v2.1 file.
 *
 * <p>The <b>label</b> of this {@link TermAnnotation} is the <code>"${DB}:${DB_Object_ID}"</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class GoGaf21Annotation implements TermAnnotation {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** Database source, e.g., <code>"UniProtKB"</code>. */
  private final String db;

  /** Id in database, e.g., <code>"P12345"</code>. */
  private final String dbObjectId;

  /** Symbol in database, e.g., <code>"PHO3"</code>. */
  private final String dbObjectSymbol;

  /** Optional, e.g., <code>"NOT"</code>; optional, <code>null</code> when missing. */
  private final String qualifier;

  /** GO ID, e.g., <code>"GO:0003993"</code>. */
  private final TermId goId;

  // TODO: list of String?
  /** Reference of entry, e.g., <code>PMID:2676709</code>. */
  private final String dbReference;

  // TODO: What does that mean?
  /** Evidence code, e.g., <code>"IMP"</code>. */
  private final String evidenceCode;

  /** With (or) From, e.g., <code>"GO:0000346"</code>; optional, <code>null</code> when missing. */
  private final String with;

  /** The annotated ontology, e.g., <code>"F"</code>. */
  private final String aspect;

  /** The DB object name; optional, <code>null</code> when missing. */
  private final String dbObjectName;

  // TODO: convert to list?
  /** The DB object synonym; optional, <code>null</code> when missing. */
  private final String dbObjectSynonym;

  /** The DB object type, e.g., <code>"protein"</code>. */
  private final String dbObjectType;

  /** Taxon(s), of cardinality 1 or 2. */
  private final List<String> taxons;

  /** Date. */
  private final Date date;

  /** Assignment author. */
  private final String assignedBy;

  /** Annotation extension; optional, <code>null</code> when missing. */
  private final String annotationExtension;

  /** Gene product form ID; ; optional, <code>null</code> when missing. */
  private final String geneProductFormId;

  /**
   * Constructor.
   *
   * @param db Database source, e.g., <code>"UniProtKB"</code>.
   * @param dbObjectId Id in database, e.g., <code>"P12345"</code>.
   * @param dbObjectSymbol Symbol in database, e.g., <code>"PHO3"</code>.
   * @param qualifier Optional, e.g., <code>"NOT"</code>; optional, <code>null</code> when missing.
   * @param goId GO ID, e.g., <code>"GO:0003993"</code>.
   * @param dbReference Reference of entry, e.g., <code>PMID:2676709</code>.
   * @param evidenceCode Evidence code, e.g., <code>"IMP"</code>.
   * @param with With (or) From, e.g., <code>"GO:0000346"</code>; optional, <code>null</code> when
   *     missing.
   * @param aspect The annotated ontology, e.g., <code>"F"</code>.
   * @param dbObjectName The DB object name; optional, <code>null</code> when missing.
   * @param dbObjectSynonym The DB object synonym; optional, <code>null</code> when missing.
   * @param dbObjectType The DB object type, e.g., <code>"protein"</code>.
   * @param taxons Taxon(s), of cardinality 1 or 2.
   * @param date Assignment date.
   * @param assignedBy Assignment author.
   * @param annotationExtension Annotation extension; optional, <code>null</code> when missing.
   * @param geneProductFormId Gene product form ID; ; optional, <code>null</code> when missing.
   */
  public GoGaf21Annotation(
      String db,
      String dbObjectId,
      String dbObjectSymbol,
      String qualifier,
      TermId goId,
      String dbReference,
      String evidenceCode,
      String with,
      String aspect,
      String dbObjectName,
      String dbObjectSynonym,
      String dbObjectType,
      List<String> taxons,
      Date date,
      String assignedBy,
      String annotationExtension,
      String geneProductFormId) {
    this.db = db;
    this.dbObjectId = dbObjectId;
    this.dbObjectSymbol = dbObjectSymbol;
    this.qualifier = qualifier;
    this.goId = goId;
    this.dbReference = dbReference;
    this.evidenceCode = evidenceCode;
    this.with = with;
    this.aspect = aspect;
    this.dbObjectName = dbObjectName;
    this.dbObjectSynonym = dbObjectSynonym;
    this.dbObjectType = dbObjectType;
    this.taxons = taxons;
    this.date = date;
    this.assignedBy = assignedBy;
    this.annotationExtension = annotationExtension;
    this.geneProductFormId = geneProductFormId;
  }

  /** @return The database name. */
  public String getDb() {
    return db;
  }

  /** @return The object's ID in the database. */
  public String getDbObjectId() {
    return dbObjectId;
  }

  /** @return dbObjectId as phenol {@link TermId}. Note that in some cases, the GO
   * annotation files use a prefix with this field, e.g., MGI:123, and in other cases, they
   * do not, e.g., Uniprot. In the latter case, we can combine the Database field (UniProtKB)
   * and the dbObjectId field (e.g., Q14469) to create a TermId.
   */
  public TermId getDbObjectIdAsTermId() {
    return dbObjectId.contains(":") ?
      TermId.constructWithPrefix(dbObjectId) :
      new TermId(new TermPrefix(db),dbObjectId);
  }

  /** @return The object's symbol in the database. */
  public String getDbObjectSymbol() {
    return dbObjectSymbol;
  }

  /** @return The object's qualifier. */
  public String getQualifier() {
    return qualifier;
  }

  /** @return The GO term ID. */
  public TermId getGoId() {
    return goId;
  }

  /** @return The database reference. */
  public String getDbReference() {
    return dbReference;
  }

  @Override
  public Optional<String> getEvidenceCode() {
    return Optional.ofNullable(evidenceCode);
  }

  /** @return The "with (or) from" value. */
  public String getWith() {
    return with;
  }

  /** @return The aspect value. */
  public String getAspect() {
    return aspect;
  }

  /** @return The database object name. */
  public String getDbObjectName() {
    return dbObjectName;
  }

  /** @return The database object synonym. */
  public String getDbObjectSynonym() {
    return dbObjectSynonym;
  }

  /** @return The database object type. */
  public String getDbObjectType() {
    return dbObjectType;
  }

  /** @return The taxons, cardinality 1 or 2. */
  public List<String> getTaxons() {
    return taxons;
  }

  /** @return The date. */
  public Date getDate() {
    return date;
  }

  /** @return The "assigned by" value. */
  public String getAssignedBy() {
    return assignedBy;
  }

  /** @return The annotation extension value. */
  public String getAnnotationExtension() {
    return annotationExtension;
  }

  /** @return The gene product form ID. */
  public String getGeneProductFormId() {
    return geneProductFormId;
  }

  @Override
  public TermId getTermId() {
    return goId;
  }

  @Override
  public String getLabel() {
    return db + ":" + dbObjectId;
  }

  @Override
  public int compareTo(TermAnnotation o) {
    if (!(o instanceof GoGaf21Annotation)) {
      throw new PhenolRuntimeException(
          "Can only compare GoGaf21Annotation with objects of same type");
    }
    GoGaf21Annotation that = (GoGaf21Annotation) o;

    return ComparisonChain.start()
        .compare(this.annotationExtension, that.annotationExtension)
        .compare(this.aspect, that.aspect)
        .compare(this.assignedBy, that.assignedBy)
        .compare(this.date, that.date)
        .compare(this.db, that.db)
        .compare(this.dbObjectId, that.dbObjectId)
        .compare(this.dbObjectName, that.dbObjectName)
        .compare(this.dbObjectSymbol, that.dbObjectSymbol)
        .compare(this.dbObjectSynonym, that.dbObjectSynonym)
        .compare(this.dbObjectType, that.dbObjectType)
        .compare(this.dbReference, that.dbReference)
        .compare(this.evidenceCode, that.evidenceCode)
        .compare(this.geneProductFormId, that.geneProductFormId)
        .compare(this.goId, that.goId)
        .compare(this.qualifier, that.qualifier)
        .compare(this.taxons, that.taxons, Ordering.<String>natural().lexicographical())
        .compare(this.with, that.with)
        .result();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((annotationExtension == null) ? 0 : annotationExtension.hashCode());
    result = prime * result + ((aspect == null) ? 0 : aspect.hashCode());
    result = prime * result + ((assignedBy == null) ? 0 : assignedBy.hashCode());
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + ((db == null) ? 0 : db.hashCode());
    result = prime * result + ((dbObjectId == null) ? 0 : dbObjectId.hashCode());
    result = prime * result + ((dbObjectName == null) ? 0 : dbObjectName.hashCode());
    result = prime * result + ((dbObjectSymbol == null) ? 0 : dbObjectSymbol.hashCode());
    result = prime * result + ((dbObjectSynonym == null) ? 0 : dbObjectSynonym.hashCode());
    result = prime * result + ((dbObjectType == null) ? 0 : dbObjectType.hashCode());
    result = prime * result + ((dbReference == null) ? 0 : dbReference.hashCode());
    result = prime * result + ((evidenceCode == null) ? 0 : evidenceCode.hashCode());
    result = prime * result + ((geneProductFormId == null) ? 0 : geneProductFormId.hashCode());
    result = prime * result + ((goId == null) ? 0 : goId.hashCode());
    result = prime * result + ((qualifier == null) ? 0 : qualifier.hashCode());
    result = prime * result + ((taxons == null) ? 0 : taxons.hashCode());
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
    GoGaf21Annotation other = (GoGaf21Annotation) obj;
    if (annotationExtension == null) {
      if (other.annotationExtension != null) {
        return false;
      }
    } else if (!annotationExtension.equals(other.annotationExtension)) {
      return false;
    }
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
    if (dbObjectId == null) {
      if (other.dbObjectId != null) {
        return false;
      }
    } else if (!dbObjectId.equals(other.dbObjectId)) {
      return false;
    }
    if (dbObjectName == null) {
      if (other.dbObjectName != null) {
        return false;
      }
    } else if (!dbObjectName.equals(other.dbObjectName)) {
      return false;
    }
    if (dbObjectSymbol == null) {
      if (other.dbObjectSymbol != null) {
        return false;
      }
    } else if (!dbObjectSymbol.equals(other.dbObjectSymbol)) {
      return false;
    }
    if (dbObjectSynonym == null) {
      if (other.dbObjectSynonym != null) {
        return false;
      }
    } else if (!dbObjectSynonym.equals(other.dbObjectSynonym)) {
      return false;
    }
    if (dbObjectType == null) {
      if (other.dbObjectType != null) {
        return false;
      }
    } else if (!dbObjectType.equals(other.dbObjectType)) {
      return false;
    }
    if (dbReference == null) {
      if (other.dbReference != null) {
        return false;
      }
    } else if (!dbReference.equals(other.dbReference)) {
      return false;
    }
    if (evidenceCode == null) {
      if (other.evidenceCode != null) {
        return false;
      }
    } else if (!evidenceCode.equals(other.evidenceCode)) {
      return false;
    }
    if (geneProductFormId == null) {
      if (other.geneProductFormId != null) {
        return false;
      }
    } else if (!geneProductFormId.equals(other.geneProductFormId)) {
      return false;
    }
    if (goId == null) {
      if (other.goId != null) {
        return false;
      }
    } else if (!goId.equals(other.goId)) {
      return false;
    }
    if (qualifier == null) {
      if (other.qualifier != null) {
        return false;
      }
    } else if (!qualifier.equals(other.qualifier)) {
      return false;
    }
    if (taxons == null) {
      if (other.taxons != null) {
        return false;
      }
    } else if (!taxons.equals(other.taxons)) {
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

  @Override
  public String toString() {
    return "GoGaf21Annotation [db="
        + db
        + ", dbObjectId="
        + dbObjectId
        + ", dbObjectSymbol="
        + dbObjectSymbol
        + ", qualifier="
        + qualifier
        + ", goId="
        + goId
        + ", dbReference="
        + dbReference
        + ", evidenceCode="
        + evidenceCode
        + ", with="
        + with
        + ", aspect="
        + aspect
        + ", dbObjectName="
        + dbObjectName
        + ", dbObjectSynonym="
        + dbObjectSynonym
        + ", dbObjectType="
        + dbObjectType
        + ", taxons="
        + taxons
        + ", date="
        + date
        + ", assignedBy="
        + assignedBy
        + ", annotationExtension="
        + annotationExtension
        + ", geneProductFormId="
        + geneProductFormId
        + "]";
  }
}
