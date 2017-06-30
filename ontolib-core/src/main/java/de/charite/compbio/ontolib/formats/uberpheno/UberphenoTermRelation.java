package de.charite.compbio.ontolib.formats.uberpheno;

import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;

/**
 * {@link TermRelation} to use for the Uberpheno ontology.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public class UberphenoTermRelation implements TermRelation {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Source {@link TermId}.
   */
  private final TermId source;

  /**
   * Destination {@link TermId}.
   */
  private final TermId dest;

  /** Id for this term relation, corresponds to Id of edge in graph. */
  private final int id;

  /**
   * {@link UberphenoRelationQualifier} for this term relation.
   */
  private final UberphenoRelationQualifier relationQualifier;

  /**
   * Constructor.
   *
   * @param source Source {@link TermId}.
   * @param dest Destination {@link TermId}.
   * @param id The term relation's Id, corresponds to Id of edge in graph.
   * @param relationQualifier The relation's further qualifier.
   */
  public UberphenoTermRelation(TermId source, TermId dest, int id,
      UberphenoRelationQualifier relationQualifier) {
    this.source = source;
    this.dest = dest;
    this.id = id;
    this.relationQualifier = relationQualifier;
  }

  @Override
  public TermId getSource() {
    return source;
  }

  @Override
  public TermId getDest() {
    return dest;
  }

  @Override
  public int getId() {
    return id;
  }

  /**
   * @return The relation's qualifier.
   */
  public UberphenoRelationQualifier getRelationQualifier() {
    return relationQualifier;
  }

  @Override
  public String toString() {
    return "UberphenoTermRelation [source=" + source + ", dest=" + dest + ", id=" + id
        + ", relationQualifier=" + relationQualifier + "]";
  }

}
