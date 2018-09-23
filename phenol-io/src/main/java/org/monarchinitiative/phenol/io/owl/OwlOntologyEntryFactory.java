package org.monarchinitiative.phenol.io.owl;

import org.geneontology.obographs.model.Node;


import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.RelationshipType;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

/**
 * Interface for constructing concrete  objects in {@link
 * OwlImmutableOntologyLoader}.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public interface OwlOntologyEntryFactory {
  Term constructTerm(Node node, TermId termId);

  Relationship constructRelationship(TermId source, TermId dest, int id, RelationshipType reltype);
}
