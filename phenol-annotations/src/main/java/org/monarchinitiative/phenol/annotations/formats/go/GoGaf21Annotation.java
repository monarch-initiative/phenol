package org.monarchinitiative.phenol.annotations.formats.go;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermAnnotation;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 *
 *
 * A GoGAF line can have 15 or 17 fields:
 * <ol>
 * <li>db Database source, e.g., <code>"UniProtKB"</code>.</li>
 * <li>dbObjectId Id in database, e.g., <code>"P12345"</code>.</li>
 * <li>dbObjectSymbol Symbol in database, e.g., <code>"PHO3"</code>.</li>
 * <li>qualifier Optional, e.g., <code>"NOT"</code>; optional, <code>null</code> when missing.</li>
 * <li>goId GO ID, e.g., <code>"GO:0003993"</code>.</li>
 * <li>dbReference Reference of entry, e.g., <code>PMID:2676709</code>.</li>
 * <li>evidenceCode Evidence code, e.g., <code>"IMP"</code>.</li>
 * <li>with With (or) From, e.g., <code>"GO:0000346"</code>; optional, <code>null</code> when missing.</li>
 * <li>aspect The annotated ontology, e.g., <code>"F"</code>.</li>
 * <li>dbObjectName The DB object name; optional, <code>null</code> when missing.</li>
 * <li>dbObjectSynonym The DB object synonym; optional, <code>null</code> when missing.</li>
 * <li>dbObjectType The DB object type, e.g., <code>"protein"</code>.</li>
 * <li>taxons Taxon(s), of cardinality 1 or 2.</li>
 * <li>date Assignment date.</li>
 * <li>assignedBy Assignment author.</li>
 * <li>annotationExtension Annotation extension; optional, <code>null</code> when missing.</li>
 * <li>geneProductFormId Gene product form ID; ; optional, <code>null</code> when missing.</li>
 * </ol>
 */
public final class GoGaf21Annotation implements TermAnnotation {
  /** The {@link TermId} of the database object being annotated. Includes both the "db" (which is the
   * first column of the GO annotation file) as well as the dbObjectId (which is the second column in
   * the GO annotation file). Note that GO is inconsistent, and sometimes the dbObjectId column
   * also includes the database, e.g., the first two columns are "MGI","MGI:1918911". The constructor
   * of this class sorts things out.
   */
  private final TermId dbObjectTermId;

  /** Symbol in database, e.g., <code>"PHO3"</code>. */
  private final String dbObjectSymbol;

  /** Optional, e.g., <code>"NOT"</code>; optional, <code>null</code> when missing. */
  private final String qualifier;

  /** GO ID, e.g., <code>"GO:0003993"</code>. */
  private final TermId goId;

  /** Reference of entry, e.g., <code>PMID:2676709</code>. */
  private final String dbReference;

  /** Evidence code, e.g., <code>"IMP"</code>. */
  private final String evidenceCode;

  /** With (or) From, e.g., <code>"GO:0000346"</code>; optional, <code>null</code> when missing. */
  private final String with;

  /** The annotated ontology, e.g., <code>"F"</code>. */
  private final String aspect;

  /** The DB object name; optional, <code>null</code> when missing. */
  private final String dbObjectName;

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
   * Construct an annotation from one Gene Ontology annotation line
   * Note that the lines can have 15 or 17 fields (the latter is the extended format).
   * @param arr Array containing the fields of a single GO annotation line
   * @throws PhenolException if the length of arr is not 15 or 17
   */
  private GoGaf21Annotation(String [] arr) throws PhenolException {

    if (arr[1].contains(":")) {
      this.dbObjectTermId = TermId.of(arr[1]);
    } else {
      this.dbObjectTermId = TermId.of(arr[0], arr[1]);
    }

    this.dbObjectSymbol = arr[2];
    this.qualifier = arr[3];
    this.goId = TermId.of(arr[4]);
    this.dbReference = arr[5];
    this.evidenceCode = arr[6];
    this.with = arr[7];
    this.aspect = arr[8];
    this.dbObjectName = arr[9];
    this.dbObjectSynonym = arr[10];
    this.dbObjectType = arr[11];
    this.taxons = Arrays.asList(arr[12].split("\\|"));
    final String dateStr = arr[13];
    this.assignedBy = arr[14];
    this.annotationExtension = (arr.length < 16) ? null : arr[15];
    this.geneProductFormId = (arr.length < 17) ? null : arr[16];

    final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    try {
      this.date = format.parse(dateStr);
    } catch (ParseException e) {
      throw new PhenolException(
        "There was a problem parsing the date value " + dateStr, e);
    }
  }


  public static GoGaf21Annotation parseAnnotation(String line) throws PhenolException {
    final String[] arr = line.split("\t");
    if (arr.length < 15 || arr.length > 17) {
      throw new PhenolException(
        "GAF line had "
          + arr.length
          + " columns, but expected between 15 and 17 entries. \n Line was:"
          + String.join("\t", arr));
    }
    return new GoGaf21Annotation(arr);
  }




  /** @return The database name. */
  public String getDb() {
    return this.dbObjectTermId.getPrefix();
  }

  /** @return The object's ID in the database. */
  public String getDbObjectId() {
    return this.dbObjectTermId.getId();
  }

  /** @return dbObjectId as phenol {@link TermId}. Note that in some cases, the GO
   * annotation files use a prefix with this field, e.g., MGI:123, and in other cases, they
   * do not, e.g., Uniprot. In the latter case, we can combine the Database field (UniProtKB)
   * and the dbObjectId field (e.g., Q14469) to create a TermId.
   */
  public TermId getDbObjectTermId() {
    return this.dbObjectTermId;
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
  public TermId id() {
    return goId;
  }

  @Override
  public TermId getItemId() {
    //return db + ":" + dbObjectId;
    return this.dbObjectTermId;
  }

  @Override
  public int compareTo(TermAnnotation o) {
    if (!(o instanceof GoGaf21Annotation)) {
      throw new PhenolRuntimeException(
          "Can only compare GoGaf21Annotation with objects of same type");
    }
    GoGaf21Annotation that = (GoGaf21Annotation) o;

    int result = annotationExtension.compareTo(that.annotationExtension);
    if (result != 0) return result;
    result = aspect.compareTo(that.aspect);
    if (result != 0) return result;
    result = assignedBy.compareTo(that.assignedBy);
    if (result != 0) return result;
    result = date.compareTo(that.date);
    if (result != 0) return result;
    result = dbObjectTermId.compareTo(that.dbObjectTermId);
    if (result != 0) return result;
    result = dbObjectName.compareTo(that.dbObjectName);
    if (result != 0) return result;
    result = dbObjectSymbol.compareTo(that.dbObjectSymbol);
    if (result != 0) return result;
    result = dbObjectSynonym.compareTo(that.dbObjectSynonym);
    if (result != 0) return result;
    result = dbObjectType.compareTo(that.dbObjectType);
    if (result != 0) return result;
    result = dbReference.compareTo(that.dbReference);
    if (result != 0) return result;
    result = evidenceCode.compareTo(that.evidenceCode);
    if (result != 0) return result;
    result = geneProductFormId.compareTo(that.geneProductFormId);
    if (result != 0) return result;
    result = goId.compareTo(that.goId);
    if (result != 0) return result;
    result = qualifier.compareTo(that.qualifier);
    if (result != 0) return result;
    for (int i = 0; i < this.taxons.size(); i++) {
      String thisTaxon = this.taxons.get(i);
      String thatTaxon = that.taxons.get(i);
      result = thisTaxon.compareTo(thatTaxon);
      if (result != 0)
        return result;
    }

    return with.compareTo(that.with);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((annotationExtension == null) ? 0 : annotationExtension.hashCode());
    result = prime * result + ((aspect == null) ? 0 : aspect.hashCode());
    result = prime * result + ((assignedBy == null) ? 0 : assignedBy.hashCode());
    result = prime * result + ((date == null) ? 0 : date.hashCode());
    result = prime * result + ((dbObjectTermId == null) ? 0 : dbObjectTermId.hashCode());
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
    if (! (obj instanceof GoGaf21Annotation)) {
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
    if (dbObjectTermId == null) {
      if (other.dbObjectTermId != null) {
        return false;
      }
    } else if (!dbObjectTermId.equals(other.dbObjectTermId)) {
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
      return other.with == null;
    } else {
      return with.equals(other.with);
    }
  }

  @Override
  public String toString() {
    return "GoGaf21Annotation [db="
        + dbObjectTermId.getPrefix()
        + ", dbObjectId="
        + dbObjectTermId.getId()
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
