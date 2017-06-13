package de.charite.compbio.ontolib.io.obo;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the <code>dbXRefList</code> from the OBO file.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DBXRefList {

  /** List of {@link DBXRef} objects. */
  private final List<DBXRef> dbXRefs = new ArrayList<>();

  /**
   * Add new {@link DBXRef} to the list.
   *
   * @param dbXRef The {@link DBXRef} to add.
   */
  public void addDBXRef(DBXRef dbXRef) {
    dbXRefs.add(dbXRef);
  }

  /**
   * @return the dbXRefs
   */
  public List<DBXRef> getDbXRefs() {
    return dbXRefs;
  }

  @Override
  public String toString() {
    return "DBXRefList [dbXRefs=" + dbXRefs + "]";
  }

}
