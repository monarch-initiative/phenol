package org.monarchinitiative.phenol.ontology.data;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for richly annotated information about a {@link Term}'s synonymous description.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermSynonym extends Serializable {

  /**
   * Query for synonym's label.
   *
   * @return The synonym's label/value
   */
  String getValue();

  /**
   * Query for synonym's scope.
   *
   * @return The synonym scope.
   */
  TermSynonymScope getScope();

  /**
   * Query for synonym type name.
   *
   * @return Optional, further qualification of the synonym's type name, <code>null</code> if
   *         missing.
   */
  String getSynonymTypeName();

  /**
   * Query for synonym's cross-references.
   *
   * @return {@link List} of term cross-references, <code>null</code> if missing.
   */
  List<TermXref> getTermXrefs();

}
