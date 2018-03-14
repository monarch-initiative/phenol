package org.monarchinitiative.phenol.io.obo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the <code>dbXrefList</code> from the OBO file.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DbXrefList {

  /** List of {@link DbXref} objects. */
  private final List<DbXref> dbXrefs = new ArrayList<>();

  /**
   * Add new {@link DbXref} to the list.
   *
   * @param dbXref The {@link DbXref} to add.
   */
  public void addDbXref(DbXref dbXref) {
    dbXrefs.add(dbXref);
  }

  /**
   * Query for list of database cross references.
   *
   * @return the dbXrefs
   */
  public List<DbXref> getDbXrefs() {
    return dbXrefs;
  }

  @Override
  public String toString() {
    return "DbXrefList [dbXrefs=" + dbXrefs + "]";
  }
}
