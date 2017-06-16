package de.charite.compbio.ontolib.io.obo;

import de.charite.compbio.ontolib.ontology.data.ImmutableTermID;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.SortedMap;

/**
 * Interface for constructing concrete {@link Term} and {@link TermRelation} objects in
 * {@link OBOImmutableOntologyLoader}.
 *
 * @param <T> The type to use for terms.
 * @param <R> The type to use for term relations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface OBOOntologyEntryFactory<T extends Term, R extends TermRelation> {

  /**
   * Set map from Term ID string to {@link ImmutableTermID} into factory.
   *
   * @param termIDs The term ID mapping to use.
   */
  public void setTermIDs(SortedMap<String, ImmutableTermID> termIDs);

  /**
   * Construct term from {@link Stanza}.
   *
   * @param stanza {@link Stanza} with information.
   * @return The constructed <code>T</code>.
   */
  public T constructTerm(Stanza stanza);

  /**
   * Construct term from {@link Stanza} and <code>is_a</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>is_a</code> tag.
   * @return The constructed <code>T</code>.
   */
  public R constructTermRelation(Stanza stanza, StanzaEntryIsA stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>disjoint_from</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>disjoint_from</code> tag.
   * @return The constructed <code>T</code>.
   */
  public R constructTermRelation(Stanza stanza, StanzaEntryDisjointFrom stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>union_of</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>union_of</code> tag.
   * @return The constructed <code>T</code>.
   */
  public R constructTermRelation(Stanza stanza, StanzaEntryUnionOf stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>intersection_of</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>intersection_of</code> tag.
   * @return The constructed <code>T</code>.
   */
  public R constructTermRelation(Stanza stanza, StanzaEntryIntersectionOf stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>relationship</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>relationship</code> tag.
   * @return The constructed <code>T</code>.
   */
  public R constructTermRelation(Stanza stanza, StanzaEntryRelationship stanzaEntry);

}
