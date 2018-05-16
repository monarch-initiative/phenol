package org.monarchinitiative.phenol.io.owl;

import org.geneontology.obographs.model.Node;

import org.monarchinitiative.phenol.ontology.data.RelationshipI;
import org.monarchinitiative.phenol.ontology.data.TermI;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Interface for constructing concrete {@link TermI} and {@link RelationshipI} objects in {@link
 * OwlImmutableOntologyLoader}.
 *
 * @param <T> The type to use for terms.
 * @param <R> The type to use for term relations.
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public interface OwlOntologyEntryFactory<T extends TermI, R extends RelationshipI> {
  T constructTerm(Node node, TermId termId);

  R constructRelationship(TermId source, TermId dest, int id);
}
