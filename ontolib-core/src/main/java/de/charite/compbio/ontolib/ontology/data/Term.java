package de.charite.compbio.ontolib.ontology.data;

import java.util.List;

/**
 * Interface for terms to implement.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface Term {

  /**
   * @return {@link TermID} for this <code>Term</code>.
   */
  TermID getID();

  /**
   * @return {@link List} of this <code>Term</code>'s alternative
   *         {@link TermID}s.
   */
  List<TermID> getAltTermIDs();

  /**
   * = * @return <code>String</code> value for this <code>Term</code>'s name.
   */
  String getName();

  /**
   * @return <code>definition</code> for this <code>Term</code>.
   */
  String getDefinition();

  /**
   * @return <code>comment</code> value for this <code>Term</code>.
   */
  String getComment();

  /**
   * @return <code>String</code> description of this <code>Term</code>'s subset.
   */
  String getSubset();

  /**
   * @return {@link List} of {@link TermSynonyms} describing this
   *         <code>Term</code>'s synonyms.
   */
  List<TermSynonym> getSynonyms();

  /**
   * @return <code>true</code> if "obsolete" flag is set, <code>false</code> if
   *         not.
   */
  boolean isObsolete();

  /** @return <code>created_by</code> value. */
  String getCreatedBy();

  /** @return <code>creation_date</code> value. */
  String getCreationDate();

}
