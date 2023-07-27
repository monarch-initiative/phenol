package org.monarchinitiative.phenol.utils;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

public class OntologyUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(OntologyUtils.class);

  private OntologyUtils() {}

  /**
   * Find the root term ID based on {@code terms} and {@code relationships} considering one {@code hierarchy}
   * {@linkplain RelationshipType}.
   * <p>
   * The candidate root term IDs are the term IDs with no outgoing edges. In case there are more than one candidate terms,
   * we add {@link TermIds#owlThing()} as an artificial root term and link the candidates with a relationship.
   * As a consequence, both {@code terms} and {@code relationships} collections must be <em>mutable</em>,
   * to allow adding the artificial root term.
   *
   * @param terms a <em>mutable</em> collection of terms
   * @param relationships a <em>mutable</em> collection
   * @param hierarchy a supplier of the {@linkplain RelationshipType} that denotes the hierarchy
   *                  (e.g. {@link RelationshipType#IS_A})
   * @return the root term - either the original or the artificial/added root term.
   */
  public static TermId findRootTermId(Collection<Term> terms,
                                      Collection<Relationship> relationships,
                                      Supplier<RelationshipType> hierarchy) {
    RelationshipType hierarchyRelationshipType = hierarchy.get();
    Collection<TermId> rootCandidates = findRootCandidates(relationships, hierarchyRelationshipType);
    if (rootCandidates.isEmpty())
      throw new PhenolRuntimeException("No root candidate found.");
    else if (rootCandidates.size() == 1) {
      return rootCandidates.iterator().next();
    } else {
      // No single root candidate, so create a new one and add it into the nodes and edges
      // As per suggestion https://github.com/monarch-initiative/phenol/issues/163#issuecomment-452880405
      // We'll use owl:Thing instead of ID:0000000 so as not to potentially conflict with an existing term id.
      TermId artificialRootTermId = TermIds.owlThing();
      String artificialRootName = "Artificial root term";
      Term artificialRootTerm = Term.of(artificialRootTermId, artificialRootName);
      addArtificialRootTerm(artificialRootTerm, rootCandidates, terms, relationships, hierarchy);
      LOGGER.debug("Created new artificial root term {} {}", artificialRootTermId.getValue(), artificialRootName);

      return artificialRootTermId;
    }
  }

  private static Collection<TermId> findRootCandidates(Iterable<Relationship> relationships,
                                                 RelationshipType hierarchy) {
    Set<TermId> rootCandidateSet = new HashSet<>();
    Set<TermId> removeMarkSet = new HashSet<>();

    // For each edge and connected nodes
    for (Relationship relationship : relationships) {
      // we check if the relationship represents hierarchy,
      if (!hierarchy.equals(relationship.getRelationshipType()))
        // and discard if the relationship does not represent the hierarchy.
        continue;

      // we add candidate obj nodes in rootCandidateSet, i.e. nodes that have incoming edges.
      rootCandidateSet.add(relationship.getTarget());
      // we then remove subj nodes from rootCandidateSet, i.e. nodes that have outgoing edges.
      removeMarkSet.add(relationship.getSource());
    }

    // Finally, we keep the nodes that have no outgoing hierarchy edges.
    rootCandidateSet.removeAll(removeMarkSet);

    return rootCandidateSet;
  }

  private static void addArtificialRootTerm(Term rootTerm,
                                            Iterable<TermId> rootCandidates,
                                            Collection<Term> terms,
                                            Collection<Relationship> relationships,
                                            Supplier<RelationshipType> hierarchy) {
    int nextUsableEdgeId = relationships.stream()
      .map(Relationship::getId)
      .max(Integer::compareTo)
      .map(i -> i + 1) // increment since we prepare the next usable edgeId
      .orElse(0);

    // Add edges pointing to the artificial root term.
    for (TermId rootCandidate : rootCandidates) {
      //Note-for the "artificial root term, we use the IS_A relation
      Relationship relationship = new Relationship(rootCandidate, rootTerm.id(), nextUsableEdgeId++, hierarchy.get());
      LOGGER.debug("Adding new artificial root relationship {}", relationship);
      relationships.add(relationship);
    }

    // Add the artificial root term itself.
    terms.add(rootTerm);
  }

}
