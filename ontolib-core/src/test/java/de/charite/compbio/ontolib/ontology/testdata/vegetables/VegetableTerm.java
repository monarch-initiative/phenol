package de.charite.compbio.ontolib.ontology.testdata.vegetables;

import java.util.List;

import de.charite.compbio.ontolib.ontology.data.ImmutableTermId;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermSynonym;

public class VegetableTerm implements Term {

  private static final long serialVersionUID = 1L;

  private ImmutableTermId termId;
  private List<TermId> altTermIds;
  private String name;
  private String definition;
  private String comment;
  private List<String> subsets;
  private List<TermSynonym> termSynonyms;
  private boolean obsolete;
  private String createdBy;
  private String creationDate;

  public VegetableTerm(ImmutableTermId termId, List<TermId> altTermIds, String name,
      String definition, String comment, List<String> subsets, List<TermSynonym> termSynonyms,
      boolean obsolete, String createdBy, String creationDate) {
    this.termId = termId;
    this.altTermIds = altTermIds;
    this.name = name;
    this.definition = definition;
    this.comment = comment;
    this.subsets = subsets;
    this.termSynonyms = termSynonyms;
    this.obsolete = obsolete;
    this.createdBy = createdBy;
    this.creationDate = creationDate;
  }

  @Override
  public TermId getId() {
    return termId;
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
    return termSynonyms;
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
    return "VegetableTerm [termId=" + termId + ", altTermIds=" + altTermIds + ", name=" + name
        + ", definition=" + definition + ", comment=" + comment + ", subsets=" + subsets
        + ", termSynonyms=" + termSynonyms + ", obsolete=" + obsolete + ", createdBy=" + createdBy
        + ", creationDate=" + creationDate + "]";
  }

}
