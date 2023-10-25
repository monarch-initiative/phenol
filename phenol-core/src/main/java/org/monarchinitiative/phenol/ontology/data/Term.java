package org.monarchinitiative.phenol.ontology.data;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

import java.util.*;

/**
 * Representation of an OBO term (forked and modified from GoTerm)
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 */
public interface Term extends Identified {

  /**
   * @deprecated use {@link #of(TermId, String)} instead. The method will be removed in {@code v3.0.0}.
   */
  // REMOVE(v3.0.0)
  @Deprecated(since = "2.0.2", forRemoval = true)
  static Term of(String termIdString, String name) {
    Objects.requireNonNull(termIdString);
    Objects.requireNonNull(name);
    TermId termId = TermId.of(termIdString);
    return Term.of(termId, name);
  }

  static Term of(TermId termId, String name) {
    Objects.requireNonNull(termId);
    Objects.requireNonNull(name);
    return new TermMinimal(termId,
      name);
  }

  /**
   * @deprecated use {@link #builder(TermId)} instead. The method will be removed in {@code v3.0.0}.
   */
  // REMOVE(v3.0.0)
  @Deprecated(since = "2.0.2", forRemoval = true)
  static Builder builder() {
    return builder(null);
  }

  /**
   * Create a builder to build a {@linkplain Term} with given primary {@code termId}.
   */
  static Builder builder(TermId termId) {
    return new Builder(termId);
  }

  List<TermId> getAltTermIds();

  String getName();

  String getDefinition();

  List<SimpleXref> getDatabaseXrefs();

  List<SimpleXref> getPmidXrefs();

  String getComment();

  List<String> getSubsets();

  List<TermSynonym> getSynonyms();

  boolean isObsolete();

  String getCreatedBy();

  Optional<Date> getCreationDate();

  List<Dbxref> getXrefs();

  List<TermId> getExactMatches();


  class Builder {

    //Primary identifiers for the Term - cannot be null
    final TermId id;
    // The human-readable name of the term.
    String name = null;

    //Optional attributes
    List<TermId> altTermIds = List.of();
    String definition = "";
    //  These are the cross-references that go along with the definition. In the case of the HPO, these
    //  are often PubMed ids.
    List<SimpleXref> databaseXrefs = List.of();
    String comment = "";
    //  The names of the subsets that the term is in, empty if none. */
    List<String> subsets = List.of();
    List<TermSynonym> synonyms = List.of();
    boolean obsolete = false;
    //  The term's author name. */
    String createdBy = "";
    Date creationDate;
    List<Dbxref> xrefs = List.of();
    List<TermId> exactMatches = List.of();

    private Builder(TermId id) {
      // REMOVE(3.0.0) - as of 3.0.0 `id` is non-null since we can remove the noarg builder() above.
      //  Then uncomment the below and delete the 2 lines below.
//      this.id = Objects.requireNonNull(id);
      this.id = id;
    }

    private static <T> boolean isNullOrEmpty(Collection<T> collection) {
      return collection == null || collection.isEmpty();
    }

    private static boolean isNullOrBlank(String value) {
      return value == null || value.isBlank();
    }

    /**
     * @param id The term's ID.
     * @deprecated set the primary {@link TermId} via {@link #builder(TermId)}.
     */
    public Builder id(TermId id) {
      throw new PhenolRuntimeException("id was deprecated, set the primary term via builder instead.");
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

    public Builder exactMatches(List<TermId> exactMatches) {
      this.exactMatches = exactMatches;
      return this;
    }

    public Builder xrefs(List<Dbxref> xrefs) {
      this.xrefs = xrefs;
      return this;
    }

    public Term build() {
      if (isNullOrEmpty(altTermIds) && isNullOrBlank(definition) && isNullOrEmpty(databaseXrefs) && isNullOrBlank(comment)
        && isNullOrEmpty(subsets) && isNullOrEmpty(synonyms) && !obsolete && isNullOrBlank(createdBy) && creationDate == null
        && isNullOrEmpty(xrefs) && isNullOrEmpty(exactMatches))
        return new TermMinimal(id, name);
      else
        return new TermDefault(this);
    }
  }
}
