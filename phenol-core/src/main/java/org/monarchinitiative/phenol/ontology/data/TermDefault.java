package org.monarchinitiative.phenol.ontology.data;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Representation of an OBO term (forked and modified from GoTerm).
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
class TermDefault implements Term {

  /**
   * The term's primary id.
   */
  private final TermId id;

  /**
   * {@link TermId}s of {@link TermDefault}s that have been obsoleted and replaced by this {@link TermDefault}.
   */
  private final List<TermId> altTermIds;

  /**
   * The human-readable name of the term.
   */
  private final String name;

  /**
   * The term's definition.
   */
  private final String definition;
  /**
   * These are the cross-references that go along with the definition. In the case of the HPO, these
   * are often PubMed ids.
   */
  private final List<SimpleXref> databaseXrefs;

  /**
   * The term's comment string.
   */
  private final String comment;

  /**
   * The names of the subsets that the term is in, empty if none.
   */
  private final List<String> subsets;

  /**
   * The list of term synonyms.
   */
  private final List<TermSynonym> synonyms;

  /**
   * Whether or not the term is obsolete.
   */
  private final boolean obsolete;

  /**
   * The term's author name.
   */
  private final String createdBy;

  /**
   * The term's creation date.
   */
  private final Date creationDate;

  /**
   * The term's xrefs.
   */
  private final List<Dbxref> xrefs;

  /**
   * The term's exact matches.
   */
  private final List<TermId> exactMatches;

  TermDefault(TermId termId,
              String name,
              List<TermId> altTermIds,
              String definition,
              List<SimpleXref> databaseXrefs,
              String comment,
              List<String> subsets,
              List<TermSynonym> synonyms,
              boolean obsolete,
              String createdBy,
              Date creationDate,
              List<Dbxref> xrefs,
              List<TermId> exactMatches) {
    this.id = termId;
    this.name = name;
    //other fields...
    this.altTermIds = altTermIds;
    this.definition = definition;
    this.databaseXrefs = databaseXrefs;
    this.comment = comment;
    this.subsets = subsets;
    this.synonyms = synonyms;
    this.obsolete = obsolete;
    this.createdBy = createdBy;
    // creation date can be null - it returns an Optional
    this.creationDate = creationDate;
    this.xrefs = xrefs;
    this.exactMatches = exactMatches;
  }

  TermDefault(Term.Builder builder) {
    this.id = Objects.requireNonNull(builder.id, "ID must not be null");
    this.name = Objects.requireNonNull(builder.name, "Name must not be null");
    this.altTermIds = List.copyOf(builder.altTermIds);
    this.definition = Objects.requireNonNull(builder.definition, "Definition must not be null");
    this.databaseXrefs = List.copyOf(builder.databaseXrefs);
    this.comment = Objects.requireNonNull(builder.comment, "Comment must not be null");
    this.subsets = List.copyOf(builder.subsets);
    this.synonyms = List.copyOf(builder.synonyms);
    this.obsolete = builder.obsolete;
    this.createdBy = Objects.requireNonNull(builder.createdBy, "Created by must not be null");
    // creation date can be null - it returns an Optional
    this.creationDate = builder.creationDate;
    this.xrefs = List.copyOf(builder.xrefs);
    this.exactMatches = List.copyOf(builder.exactMatches);
  }

  @Override
  public TermId id() {
    return id;
  }

  /**
   * @return a list of {@link TermId}s of {@link TermDefault}s that have been obsoleted and replaced by this {@link TermDefault}.
   */
  @Override
  public List<TermId> getAltTermIds() {
    return altTermIds;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDefinition() {
    return definition;
  }

  @Override
  public List<SimpleXref> getDatabaseXrefs() {
    return this.databaseXrefs;
  }

  /**
   * Get all of the pub med references attached to the definition of this term
   */
  @Override
  public List<SimpleXref> getPmidXrefs() {
    return databaseXrefs.stream().filter(SimpleXref::isPmid).collect(Collectors.toList());
  }

  @Override
  public String getComment() {
    return comment;
  }

  @Override
  public List<String> getSubsets() {
    return subsets;
  }

  @Override
  public List<TermSynonym> getSynonyms() {
    return synonyms;
  }

  @Override
  public boolean isObsolete() {
    return obsolete;
  }

  @Override
  public String getCreatedBy() {
    return createdBy;
  }

  @Override
  public Optional<Date> getCreationDate() {
    return Optional.ofNullable(creationDate);
  }

  @Override
  public List<Dbxref> getXrefs() {
    return xrefs;
  }

  @Override
  public List<TermId> getExactMatches() {
    return exactMatches;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TermDefault term = (TermDefault) o;
    return obsolete == term.obsolete &&
      Objects.equals(id, term.id) &&
      Objects.equals(altTermIds, term.altTermIds) &&
      Objects.equals(name, term.name) &&
      Objects.equals(definition, term.definition) &&
      Objects.equals(databaseXrefs, term.databaseXrefs) &&
      Objects.equals(comment, term.comment) &&
      Objects.equals(subsets, term.subsets) &&
      Objects.equals(synonyms, term.synonyms) &&
      Objects.equals(createdBy, term.createdBy) &&
      Objects.equals(creationDate, term.creationDate) &&
      Objects.equals(xrefs, term.xrefs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, altTermIds, name, definition, databaseXrefs, comment, subsets, synonyms, obsolete, createdBy, creationDate, xrefs);
  }

  @Override
  public String toString() {
    return "Term [id="
      + id
      + ", altTermIds="
      + altTermIds
      + ", name="
      + name
      + ", definition="
      + definition
      + ", comment="
      + comment
      + ", subsets="
      + subsets
      + ", synonyms="
      + synonyms
      + ", obsolete="
      + obsolete
      + ", createdBy="
      + createdBy
      + ", creationDate="
      + creationDate
      + ", xrefs="
      + xrefs
      + "]";
  }

}
