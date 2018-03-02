package org.monarchinitiative.phenol.formats.generic;

import org.monarchinitiative.phenol.ontology.data.Dbxref;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermSynonym;
import java.util.Date;
import java.util.List;

/**
 * Representation of a term in common (folked from GoTerm)
 */

public class GenericTerm implements Term {

	/** Serial UID for serialization. */
	private static long serialVersionUID = 1L;

	/** The Common term's Id. */
	private TermId id;

	/** Alternative term Ids. */
	private List<TermId> altTermIds;

	/** The human-readable name of the term. */
	private String name;

	/** The term's definition. */
	private String definition;

	/** The term's comment string. */
	private String comment;

	/** The names of the subsets that the term is in, empty if none. */
	private List<String> subsets;

	/** The list of term synonyms. */
	private List<TermSynonym> synonyms;

	/** Whether or not the term is obsolete. */
	private boolean obsolete;

	/** The term's author name. */
	private String createdBy;

	/** The term's creation date. */
	private Date creationDate;

	/** The term's xrefs. */
	private List<Dbxref> xrefs;

	/**
	 * Constructor.
	 *
	 * @param id The term's Id.
	 * @param altTermIds Alternative term Ids.
	 * @param name Human-readable term name.
	 * @param definition Term definition.
	 * @param comment Term comment.
	 * @param subsets The names of the subset that the term is in, empty if none.
	 * @param synonyms The synonyms for the term.
	 * @param obsolete Whether or not the term is obsolete.
	 * @param createdBy Author of the term.
	 * @param creationDate Date of creation of the term.
	 * @Param xrefs The Term's xrefs.
	 */
	public GenericTerm(TermId id, List<TermId> altTermIds, String name, String definition, String comment,
			List<String> subsets, List<TermSynonym> synonyms, boolean obsolete, String createdBy,
			Date creationDate, List<Dbxref> xrefs) {
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
		this.xrefs = xrefs;
	}

	public GenericTerm() {}

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
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public List<Dbxref> getXrefs() {
		return xrefs;
	}

	public void setId(TermId id) {
		this.id = id;
	}

	public void setAltTermIds(List<TermId> altTermIds) {
		this.altTermIds = altTermIds;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setSubsets(List<String> subsets) {
		this.subsets = subsets;
	}

	public void setSynonyms(List<TermSynonym> synonyms) {
		this.synonyms = synonyms;
	}

	public void setObsolete(boolean obsolete) {
		this.obsolete = obsolete;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setXrefs(List<Dbxref> xrefs) {
		this.xrefs = xrefs;
	}

	@Override
	public String toString() {
		return "CommonTerm [id=" + id + ", altTermIds=" + altTermIds + ", name=" + name + ", definition="
				+ definition + ", comment=" + comment + ", subsets=" + subsets + ", synonyms=" + synonyms
				+ ", obsolete=" + obsolete + ", createdBy=" + createdBy + ", creationDate=" + creationDate
				+ ", xrefs=" + xrefs + "]";
	}
}
