package com.github.phenomics.ontolib.ontology.algo;


import com.github.phenomics.ontolib.formats.hpo.HpoFrequency;
import com.github.phenomics.ontolib.formats.hpo.HpoFrequencyTermIds;
import com.github.phenomics.ontolib.formats.hpo.HpoModeOfInheritanceTermIds;
import com.github.phenomics.ontolib.formats.hpo.HpoSubOntologyRootTermIds;
import com.github.phenomics.ontolib.graph.algo.BreadthFirstSearch;
import com.github.phenomics.ontolib.graph.data.DirectedGraph;
import com.github.phenomics.ontolib.graph.data.Edge;
import com.github.phenomics.ontolib.graph.data.ImmutableEdge;
import com.github.phenomics.ontolib.ontology.data.Ontology;
import com.github.phenomics.ontolib.ontology.data.TermId;
import com.google.common.collect.ImmutableSet;

import java.util.*;

/**
 * Implementation of several commonly needed algorithms for traversing and searching in
 * and {@link com.github.phenomics.ontolib.ontology.data.Ontology}.
 *
 * @see HpoFrequency
 * @see HpoFrequencyTermIds
 * @see HpoModeOfInheritanceTermIds
 * @see HpoSubOntologyRootTermIds
 *
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class OntologyAlgorithm {



  public static boolean existsPath(Ontology ontology, final TermId sourceID, TermId destID){
    // special case -- a term cannot have a path to itself in an ontology (DAG)
    if (sourceID.equals(destID)) return false;
    final DirectedGraph<TermId, ImmutableEdge<TermId>> graph=ontology.getGraph();
    List<TermId> visited = new ArrayList<>();
    BreadthFirstSearch<TermId, ImmutableEdge<TermId>> bfs = new BreadthFirstSearch<>();
    bfs.startFromForward(graph, sourceID, (g, termId) -> {
      visited.add(termId);
      return true;
    });
    return visited.contains(destID);
  }


  /**
   * Find all of the direct children of parentTermId. Include parentTermId itself in the returned set.
   * @param ontology The ontology to which parentTermId belongs
   * @param parentTermId The term whose children were are seeking
   * @return A set of all child terms of parentTermId (including parentTermId itself)
   */
  public static Set<TermId> getChildTerms(Ontology ontology, TermId parentTermId) {
    return getChildTerms(ontology,parentTermId,true);
  }

  /**
   * Find all of the direct children of parentTermId (do not include "grandchildren" and other descendents).
   * @param ontology The ontology to which parentTermId belongs
   * @param parentTermId The term whose children were are seeking
   * @param includeOriginalTerm true if we should include the term itself in the set of returned child terms
   * @return A set of all child terms of parentTermId
   */
  public static Set<TermId> getChildTerms(Ontology ontology, TermId parentTermId, boolean includeOriginalTerm) {
    ImmutableSet.Builder <TermId> kids = new ImmutableSet.Builder<>();
    if (includeOriginalTerm) kids.add(parentTermId);
    Iterator it =  ontology.getGraph().inEdgeIterator(parentTermId);
    while (it.hasNext()) {
      Edge<TermId> edge = (Edge<TermId>) it.next();
      TermId sourceId=edge.getSource();
      kids.add(sourceId);
    }
    return kids.build();
  }

  /**
   * Finds the direct child terms of a set of parent terms.
   * @param ontology The ontology to which the set of parentTermIds belong
   * @param parentTermIdSet The terms whose children were are seeking
   * @return set of children of parentTermIdSet
   */
  public static Set<TermId> getChildTerms(Ontology ontology, Set<TermId> parentTermIdSet) {
    ImmutableSet.Builder <TermId> kids = new ImmutableSet.Builder<>();
    for (TermId tid:parentTermIdSet) {
      kids.addAll(getChildTerms(ontology, tid));
    }
    return kids.build();
  }

  /**
   * Finds the direct parent terms of a set of child terms
   * @param ontology The ontology to which the set of childTermIds belong
   * @param childTermIdSet The terms whose parents we are seeking
   * @return set of parents of childTermIdSet
   */
  public static Set<TermId> getParentTerms(Ontology ontology, Set<TermId> childTermIdSet) {
    ImmutableSet.Builder <TermId> parents = new ImmutableSet.Builder<>();
    for (TermId tid:childTermIdSet) {
      parents.addAll(getParentTerms(ontology, tid));
    }
    return parents.build();
  }




  /**
   * Find all of the descendents of parentTermId (including direct children and more distant descendents)
   * @param ontology The ontology to which parentTermId belongs
   * @param parentTermId The term whose descendents were are seeking
   * @return A set of all descendents of parentTermId (including the parentTermId itself)
   */
  public static Set<TermId> getDescendents(Ontology ontology, TermId parentTermId) {
    ImmutableSet.Builder<TermId> descset = new ImmutableSet.Builder<>();
    Stack<TermId> stack = new Stack<>();
    stack.push(parentTermId);
    while (! stack.empty() ) {
      TermId tid = stack.pop();
      descset.add(tid);
      Set<TermId> directChildrenSet = getChildTerms(ontology,tid,false);
      directChildrenSet.forEach(t -> stack.push(t));
    }
    return descset.build();
  }

  /** Find all of the direct parents of childTermId (do not include "grandchildren" and other descendents).
   * @param ontology The ontology to which parentTermId belongs
   * @param childTermId The term whose parents were are seeking
   * @param includeOriginalTerm true if we should include the term itself in the set of returned parent terms
   * @return A set of all parent terms of childTermId
   */
  public static Set<TermId> getParentTerms(Ontology ontology, TermId childTermId, boolean includeOriginalTerm) {
    ImmutableSet.Builder<TermId> anccset = new ImmutableSet.Builder<>();
    if (includeOriginalTerm) anccset.add(childTermId);
    Iterator it =  ontology.getGraph().outEdgeIterator(childTermId);
    while (it.hasNext()) {
      Edge<TermId> edge = (Edge<TermId>) it.next();
      TermId destId=edge.getDest();
      anccset.add(destId);
    }
    return anccset.build();
  }


  /** Find all of the direct parents of childTermId (do not include "grandchildren" and other descendents).
   * @param ontology The ontology to which parentTermId belongs
   * @param childTermId The term whose parents were are seeking
   * @return A set of all parent terms of childTermId including childTermId itself
   */
  public static Set<TermId> getParentTerms(Ontology ontology, TermId childTermId) {
    return getParentTerms(ontology,childTermId,true);
  }

  /**
   * Find all the ancestor terms of childTermId, including parents and so on up to the root.
   * This is a wrapper around the function {@link Ontology#getAncestorTermIds(TermId)}
   * for convenience - it is the counterpart of {@link #getDescendents(Ontology, TermId)}
   * @param ontology The ontology to which childTermId belongs
   * @param childTermId The term whose ancestors were are seeking
   * @return A set of all ancestors of childTermId
   */
  public static Set<TermId> getAncestorTerms(Ontology ontology, TermId childTermId) {
    return ontology.getAncestorTermIds(childTermId);
  }

}
