package de.charite.compbio.ontolib.formats.go;

import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermSynonym;
import java.util.List;

/**
 * Representation of a term in the GO.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public class GOTerm implements Term {

  /** The HPO term's ID. */
  private final TermID id;

  /** Alternative term IDs. */
  private final List<TermID> altTermIDs;

  /** The human-readable name of the term. */
  private final String name;

  /** The term's definition. */
  private final String definition;

  /** The term's comment string. */
  private final String comment;

  /** The names of the subsets that the term is in, empty if none. */
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
   * @param id The term's ID.
   * @param altTermIDs Alternative term IDs.
   * @param name Human-readable term name.
   * @param definition Term definition.
   * @param comment Term comment.
   * @param subsets The names of the subset that the term is in, empty if none.
   * @param synonyms The synonyms for the term.
   * @param obsolete Whether or not the term is obsolete.
   * @param createdBy Author of the term.
   * @param creationDate Date of creation of the term.
   */
  public GOTerm(TermID id, List<TermID> altTermIDs, String name, String definition, String comment,
      List<String> subsets, List<TermSynonym> synonyms, boolean obsolete, String createdBy,
      String creationDate) {
    this.id = id;
    this.altTermIDs = altTermIDs;
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
  public TermID getID() {
    return id;
  }

  @Override
  public List<TermID> getAltTermIDs() {
    return altTermIDs;
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

}
