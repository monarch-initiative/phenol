package org.monarchinitiative.phenol.io.obographs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.geneontology.obographs.model.*;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.io.utils.PhenolCurieUtil;
import org.monarchinitiative.phenol.ontology.data.*;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
class OboGraphDocumentAdaptorTest {

  @Test
  void testNullGraphDocument() {
    assertThrows(NullPointerException.class, () -> OboGraphDocumentAdaptor.builder().build(null));
  }

  @Test
  void testEmptyGraphDocument() {
    GraphDocument graphDocument = new GraphDocument.Builder().build();
    assertThrows(PhenolRuntimeException.class, () -> OboGraphDocumentAdaptor.builder().build(graphDocument), "GraphDocument is empty");
  }

  @Test
  void testEmptyGraph() {
    Graph graph = new Graph.Builder().build();
    GraphDocument graphDocument = new GraphDocument.Builder().graphs(ImmutableList.of(graph)).build();
    assertThrows(PhenolRuntimeException.class, () -> OboGraphDocumentAdaptor.builder().build(graphDocument), "No nodes found in loaded ontology.");
  }

  @Test
  void testGraphNoEdges() {
    Node root = new Node.Builder().id("owl:Thing").label("root").type(Node.RDFTYPES.CLASS).build();
    List<Node> nodes = ImmutableList.of(root);
    Graph graph = new Graph.Builder().nodes(nodes).build();
    GraphDocument graphDocument = new GraphDocument.Builder().graphs(ImmutableList.of(graph)).build();
    assertThrows(PhenolRuntimeException.class, () -> OboGraphDocumentAdaptor.builder().build(graphDocument), "No edges found in loaded ontology.");
  }

  @Test
  void testRemovesUnknownCurie() {

    Node root = new Node.Builder()
      .id("http://purl.obolibrary.org/obo/HP_0000001")
      .label("root")
      .type(Node.RDFTYPES.CLASS)
      .build();

    Node unknown = new Node.Builder()
      .id("http://wibble.org/WIBBLE_0000000")
      .label("They have no word for 'fluffy'")
      .type(Node.RDFTYPES.CLASS)
      .build();

    List<Node> nodes = ImmutableList.of(root, unknown);

    Edge edge = new Edge.Builder().sub(unknown.getId()).pred("is_a").obj(root.getId()).build();
    List<Edge> edges = ImmutableList.of(edge);

    Graph graph = new Graph.Builder().nodes(nodes).edges(edges).build();
    GraphDocument graphDocument = new GraphDocument.Builder().graphs(ImmutableList.of(graph)).build();

    OboGraphDocumentAdaptor instance = OboGraphDocumentAdaptor.builder().build(graphDocument);
    assertEquals(1, instance.getTerms().size());
    assertTrue(instance.getRelationships().isEmpty());
  }

  @Test
  void testCanManuallyAddUnknownCurie() {

    Node root = new Node.Builder()
      .id("http://purl.obolibrary.org/obo/HP_0000001")
      .label("root")
      .type(Node.RDFTYPES.CLASS)
      .build();

    Node unknown = new Node.Builder()
      .id("http://wibble.org/WIBBLE_0000000")
      .label("They have no word for 'fluffy'")
      .type(Node.RDFTYPES.CLASS)
      .build();

    List<Node> nodes = ImmutableList.of(root, unknown);

    Edge edge = new Edge.Builder().sub(unknown.getId()).pred("is_a").obj(root.getId()).build();
    List<Edge> edges = ImmutableList.of(edge);

    Graph graph = new Graph.Builder().nodes(nodes).edges(edges).build();
    GraphDocument graphDocument = new GraphDocument.Builder().graphs(ImmutableList.of(graph)).build();

    // WIBBLE id prefix is not in the default list, so add it here so that nodes and edges with this idspace are included
    PhenolCurieUtil curieUtil = PhenolCurieUtil.withDefaultsAnd(ImmutableMap.of("WIBBLE", "http://wibble.org/WIBBLE_"));

    OboGraphDocumentAdaptor instance = OboGraphDocumentAdaptor.builder().curieUtil(curieUtil).build(graphDocument);
    assertEquals(2, instance.getTerms().size());
    assertEquals(1, instance.getRelationships().size());
  }

  @Test
  void testPropertyNodesAreNotIncluded() {

    Node root = new Node.Builder()
      .id("http://purl.obolibrary.org/obo/HP_0000001")
      .label("root")
      .type(Node.RDFTYPES.CLASS)
      .build();

    Node fluffy = new Node.Builder()
      .id("http://purl.obolibrary.org/obo/BFO_0000001")
      .label("froodyness")
      .type(Node.RDFTYPES.PROPERTY)
      .build();

    List<Node> nodes = ImmutableList.of(root, fluffy);

    Edge edge = new Edge.Builder().sub(fluffy.getId()).pred("subPropertyOf").obj(root.getId()).build();
    List<Edge> edges = ImmutableList.of(edge);

    Graph graph = new Graph.Builder().nodes(nodes).edges(edges).build();

    GraphDocument graphDocument = new GraphDocument.Builder().graphs(ImmutableList.of(graph)).build();

    OboGraphDocumentAdaptor instance = OboGraphDocumentAdaptor.builder().build(graphDocument);

    Term rootTerm = Term.of(TermId.of("HP:0000001"), "root");
    assertEquals(ImmutableList.of(rootTerm), instance.getTerms());
    // TODO - should there be any relationships between a property and a class in here?
    // We deliberately skip property nodes when converting them, but this check isn't performed for edges
//    assertTrue(instance.getRelationships().isEmpty());
  }

  @Test
  void testCanManuallyFilterForCurie() {

    Node root = new Node.Builder()
      .id("http://purl.obolibrary.org/obo/HP_0000001")
      .label("root")
      .type(Node.RDFTYPES.CLASS)
      .build();

    Node unknown = new Node.Builder()
      .id("http://wibble.org/WIBBLE_0000000")
      .label("They have no word for 'fluffy'")
      .type(Node.RDFTYPES.CLASS)
      .build();

    List<Node> nodes = ImmutableList.of(root, unknown);

    Edge edge = new Edge.Builder().sub(unknown.getId()).pred("is_a").obj(root.getId()).build();
    List<Edge> edges = ImmutableList.of(edge);

    Graph graph = new Graph.Builder().nodes(nodes).edges(edges).build();
    GraphDocument graphDocument = new GraphDocument.Builder().graphs(ImmutableList.of(graph)).build();

    // WIBBLE id prefix is not in the default list, so add it here so that nodes and edges with this idspace are included
    PhenolCurieUtil curieUtil = PhenolCurieUtil.withDefaultsAnd(ImmutableMap.of("WIBBLE", "http://wibble.org/WIBBLE_"));
    OboGraphDocumentAdaptor instance = OboGraphDocumentAdaptor.builder()
      .curieUtil(curieUtil)
      .wantedTermIdPrefixes(ImmutableSet.of("WIBBLE"))
      .build(graphDocument);

    Term expectedTerm = Term.of(TermId.of("WIBBLE:0000000"), "They have no word for 'fluffy'");
    assertEquals(ImmutableList.of(expectedTerm), instance.getTerms());
    assertTrue(instance.getRelationships().isEmpty());
  }

  @Test
  void throwsExceptionWhenUnknownFilterIdSupplied() {

    Node root = new Node.Builder()
      .id("http://purl.obolibrary.org/obo/HP_0000001")
      .label("root")
      .type(Node.RDFTYPES.CLASS)
      .build();

    Node unknown = new Node.Builder()
      .id("http://wibble.org/WIBBLE_0000000")
      .label("They have no word for 'fluffy'")
      .type(Node.RDFTYPES.CLASS)
      .build();

    List<Node> nodes = ImmutableList.of(root, unknown);

    Edge edge = new Edge.Builder().sub(unknown.getId()).pred("is_a").obj(root.getId()).build();
    List<Edge> edges = ImmutableList.of(edge);

    Graph graph = new Graph.Builder().nodes(nodes).edges(edges).build();
    GraphDocument graphDocument = new GraphDocument.Builder().graphs(ImmutableList.of(graph)).build();

    // WIBBLE id prefix is not in the default list, so add it here so that nodes and edges with this idspace are included
    PhenolCurieUtil curieUtil = PhenolCurieUtil.withDefaultsAnd(ImmutableMap.of("WIBBLE", "http://wibble.org/WIBBLE_"));

    assertThrows(PhenolRuntimeException.class,
      () -> OboGraphDocumentAdaptor.builder()
      .curieUtil(curieUtil)
      .wantedTermIdPrefixes(ImmutableSet.of("FROOD"))
      .build(graphDocument),
      "Unable to filter terms for prefix(s) [FROOD] as these not mapped. Add the mapping to CurieUtil.");
  }

  @Test
  void happyPath() {

    Node root = new Node.Builder()
      .id("http://purl.obolibrary.org/obo/HP_0000001")
      .label("All")
      .type(Node.RDFTYPES.CLASS)
      .build();

    Node modeOfInheritance = new Node.Builder()
      .id("http://purl.obolibrary.org/obo/HP_0000005")
      .label("Mode of inheritance")
      .type(Node.RDFTYPES.CLASS)
      .build();

    Node phenotypicAbnormality = new Node.Builder()
      .id("http://purl.obolibrary.org/obo/HP_0000118")
      .label("Phenotypic abnormality")
      .type(Node.RDFTYPES.CLASS)
      .build();

    List<Node> nodes = ImmutableList.of(root, modeOfInheritance, phenotypicAbnormality);

    Edge moiRoot = new Edge.Builder().sub(modeOfInheritance.getId()).pred("is_a").obj(root.getId()).build();
    Edge paRoot = new Edge.Builder().sub(phenotypicAbnormality.getId()).pred("is_a").obj(root.getId()).build();
    List<Edge> edges = ImmutableList.of(moiRoot, paRoot);

    Meta meta = new Meta.Builder()
      .version("releases/2018-10-09")
//      .addBasicPropertyValue(new BasicPropertyValue.Builder().pred("data-version").val("releases/2018-10-09").build())
      .build();

    Graph graph = new Graph.Builder().meta(meta).nodes(nodes).edges(edges).build();
    GraphDocument graphDocument = new GraphDocument.Builder().graphs(ImmutableList.of(graph)).build();

    OboGraphDocumentAdaptor instance = OboGraphDocumentAdaptor.builder().build(graphDocument);

    Term rootTerm = Term.of(TermId.of("HP:0000001"), "All");
    Term moiTerm = Term.of(TermId.of("HP:0000005"), "Mode of inheritance");
    Term paTerm = Term.of(TermId.of("HP:0000118"), "Phenotypic abnormality");

    assertEquals(ImmutableList.of(rootTerm, moiTerm, paTerm), instance.getTerms());

    Relationship moiRootRel = new Relationship(moiTerm.getId(), rootTerm.getId(), 1, RelationshipType.IS_A);
    Relationship paRootRel = new Relationship(paTerm.getId(), rootTerm.getId(), 2, RelationshipType.IS_A);

    assertEquals(ImmutableList.of(moiRootRel, paRootRel), instance.getRelationships());
    assertEquals(ImmutableMap.of("data-version", "releases/2018-10-09"), instance.getMetaInfo());

    Ontology hpoOntology = instance.buildOntology();
    assertEquals(TermId.of("HP:0000001"), hpoOntology.getRootTermId());
  }

}
