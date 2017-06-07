package de.charite.compbio.ontolib.ontology.data;

import java.util.List;

/**
 * Interface for richly annotated information about a {@link Term}'s synoymous
 * description.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermSynonym {

  /** @return The synonym's label/value */
  String getValue();

  /** @return The author's name. */
  String getAuthor();

  /** @return The synonym scope. */
  TermSynonymScope getScope();

  /** @return Further qualitifiaction of the synonym's type name. */
  String getSynonymTypeName();

  /** @return {@link List} of term cross-references. */
  List<TermXRef> getTermXRefs();

}
