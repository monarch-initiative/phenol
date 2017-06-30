package de.charite.compbio.ontolib.ontology.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.ontolib.graph.algo.TopologicalSorting;
import de.charite.compbio.ontolib.graph.algo.VertexVisitor;
import de.charite.compbio.ontolib.graph.data.DirectedGraph;
import de.charite.compbio.ontolib.graph.data.Edge;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.TermId;

// TODO: test me!

/**
 * Shortest path table for {@link Ontology} objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class ShortestPathTable {

  /** Integer used for "no path". */
  public static final int DISTANCE_INFINITY = -1;

  /** Number of terms. */
  private final int termIdCount;

  /** Mapping from term ID to term index. */
  private final HashMap<TermId, Integer> termIdToIdx;

  /** Precomputed distances between all terms. */
  private final int distances[];

  /**
   * Constructor.
   *
   * The shortest path table will be computed upon construction.
   * 
   * @param ontology
   */
  public ShortestPathTable(Ontology<?, ?> ontology) {
    termIdCount = ontology.getNonObsoleteTermIds().size();
    distances = new int[termIdCount * termIdCount];
    termIdToIdx = new HashMap<>(termIdCount);

    int i = 0;
    for (TermId termId : ImmutableSortedSet.copyOf(ontology.getNonObsoleteTermIds())) {
      termIdToIdx.put(termId, i++);
    }

    precomputeDistances(ontology);
  }

  @SuppressWarnings("unchecked")
  private void precomputeDistances(Ontology<?, ?> ontology) {
    // Initialize with values "infinity".
    for (int i = 0; i < distances.length; ++i) {
      distances[i] = DISTANCE_INFINITY;
    }
    // Precompute distances from topological sorting.
    final Set<TermId> seen = new HashSet<>();
    new TopologicalSorting<TermId, Edge<TermId>, DirectedGraph<TermId, Edge<TermId>>>()
        .startForward((DirectedGraph<TermId, Edge<TermId>>) ontology.getGraph(),
            new VertexVisitor<TermId, Edge<TermId>>() {
              @Override
              public boolean visit(DirectedGraph<TermId, Edge<TermId>> g, TermId termId) {
                // Distance to self is 0.
                setDistance(termId, termId, 0);

                // Compute distance to all seen so far (no other can be reachable, we are doing
                // topological sorting!), via all reachable from here.
                for (TermId destTermId : seen) {
                  int minDist = DISTANCE_INFINITY;
                  final Iterator<TermId> viaIter = g.viaOutEdgeIterator(termId);
                  while (viaIter.hasNext()) {
                    final TermId viaTermId = viaIter.next();
                    final int tmpDist = getDistance(viaTermId, destTermId);
                    if (tmpDist != DISTANCE_INFINITY) {
                      final int candDist = tmpDist + 1;
                      if (minDist == DISTANCE_INFINITY || candDist < minDist) {
                        minDist = candDist;
                      }
                    }
                  }
                  if (minDist != DISTANCE_INFINITY) {
                    setDistance(termId, destTermId, minDist);
                  }
                }

                seen.add(termId);
                return true;
              }
            });
  }

  /**
   * Set distance.
   *
   * @param source Starting term.
   * @param dest Destination term.
   * @param dist Distance value.
   */
  private void setDistance(TermId source, TermId dest, int distance) {
    final Integer idxSource = termIdToIdx.get(source);
    final Integer idxDest = termIdToIdx.get(dest);
    distances[idxSource * termIdCount + idxDest] = distance;
  }

  /**
   * Query for distance (number of edges) between source and destination term.
   * 
   * @param source Starting term.
   * @param dest Destination term.
   * @return Distance between {@code source} and {@code dest}.
   */
  public int getDistance(TermId source, TermId dest) {
    final Integer idxSource = termIdToIdx.get(source);
    final Integer idxDest = termIdToIdx.get(dest);
    if (idxSource == null || idxDest == null) {
      return DISTANCE_INFINITY;
    } else {
      return distances[idxSource.intValue() * termIdCount + idxDest.intValue()];
    }
  }

}
