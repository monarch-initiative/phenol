package org.monarchinitiative.phenol.graph.csrgraph;


import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Compressed Sparse Row Graph representation. This is an efficient representation for static graphs
 * and here is intended to represent the very sparse graph induced by an OBO ontology. The Ontology
 * loader creates a list of Terms (nodes) and relations and currently creates a JGraphT graph object
 * via the following function call in the OntologyLoader class.
 * {@code makeDefaultDirectedGraph(Set<TermId> nonObsoleteTermIds, Collection<Relationship> relationships)}.
 * Currently, this implementation ignores the edge type, but it would be possible to store the edge
 * type in an array that would have the same size as F (we would need to store it for Freverse as well).
 * There may be more performant ways of storing the information -- for instance, we could create
 * a HashMap that only stores relation types that are not IS_A -- this would be good for our major
 * use cases for phenol of traversing a graph and getting MICAs via IS_A edges.
 * @author <a href="mailto:peter.robinson@jax.org">Peter N Robinson</a>
 */
public class CsrDirectedGraph {
    private static final Logger LOGGER = LoggerFactory.getLogger(CsrDirectedGraph.class);
    /** maximim vertex IDs. Valid vertex IDs are [1..V] */
    private final int V;
    /** Total number of edges. */
    private final int E;
    /** Array that is indexed by the "from" ID for directed edges. outdeg(v)==N[1+v]-N[v] */
    private final int [] N;
    /** Array that is indexed ny the "to" ID for directed edges. Accessed via N[] */
    private final int [] F;
    /** analogous to N with reversed edge direction */
    private final int [] Nreverse;
    /** analogous to F with reversed edge direction */
    private final int [] Freverse;

    private final Map<Integer, Integer> termIdToIndexMap;


    //makeDefaultDirectedGraph(Set<TermId> nonObsoleteTermIds, Collection<Relationship> relationships) {

    public CsrDirectedGraph(List<TermId> nonObsoleteTermIds, Collection<Relationship> relationships) {
        this.V = nonObsoleteTermIds.size();
        this.E = relationships.size();
        this.N = new int[2+V];
        this.F = new int[E];
        this.Nreverse = new int[2+V];
        this.Freverse = new int[E];
        // In order to have the data be available as a contiguous array rather than
        // pointers to the heap, we convert the HPO id's to integers, e.g.,
        // HP:000123 would be represented as the integer 123.
        int[] termIdIndexArray = new int[1 + V];
        this.termIdToIndexMap = new HashMap<>(1+V);
        termIdIndexArray[0] = -1; // unused
        int i = 0;
        for (TermId tid : nonObsoleteTermIds) {
            i++;
            int id = Integer.parseInt(tid.getId());
            termIdIndexArray[i] = id;
            this.termIdToIndexMap.put(id, i);
        }
        // Count the outdegree for each node
        // construct a map to hold the outdegree counts, initialize counts to zero
        Map<Integer, List<Integer>> countMap = nonObsoleteTermIds.stream().
                collect(Collectors.toMap(x -> Integer.parseInt(x.getId()), x -> new ArrayList<>()));
        for (var relationship : relationships) {
            int source = Integer.parseInt(relationship.getSource().getId());
            int dest = Integer.parseInt(relationship.getTarget().getId());
            countMap.get(source).add(dest); //
        }



        this.N[0] = -1; // not used, should never be accessed
        this.N[1] = 0; // start of first node
        int t = 0;
        int Fidx = 0;
        for (i=1; i<1+V; i++) {
            int sourceHpoId = termIdIndexArray[i];
            List<Integer> destinationList = countMap.get(sourceHpoId);
            Collections.sort(destinationList);
            t += destinationList.size();
            N[i+1] = t;
            for (int destinationNodeIndex : destinationList) {
                F[Fidx] = destinationNodeIndex;
                Fidx++;
            }
        }

      //  System.out.println("F\n" + Arrays.toString(F));
      //  System.out.println("N\n" + Arrays.toString(N));
        // reverse direction -- this allows us to get incoming edges (children)
        countMap = nonObsoleteTermIds.stream().
                collect(Collectors.toMap(x -> Integer.parseInt(x.getId()), x -> new ArrayList<>()));
        for (var relationship : relationships) {
            int source = Integer.parseInt(relationship.getSource().getId());
            int dest = Integer.parseInt(relationship.getTarget().getId());
            countMap.get(dest).add(source); //
        }
        this.Nreverse[0] = -1; // not used, should never be accessed
        this.Nreverse[1] = 0; // start of first node
        t = 0;
        Fidx = 0;
        for (i=1; i<1+V; i++) {
            int sourceHpoId = termIdIndexArray[i];
            List<Integer> destinationList = countMap.get(sourceHpoId);
            Collections.sort(destinationList);
            t += destinationList.size();
            Nreverse[i+1] = t;
            for (int destinationNodeIndex : destinationList) {
                Freverse[Fidx] = destinationNodeIndex;
                Fidx++;
            }
        }
    }

    /**
     * we store termids as integers. This will work for the OBO ontologies we
     * are most interested in, but this mauy fail for some ontologies. TODO -- consider
     * We do not really need this function, and it is "inlined" for now, keeping
     * this only to remember to think about this.
     * @param id and inter such as 123 representing the HPO term HP:0000123
     * @return corresponding TermId object
     */
    private TermId intToTermId(int id) {
        return TermId.of("HP", String.format("%07d", id));
    }

    public Set<TermId> getOutgoingEdgesOf(TermId sourceId) {
        int id = Integer.parseInt(sourceId.getId());
        if (! this.termIdToIndexMap.containsKey(id)) {
            LOGGER.error("Could not find index for HPO TermId: {}", sourceId.getValue());
            return Set.of();
        }
        int idx = this.termIdToIndexMap.get(id);
        Set<TermId> parents = new HashSet<>();
        int begin = this.N[idx];
        int end = this.N[idx+1];
        for (int i=begin; i<end; i++) {
            parents.add(TermId.of("HP", String.format("%07d", this.F[i])));
        }
        return parents;
    }

    public Set<TermId> getIngoingEdgesOf(TermId sourceId) {
        int id = Integer.parseInt(sourceId.getId());
        if (! this.termIdToIndexMap.containsKey(id)) {
            LOGGER.error("Could not find index for HPO TermId: {}", sourceId.getValue());
            return Set.of();
        }
        int idx = this.termIdToIndexMap.get(id);
        Set<TermId> parents = new HashSet<>();
        int begin = this.Nreverse[idx];
        int end = this.Nreverse[idx+1];
        for (int i=begin; i<end; i++) {
            parents.add(TermId.of("HP", String.format("%07d", this.Freverse[i])));
        }
        return parents;
    }

    public int getVertexCount() {
        return V;
    }

    public int getEdgeCount() {
        return E;
    }
}
