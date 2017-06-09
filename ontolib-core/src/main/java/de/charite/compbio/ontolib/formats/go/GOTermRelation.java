package de.charite.compbio.ontolib.formats.go;

import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;

/**
 * {@link TermRelation} to use for the GO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public class GOTermRelation implements TermRelation {

  /** Source {@link TermID}. */
  private final TermID source;

  /** Destination {@link TermID}. */
  private final TermID dest;

  /** ID for this term relation, corresponds to ID of edge in graph. */
  private final int id;

  /** {@link GORelationQualifier} for this term relation. */
  private final GORelationQualifier relationQualifier;

  /**
   * Constructor.
   * 
   * @param source Source {@link TermID}.
   * @param dest Destination {@link TermID}.
   * @param id The term relation's ID, corresponds to ID of edge in graph.
   * @param relationQualifier The relation's further qualifier.
   */
  public GOTermRelation(TermID source, TermID dest, int id,
      GORelationQualifier relationQualifier) {
    this.source = source;
    this.dest = dest;
    this.id = id;
    this.relationQualifier = relationQualifier;
  }

  @Override
  public TermID getSource() {
    return source;
  }

  @Override
  public TermID getDest() {
    return dest;
  }

  @Override
  public int getID() {
    return id;
  }

  /**
   * @return The relation's qualifier.
   */
  public GORelationQualifier getRelationQualifier() {
    return relationQualifier;
  }

  @Override
  public String toString() {
    return "HPOTermRelation [source=" + source + ", dest=" + dest + ", id=" + id
        + ", relationQualifier=" + relationQualifier + "]";
  }

}
