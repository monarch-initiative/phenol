package org.monarchinitiative.phenol.io.owl;

import org.geneontology.obographs.model.Node;

import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.Relationship;

/**
 * Interface for constructing concrete {@link Term} and {@link Relationship} objects in {@link
 * OwlImmutableOntologyLoader}.
 *
 * @param <T> The type to use for terms.
 * @param <R> The type to use for term relations.
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public interface OwlOntologyEntryFactory<T extends Term, R extends Relationship> {
  public T constructTerm(Node node, TermId termId);

  public R constructRelationship(TermId source, TermId dest, int id);
}
