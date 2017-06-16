package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for richly annotated information about a {@link Term}'s synonymous description.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermSynonym extends Serializable {

  /** @return The synonym's label/value */
  String getValue();

  /** @return The synonym scope. */
  TermSynonymScope getScope();

  /**
   * @return Optional, further qualification of the synonym's type name, <code>null</code> if
   *         missing.
   */
  String getSynonymTypeName();

  /** @return {@link List} of term cross-references, <code>null</code> if missing. */
  List<TermXRef> getTermXRefs();

}
