package org.monarchinitiative.phenol.io.owl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;

import org.geneontology.obographs.model.Edge;
import org.geneontology.obographs.model.Graph;
import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.model.Node;
import org.geneontology.obographs.owlapi.FromOwl;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.ontology.data.*;
import org.prefixcommons.CurieUtil;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.util.CompatibilityChecker;
import org.monarchinitiative.phenol.io.utils.CurieMapGenerator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Load OWL into an {@link ImmutableOntology}.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public final class OwlImmutableOntologyLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(OwlImmutableOntologyLoader.class);
  private static CurieUtil curieUtil;
  private final File file;

  private Collection<TermId> nonDepreTermIdNodes = Sets.newHashSet();
  private Collection<TermId> depreTermIdNodes = Sets.newHashSet();
  private SortedMap<TermId, Term> terms = Maps.newTreeMap();
  private Collection<TermId> termIdNodes = Sets.newHashSet();
  private Map<Integer, Relationship> relationMap = Maps.newHashMap();

  public OwlImmutableOntologyLoader(File file) {
    this.file = file;
    curieUtil = new CurieUtil(CurieMapGenerator.generate());
  }

  public ImmutableOntology load(OwlOntologyEntryFactory factory)
      throws OWLOntologyCreationException {

    // We first load ontologies expressed in owl using Obographs's FromOwl class.
    OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    OWLOntology ontology = m.loadOntologyFromOntologyDocument(file);
    FromOwl fromOwl = new FromOwl();
    GraphDocument gd = fromOwl.generateGraphDocument(ontology);

    // We assume there is only one graph instance in the graph document instance.
    Graph obograph = gd.getGraphs().get(0);
    if (obograph == null) {
      LOGGER.warn("No graph in the loaded ontology.");
      return null;
    }

    List<Node> gNodes = obograph.getNodes();
    if (gNodes == null) {
      LOGGER.warn("No nodes found in the loaded ontology.");
      return null;
    }

    // Mapping edges in obographs to termIds in phenol
    int edgeId = 1;
    DefaultDirectedGraph<TermId, IdLabeledEdge> phenolGraph =
        new DefaultDirectedGraph<>(IdLabeledEdge.class);
    final ClassBasedEdgeFactory<TermId, IdLabeledEdge> edgeFactory =
        new ClassBasedEdgeFactory<>(IdLabeledEdge.class);

    Set<String> rootCandSet = Sets.newHashSet();
    Set<String> removeMarkSet = Sets.newHashSet();

    // Mapping nodes in obographs to termIds in phenol
    for (Node node : gNodes) {
      String nodeId = node.getId();
      Optional<String> nodeCurie = curieUtil.getCurie(nodeId);
      if (nodeCurie.isPresent() != true) continue;
      TermId termId = TermId.constructWithPrefix(nodeCurie.get());
      Term term = factory.constructTerm(node, termId);
      TermPrefix oioPrefix = new TermPrefix("OIO");
      TermPrefix iaoPrefix = new TermPrefix("IAO");
      if (term.getId().getPrefix().equals(oioPrefix) || term.getId().getPrefix().equals(iaoPrefix)) continue;

      if (term.isObsolete()) {
        depreTermIdNodes.add(termId);
      } else {
        nonDepreTermIdNodes.add(termId);
      }

      phenolGraph.addVertex(termId);
      terms.put(termId, term);
    }

    termIdNodes.addAll(depreTermIdNodes);
    termIdNodes.addAll(nonDepreTermIdNodes);

    List<Edge> gEdges = obograph.getEdges();
    if (gEdges == null) {
      LOGGER.warn("No edges found in the loaded ontology.");
      return null;
    }

    for (Edge edge : gEdges) {
      String subId = edge.getSub();
      String propId = edge.getPred();
      String objId = edge.getObj();

      Optional<String> subCurie = curieUtil.getCurie(subId);
      if (subCurie.isPresent() != true) {
        LOGGER.warn("No matching curie found for edge's subject: " + subId);
        continue;
      }

      Optional<String> objCurie = curieUtil.getCurie(objId);
      if (objCurie.isPresent() != true) {
        LOGGER.warn("No matching curie found for edge's object: " + objId);
        continue;
      }

      String subCurieStr = subCurie.get();
      String objCurieStr = objCurie.get();
      TermId subTermId = TermId.constructWithPrefix(subCurieStr);
      TermId objTermId = TermId.constructWithPrefix(objCurieStr);

      // For each edge and connected nodes,
      // we add candidate obj nodes in rootCandSet, i.e. nodes that have incoming edges.
      // we then remove subj nodes from rootCandSet, i.e. nodes that have outgoing edges.
      rootCandSet.add(objId);
      removeMarkSet.add(propId);
      removeMarkSet.add(subId);

      phenolGraph.addVertex(subTermId);
      phenolGraph.addVertex(objTermId);
      IdLabeledEdge e = edgeFactory.createEdge(subTermId, objTermId);
      e.setId(edgeId);
      phenolGraph.addEdge(subTermId, objTermId, e);

      Relationship ctr = factory.constructRelationship(subTermId, objTermId, edgeId);
      relationMap.put(edgeId, ctr);

      edgeId += 1;
    }

    rootCandSet.removeAll(removeMarkSet);
    CompatibilityChecker.check(phenolGraph.vertexSet(), phenolGraph.edgeSet());

    // Let's not concern about the meta information of graph yet.
    // Meta gMeta = g.getMeta();
    Map<String, String> metaInfo = new HashMap<>();

    // A heuristic for determining root node(s).
    // If there are multiple candidate roots, we will just put owl:Thing as the root one.
    TermId rootId;
    if (rootCandSet.size() > 1 || rootCandSet.isEmpty()) {
      rootId = TermId.constructWithPrefix("owl:Thing");
    } else {
      List<String> rootCandList = new ArrayList<>(rootCandSet);
      String rootCandCurie = curieUtil.getCurie(rootCandList.get(0)).get();
      rootId = TermId.constructWithPrefix(rootCandCurie);
    }

    return new ImmutableOntology(
        ImmutableSortedMap.copyOf(metaInfo),
        phenolGraph,
        rootId,
        nonDepreTermIdNodes,
        depreTermIdNodes,
        ImmutableMap.copyOf(terms),
        ImmutableMap.copyOf(relationMap));
  }
}
