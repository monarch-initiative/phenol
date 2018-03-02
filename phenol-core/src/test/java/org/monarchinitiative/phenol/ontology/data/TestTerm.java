package org.monarchinitiative.phenol.ontology.data;

import java.util.Date;
import java.util.List;

public class TestTerm implements Term {

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
  private Date creationDate;
  private List<Dbxref> xrefs;

  public TestTerm(ImmutableTermId termId, List<TermId> altTermIds, String name, String definition,
      String comment, List<String> subsets, List<TermSynonym> termSynonyms, boolean obsolete,
      String createdBy, Date creationDate, List<Dbxref> xrefs) {
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
    this.xrefs = xrefs;
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
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public List<Dbxref> getXrefs() {
    return xrefs;
  }

  @Override
  public String toString() {
    return "TestTerm [termId=" + termId + ", altTermIds=" + altTermIds + ", name=" + name
        + ", definition=" + definition + ", comment=" + comment + ", subsets=" + subsets
        + ", termSynonyms=" + termSynonyms + ", obsolete=" + obsolete + ", createdBy=" + createdBy
        + ", creationDate=" + creationDate + ", xrefs=" + xrefs + "]";
  }

}
