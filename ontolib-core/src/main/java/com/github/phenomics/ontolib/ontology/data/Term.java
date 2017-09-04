package com.github.phenomics.ontolib.ontology.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Interface for terms to implement.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface Term extends Serializable {

  /**
   * @return {@link TermId} for this <code>Term</code>.
   */
  TermId getId();

  /**
   * @return {@link List} of this <code>Term</code>'s alternative {@link TermId}s.
   */
  List<TermId> getAltTermIds();

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
   * @return {@link List} of <code>String</code> with this <code>Term</code>'s subset names, empty
   *         if part of no subset.
   */
  List<String> getSubsets();

  /**
   * @return {@link List} of {@link TermSynonym}s describing this <code>Term</code>'s synonyms.
   */
  List<TermSynonym> getSynonyms();

  /**
   * @return <code>true</code> if "obsolete" flag is set, <code>false</code> if not.
   */
  boolean isObsolete();

  /**
   * Query for creator.
   *
   * @return <code>created_by</code> value.
   */
  String getCreatedBy();

  /**
   * Query for creation date.
   *
   * @return <code>creation_date</code> value, parsed to a {@link Date}.
   */
  Date getCreationDate();

  /**
   * Query for {@code xref} values.
   */
  List<Dbxref> getXrefs();

}
