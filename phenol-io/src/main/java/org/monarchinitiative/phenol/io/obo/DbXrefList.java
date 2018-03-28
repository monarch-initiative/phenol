package org.monarchinitiative.phenol.io.obo;

import java.util.ArrayList;
import java.util.List;

import org.monarchinitiative.phenol.ontology.data.Dbxref;

/**
 * Representation of the <code>dbXrefList</code> from the OBO file.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DbXrefList {

  /** List of {@link DbXref} objects. */
  private final List<Dbxref> dbXrefs = new ArrayList<>();

  /**
   * Add new {@link DbXref} to the list.
   *
   * @param dbXref The {@link DbXref} to add.
   */
  public void addDbXref(Dbxref dbXref) {
    dbXrefs.add(dbXref);
  }

  /**
   * Query for list of database cross references.
   *
   * @return the dbXrefs
   */
  public List<Dbxref> getDbXrefs() {
    return dbXrefs;
  }

  @Override
  public String toString() {
    return "DbXrefList [dbXrefs=" + dbXrefs + "]";
  }
}
