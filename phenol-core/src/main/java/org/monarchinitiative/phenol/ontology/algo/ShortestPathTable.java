package org.monarchinitiative.phenol.ontology.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.monarchinitiative.phenol.graph.algo.TopologicalSorting;
import org.monarchinitiative.phenol.graph.algo.VertexVisitor;
import org.monarchinitiative.phenol.graph.util.GraphUtil;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ImmutableSortedSet;

// TODO: Separate separation of algorithm and generated data structure to equalize with graph.algo

/**
 * Shortest path table for {@link Ontology} objects.
 *
 * <p>The length is given in number of edges to traverse.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class ShortestPathTable {

  /** Integer used for "no path". */
  private static final int DISTANCE_INFINITY = -1;

  /** Number of terms. */
  private final int termIdCount;

  /** Mapping from term ID to term index. */
  private final HashMap<TermId, Integer> termIdToIdx;

  /** Precomputed distances between all terms. */
  private final int distances[];

  /**
   * Constructor.
   *
   * <p>The shortest path table will be computed upon construction.
   *
   * @param ontology reference to Ontology object
   */
  public ShortestPathTable(Ontology ontology) {
    termIdCount = ontology.getNonObsoleteTermIds().size();
    distances = new int[termIdCount * termIdCount];
    termIdToIdx = new HashMap<>(termIdCount);

    int i = 0;
    for (TermId termId : ImmutableSortedSet.copyOf(ontology.getNonObsoleteTermIds())) {
      termIdToIdx.put(termId, i++);
    }

    precomputeDistances(ontology);
  }

  private void precomputeDistances(Ontology ontology) {
    // Initialize with values "infinity".
    for (int i = 0; i < distances.length; ++i) {
      distances[i] = DISTANCE_INFINITY;
    }
    // Precompute distances from topological sorting.
    new TopologicalSorting<TermId, IdLabeledEdge, DefaultDirectedGraph<TermId, IdLabeledEdge>>()
        .startForward(ontology.getGraph(), new BuildDistanceTableVertexVisitor());
  }

  /**
   * Set distance.
   *
   * @param source Starting term.
   * @param dest Destination term.
   * @param distance Distance value.
   */
  private void setDistance(TermId source, TermId dest, int distance) {
    final Integer idxSource = termIdToIdx.get(source);
    final Integer idxDest = termIdToIdx.get(dest);
    distances[idxSource * termIdCount + idxDest] = distance;
  }

  /**
   * Query for distance (number of edges) between source and destination term.
   *
   * <p>If there is no <b>directed path</b> (in the direction child to parent) between {@code
   * source} and {@code dest} then the result will be {@link #DISTANCE_INFINITY}. See {@link
   * #getDistanceSymmetric(TermId, TermId)} for a variant that also allows directed paths between
   * {@code dest} and {@code source}.
   *
   * @see #getDistanceSymmetric(TermId, TermId)
   * @param source Starting term.
   * @param dest Destination term.
   * @return Distance between {@code source} and {@code dest}; {@link #DISTANCE_INFINITY} if there
   *     is no path.
   */
  public int getDistance(TermId source, TermId dest) {
    final Integer idxSource = termIdToIdx.get(source);
    final Integer idxDest = termIdToIdx.get(dest);
    if (idxSource == null || idxDest == null) {
      return DISTANCE_INFINITY;
    } else {
      return distances[idxSource * termIdCount + idxDest];
    }
  }

  /**
   * Query for distance (number of edges) between source and destination or destination and source
   * term.
   *
   * @see #getDistance(TermId, TermId)
   * @param source Starting term.
   * @param dest Destination term.
   * @return Distance between {@code source} and {@code dest}; {@link #DISTANCE_INFINITY} if there
   *     is no path.
   */
  public int getDistanceSymmetric(TermId source, TermId dest) {
    final int dist = getDistance(source, dest);
    if (dist != DISTANCE_INFINITY) {
      return dist;
    } else {
      return getDistance(dest, source);
    }
  }

  /** Helper class implementing the building of the vertex distance table. */
  private class BuildDistanceTableVertexVisitor implements VertexVisitor<TermId, IdLabeledEdge> {

    /** Set of TermId values that were already handled. */
    private final Set<TermId> seen = new HashSet<>();

    @Override
    public boolean visit(DefaultDirectedGraph<TermId, IdLabeledEdge> g, TermId termId) {
      // Distance to self is 0.
      setDistance(termId, termId, 0);

      // Compute distance to all seen so far (no other can be reachable, we are doing
      // topological sorting!), via all reachable from here.
      for (TermId destTermId : seen) {
        int minDist = DISTANCE_INFINITY;
        final Iterator<TermId> viaIter = GraphUtil.viaOutEdgeIterator(g, termId);
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
  }
}
