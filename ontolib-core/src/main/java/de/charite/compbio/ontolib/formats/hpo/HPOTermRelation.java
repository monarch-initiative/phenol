package de.charite.compbio.ontolib.formats.hpo;

import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;

/**
 * {@link TermRelation} to use for the HPO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public class HPOTermRelation implements TermRelation {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /** Source {@link TermID}. */
  private final TermID source;

  /** Destination {@link TermID}. */
  private final TermID dest;

  /** ID for this term relation, corresponds to ID of edge in graph. */
  private final int id;

  /** {@link HPORelationQualifier} for this term relation. */
  private final HPORelationQualifier relationQualifier;

  /**
   * Constructor.
   * 
   * @param source Source {@link TermID}.
   * @param dest Destination {@link TermID}.
   * @param id The term relation's ID, corresponds to ID of edge in graph.
   * @param relationQualifier The relation's further qualifier.
   */
  public HPOTermRelation(TermID source, TermID dest, int id,
      HPORelationQualifier relationQualifier) {
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
  public HPORelationQualifier getRelationQualifier() {
    return relationQualifier;
  }

  @Override
  public String toString() {
    return "HPOTermRelation [source=" + source + ", dest=" + dest + ", id=" + id
        + ", relationQualifier=" + relationQualifier + "]";
  }

}
