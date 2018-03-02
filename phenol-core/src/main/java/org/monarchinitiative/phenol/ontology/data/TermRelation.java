package org.monarchinitiative.phenol.ontology.data;

import java.io.Serializable;

/**
 * Description of a term relation, such relations are directional
 *
 * <p>
 * This corresponds to an edge in the ontology graph, and the type to use for the relation
 * description (e.g., &quot;part of&quot; or &quot;down-regulates&quot; is ontology-specific.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermRelation extends Serializable {

  /**
   * Query for relation's source (from).
   *
   * @return {@link TermId} of source {@link Term}
   */
  TermId getSource();

  /**
   * Query for relation's destination (to).
   *
   * @return {@link TermId} of destination{@link Term}
   */
  TermId getDest();

  /**
   * Query for numeric relation identifier.
   *
   * @return Numeric identifier, for building efficient maps with further information
   */
  int getId();

}
