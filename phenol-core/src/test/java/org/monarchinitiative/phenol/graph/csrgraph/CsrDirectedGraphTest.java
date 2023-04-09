package org.monarchinitiative.phenol.graph.csrgraph;


import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import org.monarchinitiative.phenol.ontology.data.RelationshipType;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * We construct a simple graph that we will use to do some sanity checks
 * the root is CSR:A. A has three child, CSR:B, CSR:C, and CSR:D
 * CSR:B has four children: CSR:E, CSR:F, CSR:G, CSR:H,
 * CSR:C has two children CSR:I, CSR:J, and CSR:K
 * CSR:D has five children CSR:L, CSR:M, CSR:N, CSR:O, and CSR:P
 */
public class CsrDirectedGraphTest {

    private static final List<TermId> termIdList;
    private static final List<Relationship> relationships;

    private static final TermId n1 = TermId.of("HP:0000001");
    private static final TermId n2 = TermId.of("HP:0000002");
    private static final TermId n3 = TermId.of("HP:0000003");
    private static final  TermId n4 = TermId.of("HP:0000004");
    private static final TermId n5 = TermId.of("HP:0000005");
    private static final TermId n6 = TermId.of("HP:0000006");
    private static final TermId n7 = TermId.of("HP:0000007");
    private static final TermId n8 = TermId.of("HP:0000008");
    private static final TermId n9 = TermId.of("HP:0000009");
    private static final  CsrDirectedGraph graph;

    static {
        termIdList = List.of(n1,n2,n3,n4,n5, n6,n7,n8, n9);
        List<Relationship> relationsBuilder = new ArrayList<>();
        relationsBuilder.add(new Relationship(n2, n1, 1, RelationshipType.IS_A));
        relationsBuilder.add(new Relationship(n2, n6, 2, RelationshipType.IS_A));
        relationsBuilder.add(new Relationship(n2, n8, 3, RelationshipType.IS_A));
        relationsBuilder.add(new Relationship(n3, n1, 4, RelationshipType.IS_A));
        relationsBuilder.add(new Relationship(n3, n6, 5, RelationshipType.IS_A));
        relationsBuilder.add(new Relationship(n3, n7, 6, RelationshipType.IS_A));
        relationsBuilder.add(new Relationship(n7, n2, 7, RelationshipType.IS_A));
        relationsBuilder.add(new Relationship(n7, n4, 8, RelationshipType.IS_A));
        relationsBuilder.add(new Relationship(n8, n4, 9, RelationshipType.IS_A));
        relationships = List.copyOf(relationsBuilder);
        graph = new CsrDirectedGraph(termIdList, relationships);
    }

    @Test
    public void testDataConstruction() {
        // sanity check
        // expect 16 term ids
        assertEquals(9, termIdList.size());
        // expect 15 relations
        assertEquals(9, relationships.size());
    }


    private TermId intToTermId(int id) {
        // this is not in the public API of the CsrDirectedGraph class, we are just sanity-checking the conversion
        return TermId.of("HP", String.format("%07d", id));
    }

    @Test
    public void testTermIdConversion() {
        TermId id42 = TermId.of("HP:0000042");
        assertEquals(id42, intToTermId(42));
        TermId id14562 = TermId.of("HP:0014562");
        assertEquals(id14562, intToTermId(14562));
    }


    @Test
    public void testGetOutGoingEdges() {
        // node 1 has no parents
        Set<TermId> parents = graph.getOutgoingEdgesOf(n1);
        assertTrue(parents.isEmpty());
        // node 2 has three parents, n1, n6, n8
        parents = graph.getOutgoingEdgesOf(n2);
        assertEquals(3, parents.size());
        assertTrue(parents.contains(n1));
        assertTrue(parents.contains(n6));
        assertTrue(parents.contains(n8));
        // node 3 has three parents, n1, n6, n7
        parents = graph.getOutgoingEdgesOf(n3);
        assertEquals(3, parents.size());
        assertTrue(parents.contains(n1));
        assertTrue(parents.contains(n6));
        assertTrue(parents.contains(n7));
        // node 4 has no parents
        parents = graph.getOutgoingEdgesOf(n4);
        assertTrue(parents.isEmpty());
        // node 5 has no parents
        parents = graph.getOutgoingEdgesOf(n5);
        assertTrue(parents.isEmpty());
        // node 6 has no parents
        parents = graph.getOutgoingEdgesOf(n6);
        assertTrue(parents.isEmpty());
        // node 7 has two parents, n2, n4
        parents = graph.getOutgoingEdgesOf(n7);
        assertEquals(2, parents.size());
        assertTrue(parents.contains(n2));
        assertTrue(parents.contains(n4));
        // node8 has one parent, n4
        parents = graph.getOutgoingEdgesOf(n8);
        assertEquals(1, parents.size());
        assertTrue(parents.contains(n4));
        // node 9 has no parents
        parents = graph.getOutgoingEdgesOf(n9);
        assertTrue(parents.isEmpty());
    }



    @Test
    public void testGetIngoingEdges() {
        // node 1 has two chlildren, n2, n3
        Set<TermId> children = graph.getIngoingEdgesOf(n1);
        assertEquals(2, children.size());
        assertTrue(children.contains(n2));
        assertTrue(children.contains(n3));
        // node 2 has one child, n7
        children = graph.getIngoingEdgesOf(n2);
        assertEquals(1, children.size());
        assertTrue(children.contains(n7));
        // node 3 has no children
        children = graph.getIngoingEdgesOf(n3);
        assertTrue(children.isEmpty());
        // node 4 has two children, n7, n8
        children = graph.getIngoingEdgesOf(n4);
        assertEquals(2, children.size());
        assertTrue(children.contains(n7));
        assertTrue(children.contains(n8));
        // node 5 has no children
        children = graph.getIngoingEdgesOf(n5);
        assertTrue(children.isEmpty());
        // node 6 has two children, n7, n8
        children = graph.getIngoingEdgesOf(n6);
        assertEquals(2, children.size());
        assertTrue(children.contains(n2));
        assertTrue(children.contains(n3));
        // node 7 has one child, n3
        children = graph.getIngoingEdgesOf(n7);
        assertEquals(1, children.size());
        assertTrue(children.contains(n3));
        // node 8 has one child, n2
        children = graph.getIngoingEdgesOf(n8);
        assertEquals(1, children.size());
        assertTrue(children.contains(n2));
        // node 9 has no children
        children = graph.getIngoingEdgesOf(n9);
        assertTrue(children.isEmpty());
    }

    @Test
    public void testVertexCount() {
        assertEquals(9, graph.getVertexCount());
    }


    @Test
    public void testEdgeCount() {
        assertEquals(9, graph.getEdgeCount());
    }


}
