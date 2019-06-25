package org.monarchinitiative.phenol.ontology.data;

import com.google.common.collect.ImmutableList;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Representation of an OBO term (forked and modified from GoTerm)
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class Term {
  private static final long serialVersionUID = 2L;
  /**
   * The term's Id.
   */
  private final TermId id;

  /**
   * Alternative term Ids.
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

  public static Term of(String termIdString, String name) {
    Objects.requireNonNull(termIdString);
    Objects.requireNonNull(name);
    TermId termId = TermId.of(termIdString);
    return new Term(termId, name);
  }

  public static Term of(TermId termId, String name) {
    Objects.requireNonNull(termId);
    Objects.requireNonNull(name);
    return new Term(termId, name);
  }

  private Term(TermId termId, String name) {
    this.id = termId;
    this.name = name;
    //other fields...
    this.altTermIds = ImmutableList.of();
    this.definition = "";
    this.databaseXrefs = ImmutableList.of();
    this.comment = "";
    this.subsets = ImmutableList.of();
    this.synonyms = ImmutableList.of();
    this.obsolete = false;
    this.createdBy = "";
    // creation date can be null - it returns an Optional
    this.creationDate = null;
    this.xrefs = ImmutableList.of();
  }

  private Term(Builder builder) {
    Objects.requireNonNull(builder.id);
    this.id = builder.id;
    Objects.requireNonNull(builder.name);
    this.name = builder.name;
    this.altTermIds = ImmutableList.copyOf(builder.altTermIds);
    Objects.requireNonNull(builder.definition);
    this.definition = builder.definition;
    this.databaseXrefs = ImmutableList.copyOf(builder.databaseXrefs);
    Objects.requireNonNull(builder.comment);
    this.comment = builder.comment;
    this.subsets = ImmutableList.copyOf(builder.subsets);
    this.synonyms = ImmutableList.copyOf(builder.synonyms);
    this.obsolete = builder.obsolete;
    Objects.requireNonNull(builder.createdBy);
    this.createdBy = builder.createdBy;
    // creation date can be null - it returns an Optional
    this.creationDate = builder.creationDate;
    this.xrefs = ImmutableList.copyOf(builder.xrefs);
  }

  public TermId getId() {
    return id;
  }

  public List<TermId> getAltTermIds() {
    return altTermIds;
  }

  public String getName() {
    return name;
  }

  public String getDefinition() {
    return definition;
  }

  public List<SimpleXref> getDatabaseXrefs() {
    return this.databaseXrefs;
  }

  /**
   * Get all of the pub med references attached to the definition of this term
   */
  public List<SimpleXref> getPmidXrefs() {
    return databaseXrefs.stream().filter(SimpleXref::isPmid).collect(Collectors.toList());
  }

  public String getComment() {
    return comment;
  }

  public List<String> getSubsets() {
    return subsets;
  }

  public List<TermSynonym> getSynonyms() {
    return synonyms;
  }

  public boolean isObsolete() {
    return obsolete;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public Optional<Date> getCreationDate() {
    return Optional.ofNullable(creationDate);
  }

  public List<Dbxref> getXrefs() {
    return xrefs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Term term = (Term) o;
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

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    //Primary identifiers for the Term - cannot be null
    private TermId id = null;
    // The human-readable name of the term.
    private String name = null;

    //Optional attributes
    private List<TermId> altTermIds = ImmutableList.of();
    private String definition = "";
    //  These are the cross-references that go along with the definition. In the case of the HPO, these
    //  are often PubMed ids.
    private List<SimpleXref> databaseXrefs = ImmutableList.of();
    private String comment = "";
    //  The names of the subsets that the term is in, empty if none. */
    private List<String> subsets = ImmutableList.of();
    private List<TermSynonym> synonyms = ImmutableList.of();
    private boolean obsolete = false;
    //  The term's author name. */
    private String createdBy = "";
    private Date creationDate;
    private List<Dbxref> xrefs = ImmutableList.of();

    /**
     * @param id The term's ID.
     */
    public Builder id(TermId id) {
      this.id = id;
      return this;
    }

    /**
     * @param name Human-readable term name.
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * @param altTermIds Alternative term Ids.
     */
    public Builder altTermIds(List<TermId> altTermIds) {
      this.altTermIds = altTermIds;
      return this;
    }

    /**
     * @param definition TermI definition.
     */
    public Builder definition(String definition) {
      this.definition = definition;
      return this;
    }

    public Builder databaseXrefs(List<SimpleXref> databaseXrefs) {
      this.databaseXrefs = databaseXrefs;
      return this;
    }

    public Builder comment(String comment) {
      this.comment = comment;
      return this;
    }

    public Builder subsets(List<String> subsets) {
      this.subsets = subsets;
      return this;
    }

    public Builder synonyms(List<TermSynonym> synonyms) {
      this.synonyms = synonyms;
      return this;
    }

    public Builder obsolete(boolean obsolete) {
      this.obsolete = obsolete;
      return this;
    }

    public Builder createdBy(String createdBy) {
      this.createdBy = createdBy;
      return this;
    }

    public Builder creationDate(Date creationDate) {
      this.creationDate = creationDate;
      return this;
    }

    public Builder xrefs(List<Dbxref> xrefs) {
      this.xrefs = xrefs;
      return this;
    }

    public Term build() {
      return new Term(this);
    }
  }
}
