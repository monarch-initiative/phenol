package org.monarchinitiative.phenol.ontology.testdata.vegetables;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.jupiter.api.BeforeEach;
import org.monarchinitiative.phenol.ontology.data.*;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.util.GraphUtil;
import org.monarchinitiative.phenol.ontology.data.TermId;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;

/**
 * Re-useable base class for ontology-using tests.
 *
 * <p>Provides a simple ontology of vegetables with annotations, occurence in recipes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public class VegetableOntologyTestBase {

  private ImmutableSortedMap<String, String> metaInfo;

  protected ImmutableList<TermId> vertices;
  protected ImmutableList<IdLabeledEdge> edges;
  protected DefaultDirectedGraph<TermId, IdLabeledEdge> graph;

  private TermId rootTermId;
  private ImmutableMap<TermId, Term> termMap;
  private ImmutableMap<Integer, Relationship> relationMap;

  protected ImmutableOntology ontology;

  protected TermId idVegetable;
  protected TermId idRootVegetable;
  protected TermId idLeafVegetable;
  protected TermId idCarrot;
  protected TermId idBeet;
  protected TermId idPumpkin;
  protected TermId idBlueCarrot;

  protected List<VegetableRecipeAnnotation> recipeAnnotations;

  @BeforeEach
  public void setUp() {
    metaInfo = ImmutableSortedMap.of();

    idVegetable = TermId.of("VO:0000001");
    idRootVegetable = TermId.of("VO:0000002");
    idLeafVegetable = TermId.of("VO:0000003");
    idCarrot = TermId.of("VO:0000004");
    idBeet = TermId.of("VO:0000005");
    idPumpkin = TermId.of("VO:0000006");
    idBlueCarrot = TermId.of("VO:0000007");

    vertices = ImmutableList.of(
            idVegetable,
            idRootVegetable,
            idLeafVegetable,
            idCarrot,
            idBeet,
            idPumpkin,
            idBlueCarrot);

    DefaultDirectedGraph<TermId, IdLabeledEdge> graph = new DefaultDirectedGraph<>(IdLabeledEdge.class);
    GraphUtil.addEdgeToGraph(graph, idRootVegetable, idVegetable, 1);
    GraphUtil.addEdgeToGraph(graph, idLeafVegetable, idVegetable, 2);
    GraphUtil.addEdgeToGraph(graph, idCarrot, idRootVegetable, 3);
    GraphUtil.addEdgeToGraph(graph, idBeet, idRootVegetable, 4);
    GraphUtil.addEdgeToGraph(graph, idBeet, idLeafVegetable, 5);
    GraphUtil.addEdgeToGraph(graph, idPumpkin, idRootVegetable, 6);
    GraphUtil.addEdgeToGraph(graph, idBlueCarrot, idCarrot, 7);

    rootTermId = idVegetable;

    ImmutableMap.Builder<TermId, Term> termMapBuilder = ImmutableMap.builder();
    termMapBuilder.put(
      idVegetable,
      Term.builder()
        .id(idVegetable)
        .name("vegetable")
        .definition("part of a plant that is consumed")
        .build()
    );
    termMapBuilder.put(
      idRootVegetable,
      Term.builder()
        .id(idRootVegetable)
        .name("root vegetable")
        .definition("consumed root part of plant")
        .build()
    );
    termMapBuilder.put(
      idLeafVegetable,
      Term.builder()
        .id(idLeafVegetable)
        .name("leaf vegetable")
        .definition("consumed leaf part of plant")
        .build()
    );
    termMapBuilder.put(
      idCarrot,
      Term.builder()
        .id(idCarrot)
        .name("carrot")
        .definition("carrots are very tasty root vegetables")
        .build()
    );
    termMapBuilder.put(
      idBlueCarrot,
      Term.builder()
        .id(idBlueCarrot)
        .name("blue carrot")
        .definition("blue ones are even better")
        .build()
    );
    termMapBuilder.put(
      idBeet,
      Term.builder()
        .id(idBeet)
        .name("beet root")
        .definition("beets are tasty and can be used for coloring")
        .build()
    );
    termMapBuilder.put(
      idPumpkin,
      Term.builder()
        .id(idPumpkin)
        .name("pumpkin")
        .definition("pumpkins are great for soup and pickling")
        .build()
    );
    termMap = termMapBuilder.build();

    ImmutableMap.Builder<Integer, Relationship> relationMapBuilder = ImmutableMap.builder();
    relationMapBuilder.put(1, new Relationship(idRootVegetable, idVegetable, 1, RelationshipType.IS_A));
    relationMapBuilder.put(2, new Relationship(idLeafVegetable, idVegetable, 2,RelationshipType.IS_A));
    relationMapBuilder.put(3, new Relationship(idCarrot, idRootVegetable, 3,RelationshipType.IS_A));
    relationMapBuilder.put(4, new Relationship(idBeet, idRootVegetable, 4,RelationshipType.IS_A));
    relationMapBuilder.put(5, new Relationship(idBeet, idLeafVegetable, 5,RelationshipType.IS_A));
    relationMapBuilder.put(6, new Relationship(idPumpkin, idRootVegetable, 6,RelationshipType.IS_A));
    relationMapBuilder.put(7, new Relationship(idBlueCarrot, idCarrot, 7,RelationshipType.IS_A));
    relationMap = relationMapBuilder.build();

    ontology =
        new ImmutableOntology(
            metaInfo, graph, rootTermId, termMap.keySet(), ImmutableSet.of(), termMap, relationMap);

    recipeAnnotations =
        Lists.newArrayList(
            new VegetableRecipeAnnotation(idCarrot, "pumpkin soup"),
            new VegetableRecipeAnnotation(idPumpkin, "pumpkin soup"),
            new VegetableRecipeAnnotation(idPumpkin, "pickled pumpkin"),
            new VegetableRecipeAnnotation(idBeet, "pumpkin soup"),
            new VegetableRecipeAnnotation(idBlueCarrot, "beet surpreme"),
            new VegetableRecipeAnnotation(idBeet, "beet surpreme"));
  }
}
