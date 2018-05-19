package org.monarchinitiative.phenol.io.obo;

import java.util.SortedMap;

import org.monarchinitiative.phenol.formats.generic.Relationship;
import org.monarchinitiative.phenol.formats.generic.Term;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;


/**
 * Interface for constructing concrete {@link Term} and {@link Relationship} objects in {@link
 * OboImmutableOntologyLoader}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface OboOntologyEntryFactory {

  /**
   * Set map from TermI Id string to {@link ImmutableTermId} into factory.
   *
   * @param termIds The term Id mapping to use.
   */
  void setTermIds(SortedMap<String, ImmutableTermId> termIds);

  /**
   * Construct term from {@link Stanza}.
   *
   * @param stanza {@link Stanza} with information.
   * @return The constructed <code>T</code>.
   */
  Term constructTerm(Stanza stanza);

  /**
   * Construct term from {@link Stanza} and <code>is_a</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>is_a</code> tag.
   * @return The constructed <code>T</code>.
   */
  Relationship constructrelationship(Stanza stanza, StanzaEntryIsA stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>disjoint_from</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>disjoint_from</code> tag.
   * @return The constructed <code>T</code>.
   */
  Relationship constructrelationship(Stanza stanza, StanzaEntryDisjointFrom stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>union_of</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>union_of</code> tag.
   * @return The constructed <code>T</code>.
   */
  Relationship constructrelationship(Stanza stanza, StanzaEntryUnionOf stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>intersection_of</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>intersection_of</code> tag.
   * @return The constructed <code>T</code>.
   */
  Relationship constructrelationship(Stanza stanza, StanzaEntryIntersectionOf stanzaEntry);

  /**
   * Construct term from {@link Stanza} and <code>relationship</code> tag.
   *
   * @param stanza Relation's source {@link Stanza} with information.
   * @param stanzaEntry The <code>relationship</code> tag.
   * @return The constructed <code>T</code>.
   */
  Relationship constructrelationship(Stanza stanza, StanzaEntryRelationship stanzaEntry);
}
