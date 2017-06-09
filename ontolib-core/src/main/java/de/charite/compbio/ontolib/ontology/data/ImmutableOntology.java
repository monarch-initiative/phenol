package de.charite.compbio.ontolib.ontology.data;

import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ImmutableOntology<T extends Term, R extends TermRelation> implements Ontology<T, R> {

  @Override
  public DirectedGraph<TermID, ? extends Edge<TermID>> getGraph() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<TermID, T> getTermMap() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Map<Integer, R> getEdgeMap() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isRootTerm(TermID tID) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Collection<TermID> getAncestors(TermID tID) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Set<TermID> getAllAncestorTermIDs(Collection<TermID> termIDs, boolean includeRoot) {
    final Set<TermID> result = new HashSet<>();
    for (TermID termID : termIDs) {
      result.add(termID);
      for (TermID ancestorID : getAncestors(termID)) {
        if (includeRoot || !isRootTerm(ancestorID)) {
          result.add(ancestorID);
        }
      }
    }
    return result;
  }

  @Override
  public TermID getRootTermID() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<TermID> getTermIDs() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<Term> getTerms() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int countTerms() {
    // TODO Auto-generated method stub
    return 0;
  }


}
