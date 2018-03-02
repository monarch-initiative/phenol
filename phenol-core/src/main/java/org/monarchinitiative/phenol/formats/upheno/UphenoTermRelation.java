package org.monarchinitiative.phenol.formats.upheno;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermRelation;

/**
 * {@link TermRelation} to use for uPheno.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public class UphenoTermRelation implements TermRelation {

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
   * {@link UphenoRelationQualifier} for this term relation.
   */
  private final UphenoRelationQualifier relationQualifier;

  /**
   * Constructor.
   *
   * @param source Source {@link TermId}.
   * @param dest Destination {@link TermId}.
   * @param id The term relation's Id, corresponds to Id of edge in graph.
   * @param relationQualifier The relation's further qualifier.
   */
  public UphenoTermRelation(TermId source, TermId dest, int id,
      UphenoRelationQualifier relationQualifier) {
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
  public UphenoRelationQualifier getRelationQualifier() {
    return relationQualifier;
  }

  @Override
  public String toString() {
    return "UphenoTermRelation [source=" + source + ", dest=" + dest + ", id=" + id
        + ", relationQualifier=" + relationQualifier + "]";
  }

}
