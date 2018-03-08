package org.monarchinitiative.phenol.io.owl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

import org.geneontology.obographs.model.Edge;
import org.geneontology.obographs.model.Graph;
import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.model.Node;
import org.geneontology.obographs.owlapi.FromOwl;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.prefixcommons.CurieUtil;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.io.utils.CurieMapGenerator;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;
import org.monarchinitiative.phenol.ontology.data.TermRelation;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Load OWL into an {@link ImmutableOntology}.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public final class OwlImmutableOntologyLoader<T extends Term, R extends TermRelation>{
	private static final Logger LOGGER = LoggerFactory.getLogger(OwlImmutableOntologyLoader.class);
	private static CurieUtil curieUtil;
	private final File file;

	private Collection<TermId> nonDepreTermIdNodes = Sets.newHashSet();
	private Collection<TermId> depreTermIdNodes = Sets.newHashSet();
	private SortedMap<TermId, T> terms =  Maps.newTreeMap();
	private Collection<TermId> termIdNodes = Sets.newHashSet();
	private Map<Integer, R> relationMap = Maps.newHashMap();

	public OwlImmutableOntologyLoader(File file) {
		this.file = file;
		curieUtil = new CurieUtil(CurieMapGenerator.generate());
	}

	public ImmutableOntology<T, R> load(OwlOntologyEntryFactory<T, R> factory)
			throws IOException, OWLOntologyCreationException {

		// We first load ontologies expressed in owl using Obographs's FromOwl class.
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = m.loadOntologyFromOntologyDocument(file);
		FromOwl fromOwl = new FromOwl();
		GraphDocument gd = fromOwl.generateGraphDocument(ontology);

		// We assume there is only one graph instance in the graph document instance.
		Graph g = gd.getGraphs().get(0);
		if (g == null) {
			LOGGER.warn("No graph in the loaded ontology.");
			return null;
		}

		List<Node> gNodes = g.getNodes();
		if (gNodes == null) {
			LOGGER.warn("No nodes found in the loaded ontology.");
			return null;
		}

		// Mapping nodes in obographs to termIds in phenol
		for (Node node: gNodes) {
			Optional<String> nodeCurie = curieUtil.getCurie(node.getId());
			if (nodeCurie.isPresent() != true) continue;
			ImmutableTermId termId = ImmutableTermId.constructWithPrefix(nodeCurie.get());
			T term = factory.constructTerm(node, termId);

			if (term.isObsolete())
				depreTermIdNodes.add(termId);
			else
				nonDepreTermIdNodes.add(termId);

			terms.put(termId, term);
		}

		termIdNodes.addAll(depreTermIdNodes);
		termIdNodes.addAll(nonDepreTermIdNodes);

		List<Edge> gEdges = g.getEdges();
		if (gEdges == null) {
			LOGGER.warn("No edges found in the loaded ontology.");
			return null;
		}

		// Mapping edges in obographs to termIds in phenol
		int edgeId = 1;
		DefaultDirectedGraph<TermId, IdLabeledEdge> newGraph = new DefaultDirectedGraph<>(IdLabeledEdge.class); 
		final ClassBasedEdgeFactory<TermId, IdLabeledEdge> edgeFactory = new ClassBasedEdgeFactory<>(IdLabeledEdge.class);
		
		for (Edge edge: gEdges) {
			Optional<String> subCurie = curieUtil.getCurie(edge.getSub());
			if (subCurie.isPresent() != true) {
				LOGGER.warn("No matching curie found for edge's subject: " + edge.getSub());
				continue;
			}

			Optional<String> objCurie = curieUtil.getCurie(edge.getObj());
			if (objCurie.isPresent() != true) {
				LOGGER.warn("No matching curie found for edge's object: " + edge.getObj());
				continue;
			}

			ImmutableTermId subTermId = ImmutableTermId.constructWithPrefix(subCurie.get());
			ImmutableTermId objTermId = ImmutableTermId.constructWithPrefix(objCurie.get());
			
			newGraph.addVertex(subTermId);
			newGraph.addVertex(objTermId);
			IdLabeledEdge e = edgeFactory.createEdge(subTermId, objTermId);
			e.setId(edgeId);
			newGraph.addEdge(subTermId, objTermId, e);
			
			R ctr =factory.constructTermRelation(subTermId, objTermId, edgeId);
			relationMap.put(edgeId, ctr);
			
			edgeId += 1;
		}

		// TODO: add graph compatability check here.

		// Let's not concern about the meta information of graph yet.
		/*
		Meta gMeta = g.getMeta();
		*/
		Map<String, String> metaInfo = new HashMap<>();
		
		// Borrowed the codes that create an artificial root from findOrCreateArtificalRoot in OboImmutableOntologyLoader
		final TermPrefix rootPrefix = new ArrayList<TermId>(nonDepreTermIdNodes).get(0).getPrefix();
		final String rootLocalId = "0000000"; // assumption: no term ID value "0"*7
		final TermId rootId = new ImmutableTermId(rootPrefix, rootLocalId);

		return new ImmutableOntology<T, R>(ImmutableSortedMap.copyOf(metaInfo), newGraph,
				rootId, nonDepreTermIdNodes, depreTermIdNodes,
				ImmutableMap.copyOf(terms), ImmutableMap.copyOf(relationMap));
	}
}
