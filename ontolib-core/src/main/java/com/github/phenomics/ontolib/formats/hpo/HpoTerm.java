package com.github.phenomics.ontolib.formats.hpo;

import java.util.List;

import com.github.phenomics.ontolib.ontology.data.Term;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.github.phenomics.ontolib.ontology.data.TermSynonym;

/**
 * Representation of a term in the HPO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public class HpoTerm implements Term {

  /** Serial UId for serialization. */
  private static final long serialVersionUID = 1L;

  /** The HPO term's Id. */
  private final TermId id;

  /** Alternative term Ids. */
  private final List<TermId> altTermIds;

  /** The human-readable name of the term. */
  private final String name;

  /** The term's definition. */
  private final String definition;

  /** The term's comment string. */
  private final String comment;

  /** The names of the subsets that the term is in, empty list if none. */
  private final List<String> subsets;

  /** The list of term synonyms. */
  private final List<TermSynonym> synonyms;

  /** Whether or not the term is obsolete. */
  private final boolean obsolete;

  /** The term's author name. */
  private final String createdBy;

  /** The term's creation date. */
  private final String creationDate; // TODO: replace by Date?

  /**
   * Constructor.
   *
   * @param id The term's Id.
   * @param altTermIds Alternative term Ids.
   * @param name Human-readable term name.
   * @param definition Term definition.
   * @param comment Term comment.
   * @param subsets The names of the subsets that the term is in, empty if none.
   * @param synonyms The synonyms for the term.
   * @param obsolete Whether or not the term is obsolete.
   * @param createdBy Author of the term.
   * @param creationDate Date of creation of the term.
   */
  public HpoTerm(TermId id, List<TermId> altTermIds, String name, String definition, String comment,
      List<String> subsets, List<TermSynonym> synonyms, boolean obsolete, String createdBy,
      String creationDate) {
    this.id = id;
    this.altTermIds = altTermIds;
    this.name = name;
    this.definition = definition;
    this.comment = comment;
    this.subsets = subsets;
    this.synonyms = synonyms;
    this.obsolete = obsolete;
    this.createdBy = createdBy;
    this.creationDate = creationDate;
  }

  @Override
  public TermId getId() {
    return id;
  }

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
  public String getCreationDate() {
    return creationDate;
  }

  @Override
  public String toString() {
    return "HPOTerm [id=" + id + ", altTermIds=" + altTermIds + ", name=" + name + ", definition="
        + definition + ", comment=" + comment + ", subsets=" + subsets + ", synonyms=" + synonyms
        + ", obsolete=" + obsolete + ", createdBy=" + createdBy + ", creationDate=" + creationDate
        + "]";
  }

}
