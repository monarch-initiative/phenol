package org.monarchinitiative.phenol.graph;

import org.monarchinitiative.phenol.ontology.data.RelationshipType;

/**
 * Some common {@link RelationType}s, including {@link #isA()} and {@link #partOf()}.
 *
 * @see RelationType
 * @see OntologyGraphEdge
 * @author <a href="mailto:daniel.gordon.danis@protonmail.com">Daniel Danis</a>
 */
public class RelationTypes {

  private static final RelationType IS_A = RelationshipType.IS_A;
  private static final RelationType PART_OF = RelationshipType.PART_OF;

  public static RelationType isA() {
    return IS_A;
  }

  public static RelationType partOf() {
    return PART_OF;
  }

  private RelationTypes(){}

}
