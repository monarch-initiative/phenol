package org.monarchinitiative.phenol.ontology.testdata.vegetables;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.junit.Before;
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

  @Before
  public void setUp() {
    metaInfo = ImmutableSortedMap.of();

    idVegetable = TermId.constructWithPrefix("VO:0000001");
    idRootVegetable = TermId.constructWithPrefix("VO:0000002");
    idLeafVegetable = TermId.constructWithPrefix("VO:0000003");
    idCarrot = TermId.constructWithPrefix("VO:0000004");
    idBeet = TermId.constructWithPrefix("VO:0000005");
    idPumpkin = TermId.constructWithPrefix("VO:0000006");
    idBlueCarrot = TermId.constructWithPrefix("VO:0000007");

    vertices =
        ImmutableList.of(
            idVegetable,
            idRootVegetable,
            idLeafVegetable,
            idCarrot,
            idBeet,
            idPumpkin,
            idBlueCarrot);

    DefaultDirectedGraph<TermId, IdLabeledEdge> graph =
      new DefaultDirectedGraph<>(IdLabeledEdge.class);
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
        new Term(
            idVegetable,
            new ArrayList<>(),
            "vegetable",
            "part of a plant that is consumed",
            ImmutableList.of(),
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        idRootVegetable,
        new Term(
            idRootVegetable,
            new ArrayList<>(),
            "root vegetable",
            "consumed root part of plant",
          ImmutableList.of(),
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        idLeafVegetable,
        new Term(
            idLeafVegetable,
            new ArrayList<>(),
            "leaf vegetable",
            "consumed leaf part of plant",
          ImmutableList.of(),
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        idCarrot,
        new Term(
            idCarrot,
            new ArrayList<>(),
            "carrot",
            "carrots are very tasty root vegetables",
          ImmutableList.of(),
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        idBlueCarrot,
        new Term(
            idBlueCarrot,
            new ArrayList<>(),
            "blue carrot",
            "blue ones are even better",
          ImmutableList.of(),
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        idBeet,
        new Term(
            idBeet,
            new ArrayList<>(),
            "beet root",
            "beets are tasty and can be used for coloring",
          ImmutableList.of(),
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMapBuilder.put(
        idPumpkin,
        new Term(
            idPumpkin,
            new ArrayList<>(),
            "pumpkin",
            "pumpkins are great for soup and pickling",
          ImmutableList.of(),
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            false,
            null,
            null,
            new ArrayList<>()));
    termMap = termMapBuilder.build();

    ImmutableMap.Builder<Integer, Relationship> relationMapBuilder =
        ImmutableMap.builder();
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
