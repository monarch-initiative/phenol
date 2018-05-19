package org.monarchinitiative.phenol.formats.uberpheno;

import java.util.Collection;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;

/**
 * Implementation of the Uberpheno ontology as an {@link ImmutableOntology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class UberphenoOntology
    extends ImmutableOntology {

  /** Serial UID for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param metaInfo {@link ImmutableSortedMap} with meta information.
   * @param graph Graph with the ontology's topology.
   * @param rootTermId {@link TermId} of the root term.
   * @param nonObsoleteTermIds {@link Collection} of {@link TermId}s of non-obsolete terms.
   * @param obsoleteTermIds {@link Collection} of {@link TermId}s of obsolete terms.
   * @param termMap Mapping from {@link TermId} to GO term.
   * @param relationMap Mapping from numeric edge identifier to {@link Relationship}.
   */
  public UberphenoOntology(
      ImmutableSortedMap<String, String> metaInfo,
      DefaultDirectedGraph<TermId, IdLabeledEdge> graph,
      TermId rootTermId,
      Collection<TermId> nonObsoleteTermIds,
      Collection<TermId> obsoleteTermIds,
      ImmutableMap<TermId, Term> termMap,
      ImmutableMap<Integer, Relationship> relationMap) {
    super(metaInfo, graph, rootTermId, nonObsoleteTermIds, obsoleteTermIds, termMap, relationMap);
  }

  @Override
  public String toString() {
    return "UberphenoOntology [getMetaInfo()="
        + getMetaInfo()
        + ", getGraph()="
        + getGraph()
        + ", getTermMap()="
        + getTermMap()
        + ", getRelationMap()="
        + getRelationMap()
        + ", getRootTermId()="
        + getRootTermId()
        + ", getAllTermIds()="
        + getAllTermIds()
        + ", getTerms()="
        + getTerms()
        + ", getNonObsoleteTermIds()="
        + getNonObsoleteTermIds()
        + ", getObsoleteTermIds()="
        + getObsoleteTermIds()
        + "]";
  }
}
