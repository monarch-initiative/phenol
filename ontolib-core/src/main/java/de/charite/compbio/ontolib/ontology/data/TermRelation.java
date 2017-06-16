package de.charite.compbio.ontolib.ontology.data;

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
   * @return {@link TermID} of source {@link Term}
   */
  TermID getSource();

  /**
   * @return {@link TermID} of destination{@link Term}
   */
  TermID getDest();

  /**
   * @return Numeric identifier, for building efficient maps with further information
   */
  int getID();

}
