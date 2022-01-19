package org.monarchinitiative.phenol.ontology.algo;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.algo.BreadthFirstSearch;
import org.monarchinitiative.phenol.ontology.data.*;
import org.monarchinitiative.phenol.utils.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implementation of several commonly needed algorithms for traversing and searching in and {@link
 * Ontology}.
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 * @author <a href="mailto:HyeongSikKIm@lbl.gov">HyeongSik Kim</a>
 */
public class OntologyAlgorithm {

  private static final Logger logger = LoggerFactory.getLogger(OntologyAlgorithm.class);

  private OntologyAlgorithm() {
  }

  public static boolean existsPath(Ontology ontology, TermId sourceID, TermId destID) {
    // special case -- a term cannot have a path to itself in an ontology (DAG)
    if (sourceID.equals(destID)) return false;

    DefaultDirectedGraph<TermId, IdLabeledEdge> graph = ontology.getGraph();
    List<TermId> visited = new ArrayList<>();
    BreadthFirstSearch<TermId, IdLabeledEdge> bfs = new BreadthFirstSearch<>();
    bfs.startFromForward(
        graph,
        sourceID,
        (g, termId) -> {
          visited.add(termId);
          return true;
        });
    return visited.contains(destID);
  }

  /**
   * Find all of the direct children of parentTermId. Include parentTermId itself in the returned
   * set.
   *
   * @param ontology The ontology to which parentTermId belongs
   * @param parentTermId The term whose children were are seeking
   * @return A set of all child terms of parentTermId (including parentTermId itself)
   */
  public static Set<TermId> getChildTerms(Ontology ontology, TermId parentTermId) {
    return getChildTerms(ontology, parentTermId, true);
  }

  /**
   * Find all of the direct children of parentTermId (do not include "grandchildren" and other
   * descendents).
   *
   * @param ontology            The ontology to which parentTermId belongs
   * @param parentTermId        The term whose children were are seeking
   * @param includeOriginalTerm true if we should include the term itself in the set of returned
   *                            child terms
   * @return A set of all child terms of parentTermId
   */
  public static Set<TermId> getChildTerms(Ontology ontology, TermId parentTermId, boolean includeOriginalTerm) {
    Set<TermId> kids = new HashSet<>();
    if (includeOriginalTerm) kids.add(parentTermId);
    for (IdLabeledEdge edge : ontology.getGraph().incomingEdgesOf(parentTermId)) {
      TermId sourceId = (TermId) edge.getSource();
      kids.add(sourceId);
    }
    return Set.copyOf(kids);
  }

  /**
   * Finds the direct child terms of a set of parent terms.
   *
   * @param ontology        The ontology to which the set of parentTermIds belong
   * @param parentTermIdSet The terms whose children were are seeking
   * @return set of children of parentTermIdSet
   */
  public static Set<TermId> getChildTerms(Ontology ontology, Set<TermId> parentTermIdSet) {
    Set<TermId> kids = new HashSet<>();
    for (TermId tid : parentTermIdSet) {
      kids.addAll(getChildTerms(ontology, tid));
    }
    return Set.copyOf(kids);
  }

  /**
   * Finds the direct parent terms of a set of child terms
   *
   * @param ontology The ontology to which the set of childTermIds belong
   * @param childTermIdSet The terms whose parents we are seeking
   * @return set of parents of childTermIdSet
   */
  public static Set<TermId> getParentTerms(Ontology ontology, Set<TermId> childTermIdSet) {
    Set<TermId> parents = new HashSet<>();
    for (TermId tid : childTermIdSet) {
      parents.addAll(getParentTerms(ontology, tid));
    }
    return Set.copyOf(parents);
  }

  public static Set<TermId> getParentTerms(Ontology ontology, Set<TermId> childTermIdSet, boolean includeOriginalTerm) {
    Set<TermId> parents = new HashSet<>();

    for (TermId tid : childTermIdSet) {
      parents.addAll(getParentTerms(ontology, tid, includeOriginalTerm));
    }
    return Set.copyOf(parents);
  }

  /**
   * Find all of the descendents of parentTermId (including direct children and more distant
   * descendents)
   *
   * @param ontology The ontology to which parentTermId belongs
   * @param parentTermId The term whose descendents were are seeking
   * @return A set of all descendents of parentTermId (including the parentTermId itself)
   */
  public static Set<TermId> getDescendents(Ontology ontology, TermId parentTermId) {
    Set<TermId> descset = new HashSet<>();
    Deque<TermId> stack = new ArrayDeque<>();
    stack.push(parentTermId);
    while (!stack.isEmpty()) {
      TermId tid = stack.pop();
      descset.add(tid);
      Set<TermId> directChildrenSet = getChildTerms(ontology, tid, false);
      directChildrenSet.forEach(stack::push);
    }
    return Set.copyOf(descset);
  }

  /**
   * Find all of the direct parents of childTermId (do not include "grandchildren" and other
   * descendents).
   *
   * @param ontology The ontology to which parentTermId belongs
   * @param childTermId The term whose parents were are seeking
   * @param includeOriginalTerm true if we should include the term itself in the set of returned
   *     parent terms
   * @return A set of all parent terms of childTermId
   */
  public static Set<TermId> getParentTerms(Ontology ontology, TermId childTermId, boolean includeOriginalTerm) {
    Set<TermId> anccset = new HashSet<>();
    if (includeOriginalTerm) anccset.add(childTermId);
    for (IdLabeledEdge edge : ontology.getGraph().outgoingEdgesOf(childTermId)) {
      int edgeId = edge.getId();
      Relationship rel = ontology.getRelationMap().get(edgeId);
      if (rel == null) {
        logger.error("Could not retrieve relation for id={} [child term={}]", edgeId, ontology.getTermMap().get(childTermId).getName());
        continue;
      }
      RelationshipType reltyp = rel.getRelationshipType();
      if (! reltyp.propagates()) {
        continue; // this is a relationship that does not follow the annotation-propagation rule (aka true path rule)
      }
      TermId destId = (TermId) edge.getTarget();
      anccset.add(destId);
    }
    return Set.copyOf(anccset);
  }

  // Retrieve all ancestor terms from (sub)ontology where its new root node is rootTerm.
  // All nodes above that root node in the original ontology will be ignored.
  public static Set<TermId> getAncestorTerms(Ontology ontology, TermId rootTerm, Set<TermId> children, boolean includeOriginalTerm) {
    Ontology subontology = ontology.subOntology(rootTerm);
    return getAncestorTerms(subontology, children, includeOriginalTerm);
  }

  public static Set<TermId> getAncestorTerms(Ontology ontology, TermId rootTerm, TermId child, boolean includeOriginalTerm) {
    ImmutableOntology subontology =
      (ImmutableOntology) ontology.subOntology(rootTerm);
    return getAncestorTerms(subontology, child, includeOriginalTerm);
  }

  public static Set<TermId> getAncestorTerms(Ontology ontology, Set<TermId> children, boolean includeOriginalTerm) {
    Set<TermId> builder = new HashSet<>();
    if (includeOriginalTerm) builder.addAll(children);
    Deque<TermId> stack = new ArrayDeque<>();
    Set<TermId> parents = getParentTerms(ontology, children, false);
    for (TermId t : parents) stack.push(t);
    while (!stack.isEmpty()) {
      TermId tid = stack.pop();
      builder.add(tid);
      Set<TermId> prnts = getParentTerms(ontology, tid, false);
      for (TermId t : prnts) stack.push(t);
    }
    return Set.copyOf(builder);
  }

  public static Set<TermId> getAncestorTerms(Ontology ontology, TermId child, boolean includeOriginalTerm) {
    return getAncestorTerms(ontology, Set.of(child), includeOriginalTerm);
  }

  /**
   * Find all of the direct parents of childTermId (do not include "grandchildren" and other
   * descendents).
   *
   * @param ontology The ontology to which parentTermId belongs
   * @param childTermId The term whose parents were are seeking
   * @return A set of all parent terms of childTermId including childTermId itself
   */
  public static Set<TermId> getParentTerms(
    Ontology ontology, TermId childTermId) {
    return getParentTerms(ontology, childTermId, true);
  }

  /**
   * Find all the ancestor terms of childTermId, including parents and so on up to the root. This is
   * a wrapper around the function {@link Ontology#getAncestorTermIds(TermId)} for convenience - it
   * is the counterpart of {@link #getDescendents(Ontology, TermId)}
   *
   * @param ontology The ontology to which childTermId belongs
   * @param childTermId The term whose ancestors were are seeking
   * @return A set of all ancestors of childTermId
   */
  public static Set<TermId> getAncestorTerms(
    Ontology ontology, TermId childTermId) {
    return ontology.getAncestorTermIds(childTermId);
  }

  public static boolean isSubclass(
    Ontology ontology, TermId source, TermId dest) {
    return ontology.getAncestorTermIds(source).contains(dest);
  }

  public static boolean termsAreSiblings(
    Ontology ontology, TermId t1, TermId t2) {
    Set<TermId> parents1 = getParentTerms(ontology, t1, false); // false=do not include t1 itself
    Set<TermId> parents2 = getParentTerms(ontology, t2, false); // ditto
    return (!Sets.intersection(parents1, parents2).isEmpty());
  }

  /**
   * @param ontology An ontology
   * @param t1 One Ontology term
   * @param t2 Another ontology term
   * @return true iff t1 and t2 have a common ancestor that is not the root of the ontology
   */
  public static boolean termsAreRelated(
    Ontology ontology, TermId t1, TermId t2) {
    Set<TermId> ancs1 = getAncestorTerms(ontology, t1, false);
    Set<TermId> ancs2 = getAncestorTerms(ontology, t2, false);
    Set<TermId> isect = Sets.intersection(ancs1, ancs2);
    for (TermId tid : isect) {
      if (!ontology.isRootTerm(tid)) {
        return true;
      }
    }
    return false;
  }

  public static boolean termsAreUnrelated(
    Ontology ontology, TermId t1, TermId t2) {
    return (!termsAreRelated(ontology, t1, t2));
  }
}
