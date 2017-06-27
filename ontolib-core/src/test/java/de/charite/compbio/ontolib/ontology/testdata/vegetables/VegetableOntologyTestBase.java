package de.charite.compbio.ontolib.ontology.testdata.vegetables;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;

import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import de.charite.compbio.ontolib.ontology.data.ImmutableOntology;
import de.charite.compbio.ontolib.ontology.data.ImmutableTermId;
import de.charite.compbio.ontolib.ontology.data.TermId;

/**
 * Re-useable base class for ontology-using tests.
 *
 * <p>
 * Provides a simple ontology of vegetables with annotations, occurence in recipes.
 * </p>
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class VegetableOntologyTestBase {

  protected ImmutableSortedMap<String, String> metaInfo;

  protected ImmutableList<TermId> vertices;
  protected ImmutableList<ImmutableEdge<TermId>> edges;
  protected ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph;

  protected TermId rootTermId;
  protected ImmutableMap<TermId, VegetableTerm> termMap;
  protected ImmutableMap<TermId, VegetableTerm> obsoleteTermMap;
  protected ImmutableMap<Integer, VegetableTermRelation> relationMap;

  protected ImmutableOntology<VegetableTerm, VegetableTermRelation> ontology;

  protected ImmutableTermId idVegetable;
  protected ImmutableTermId idRootVegetable;
  protected ImmutableTermId idLeafVegetable;
  protected ImmutableTermId idCarrot;
  protected ImmutableTermId idBeet;
  protected ImmutableTermId idPumpkin;
  protected ImmutableTermId idBlueCarrot;

  protected List<VegetableRecipeAnnotation> recipeAnnotations;

  @Before
  public void setUp() {
    metaInfo = ImmutableSortedMap.of();

    idVegetable = ImmutableTermId.constructWithPrefix("VO:0000001");
    idRootVegetable = ImmutableTermId.constructWithPrefix("VO:0000002");
    idLeafVegetable = ImmutableTermId.constructWithPrefix("VO:0000003");
    idCarrot = ImmutableTermId.constructWithPrefix("VO:0000004");
    idBeet = ImmutableTermId.constructWithPrefix("VO:0000005");
    idPumpkin = ImmutableTermId.constructWithPrefix("VO:0000006");
    idBlueCarrot = ImmutableTermId.constructWithPrefix("VO:0000007");

    vertices = ImmutableList.of(idVegetable, idRootVegetable, idLeafVegetable, idCarrot, idBeet,
        idPumpkin);
    edges = ImmutableList.of(ImmutableEdge.construct(idRootVegetable, idVegetable, 1),
        ImmutableEdge.construct(idLeafVegetable, idVegetable, 2),
        ImmutableEdge.construct(idCarrot, idRootVegetable, 3),
        ImmutableEdge.construct(idBeet, idRootVegetable, 4),
        ImmutableEdge.construct(idBeet, idLeafVegetable, 5),
        ImmutableEdge.construct(idPumpkin, idRootVegetable, 6),
        ImmutableEdge.construct(idBlueCarrot, idCarrot, 7));
    graph = ImmutableDirectedGraph.construct(edges);

    rootTermId = idVegetable;

    ImmutableMap.Builder<TermId, VegetableTerm> termMapBuilder = ImmutableMap.builder();
    termMapBuilder.put(idVegetable,
        new VegetableTerm(idVegetable, new ArrayList<>(), "vegetable",
            "part of a plant that is consumed", null, new ArrayList<>(), new ArrayList<>(), false,
            null, null));
    termMapBuilder.put(idRootVegetable,
        new VegetableTerm(idRootVegetable, new ArrayList<>(), "root vegetable",
            "consumed root part of plant", null, new ArrayList<>(), new ArrayList<>(), false, null,
            null));
    termMapBuilder.put(idLeafVegetable,
        new VegetableTerm(idLeafVegetable, new ArrayList<>(), "leaf vegetable",
            "consumed leaf part of plant", null, new ArrayList<>(), new ArrayList<>(), false, null,
            null));
    termMapBuilder.put(idCarrot,
        new VegetableTerm(idCarrot, new ArrayList<>(), "carrot",
            "carrots are very tasty root vegetables", null, new ArrayList<>(), new ArrayList<>(),
            false, null, null));
    termMapBuilder.put(idBeet,
        new VegetableTerm(idBeet, new ArrayList<>(), "beet root",
            "beets are tasty and can be used for coloring", null, new ArrayList<>(),
            new ArrayList<>(), false, null, null));
    termMapBuilder.put(idPumpkin,
        new VegetableTerm(idPumpkin, new ArrayList<>(), "pumpkin",
            "pumpkins are great for soup and pickling", null, new ArrayList<>(), new ArrayList<>(),
            false, null, null));
    termMap = termMapBuilder.build();

    obsoleteTermMap = ImmutableMap.of();

    ImmutableMap.Builder<Integer, VegetableTermRelation> relationMapBuilder =
        ImmutableMap.builder();
    relationMapBuilder.put(1, new VegetableTermRelation(idRootVegetable, idVegetable, 1));
    relationMapBuilder.put(2, new VegetableTermRelation(idLeafVegetable, idVegetable, 2));
    relationMapBuilder.put(3, new VegetableTermRelation(idCarrot, idRootVegetable, 3));
    relationMapBuilder.put(4, new VegetableTermRelation(idBeet, idRootVegetable, 4));
    relationMapBuilder.put(5, new VegetableTermRelation(idBeet, idLeafVegetable, 5));
    relationMapBuilder.put(6, new VegetableTermRelation(idPumpkin, idRootVegetable, 6));
    relationMapBuilder.put(7, new VegetableTermRelation(idBlueCarrot, idCarrot, 7));
    relationMap = relationMapBuilder.build();

    ontology = new ImmutableOntology<VegetableTerm, VegetableTermRelation>(metaInfo, graph,
        rootTermId, termMap, obsoleteTermMap, relationMap);

    recipeAnnotations = Lists.newArrayList(new VegetableRecipeAnnotation(idCarrot, "pumpkin soup"),
        new VegetableRecipeAnnotation(idPumpkin, "pumpkin soup"),
        new VegetableRecipeAnnotation(idPumpkin, "pickled pumpkin"),
        new VegetableRecipeAnnotation(idBeet, "pumpkin soup"),
        new VegetableRecipeAnnotation(idBlueCarrot, "beet surpreme"),
        new VegetableRecipeAnnotation(idBeet, "beet surpreme"));
  }

}
