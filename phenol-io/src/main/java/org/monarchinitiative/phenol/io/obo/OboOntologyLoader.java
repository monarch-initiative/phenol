package org.monarchinitiative.phenol.io.obo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import com.google.common.collect.*;
import org.geneontology.obographs.model.*;
import org.geneontology.obographs.model.meta.BasicPropertyValue;
import org.geneontology.obographs.owlapi.FromOwl;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.io.owl.OwlOntologyEntryFactory;
import org.monarchinitiative.phenol.io.owl.Owl2OboTermFactory;
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

/**
 * This class loads an OBO ontology using the OWLAPI.
 * Load OWL into an {@link ImmutableOntology}.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class OboOntologyLoader implements OntologyLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(OboOntologyLoader.class);

  private static final String BFO_PREFIX = "BFO";
  private static final String RO_PREFIX = "RO";

  private final CurieUtil curieUtil;
  private final InputStream obo;

  /** Factory object that adds OBO-typical data to each term. */
  private final OwlOntologyEntryFactory factory;

  /**
   * Construct an OWL loader that can load an OBO ontology.
   * @param file Path to the OBO file
   */
  public OboOntologyLoader(File file) throws FileNotFoundException {
    this(new FileInputStream(file));
  }
  
  public OboOntologyLoader(InputStream obo) {
    this.obo = obo;
    curieUtil = new CurieUtil(CurieMapGenerator.generate());
    this.factory = new Owl2OboTermFactory();
  }

  @Override
  public Ontology load() throws PhenolException {
    // We first load ontologies expressed in owl using Obographs's FromOwl class.
    OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    OWLOntology owlOntology;
    try {
      owlOntology = m.loadOntologyFromOntologyDocument(obo);
    } catch (OWLOntologyCreationException e) {
      throw new PhenolException(e);
    }
    LOGGER.debug("Finished loading OWLOntology");

    LOGGER.debug("Converting to obograph graph");
    FromOwl fromOwl = new FromOwl();
    GraphDocument gd = fromOwl.generateGraphDocument(owlOntology);

    // We assume there is only one graph instance in the graph document instance.
    Graph obograph = gd.getGraphs().get(0);
    if (obograph == null) {
      LOGGER.error("No graph in the loaded ontology.");
      throw new PhenolException("No graph in the loaded ontology.");
    }
    LOGGER.debug("Finished converting to obograph graph");

    // TODO this can become a new GraphDocumentConverter
    LOGGER.debug("Converting metadata...");
    // Metadata about the ontology
    Map<String, String> metaInfo = convertMetaData(obograph.getMeta());
    LOGGER.debug("Converting nodes to terms...");
    List<Term> terms = convertNodesToTerms(obograph.getNodes());
    LOGGER.debug("Converting edges to relationships...");
    // Mapping edges in obographs to termIds in phenol
    int edgeId = 1;
    List<Relationship> relationships = convertEdgesToRelationships(edgeId, obograph.getEdges());

    // TODO and this ought to become an OntologyBuilder.meta().terms().relationships().build();
    LOGGER.debug("Creating phenol ontology");
    Ontology ontology = createOntology(metaInfo, terms, relationships);
    LOGGER.debug("Done");
    return ontology;
  }

  private Ontology createOntology(Map<String, String> metaInfo, List<Term> terms, List<Relationship> relationships) throws PhenolException {
    DefaultDirectedGraph<TermId, IdLabeledEdge> phenolGraph = new DefaultDirectedGraph<>(IdLabeledEdge.class);

    // Term ids of non-obsolete Terms
    Collection<TermId> nonObsoleteTermIds = Sets.newHashSet();
    // Term ids of obsolete Terms
    Collection<TermId> obsoleteTermIds = Sets.newHashSet();
    // Key: a TermId; value: corresponding Term object
    Map<TermId, Term> termsMap = Maps.newTreeMap();

    for (Term term : terms) {
      TermId termId = term.getId();
      if (term.isObsolete()) {
        obsoleteTermIds.add(termId);
      } else {
        nonObsoleteTermIds.add(termId);
        phenolGraph.addVertex(termId);
        termsMap.put(termId, term);
        for (TermId alternateId : term.getAltTermIds()) {
          termsMap.put(alternateId, term);
        }
      }
    }

    Set<TermId> rootCandidateSet = Sets.newHashSet();
    Set<TermId> removeMarkSet = Sets.newHashSet();
    // The relations are numbered incrementally--this is the key, and the value is the corresponding relation.
    Map<Integer, Relationship> relationshipMap = Maps.newHashMap();
    for (Relationship relationship : relationships) {
      TermId subjectTermId = relationship.getSource();
      TermId objectTermId = relationship.getTarget();
      // For each edge and connected nodes,
      // we add candidate obj nodes in rootCandidateSet, i.e. nodes that have incoming edges.
      // we then remove subj nodes from rootCandidateSet, i.e. nodes that have outgoing edges.
      rootCandidateSet.add(objectTermId);
      removeMarkSet.add(subjectTermId);

      IdLabeledEdge e = new IdLabeledEdge(relationship.getId());
      phenolGraph.addVertex(subjectTermId);
      phenolGraph.addVertex(objectTermId);
      phenolGraph.addEdge(subjectTermId, objectTermId, e);
      relationshipMap.put(e.getId(), relationship);
    }

    rootCandidateSet.removeAll(removeMarkSet);
    CompatibilityChecker.check(phenolGraph.vertexSet(), phenolGraph.edgeSet());

    LOGGER.debug("Setting root node...");
    // A heuristic for determining root node(s).
    // If there are multiple candidate roots, we will just put owl:Thing as the root one.
    TermId rootId;
    Optional<TermId> firstId = rootCandidateSet.stream().findFirst();
    if (firstId.isPresent()) {
      if (rootCandidateSet.size() == 1) {
        rootId = firstId.get();
      } else {
        Term rootTerm = createArtificialRootTerm(firstId);
        rootId = rootTerm.getId();
        phenolGraph.addVertex(rootId);
        nonObsoleteTermIds.add(rootId);
        termsMap.put(rootId, rootTerm);
        int edgeId = relationships.stream().mapToInt(Relationship::getId).max().orElse(1);
        for (TermId childOfNewRootTermId : rootCandidateSet) {
          IdLabeledEdge e = new IdLabeledEdge(edgeId++);
          phenolGraph.addEdge(childOfNewRootTermId, rootId, e);
          //Note-for the "artificial root term, we use the IS_A relation
          Relationship relationship = new Relationship(childOfNewRootTermId, rootId, e.getId(), RelationshipType.IS_A);
          relationshipMap.put(e.getId(), relationship);
        }
      }
    } else {
      throw new PhenolException("No root candidate found.");
    }

    return new ImmutableOntology(
      ImmutableSortedMap.copyOf(metaInfo),
      phenolGraph,
      rootId,
      nonObsoleteTermIds,
      obsoleteTermIds,
      ImmutableMap.copyOf(termsMap),
      ImmutableMap.copyOf(relationshipMap));
  }

  private Map<String, String> convertMetaData(Meta meta) {
    if (meta == null){
      return ImmutableSortedMap.of();
    }
    ImmutableMap.Builder<String, String> metaInfo = new ImmutableSortedMap.Builder<>(Comparator.naturalOrder());
    String version = meta.getVersion() != null ? meta.getVersion() : "";
    metaInfo.put("data-version", version);
    if (meta.getBasicPropertyValues() != null) {
      for (BasicPropertyValue bpv : meta.getBasicPropertyValues()) {
        if (bpv.getPred().equalsIgnoreCase("date")) {
          String date = bpv.getVal().trim();
          metaInfo.put("date", date);
        }
      }
    }
    return metaInfo.build();
  }

  private List<Term> convertNodesToTerms(List<Node> gNodes) throws PhenolException {
    List<Term> terms = new ArrayList<>();
    if (gNodes == null) {
      LOGGER.warn("No nodes found in the loaded ontology.");
      throw new PhenolException("PhenolException[ERROR]: No nodes found in the loaded ontology.");
    }
    LOGGER.debug("Mapping nodes...");
    // Mapping nodes in obographs to termIds in phenol
    for (Node node : gNodes) {
      if (node.getType() == null || node.getType() != Node.RDFTYPES.CLASS) {
        continue; // only take classes-- otherwise, we may get some OIO and IAO entities
      }
      String nodeId = node.getId();
      Optional<String> nodeCurie = curieUtil.getCurie(nodeId);
      if (nodeCurie.isPresent()) {
        TermId termId = TermId.of(nodeCurie.get());
        String idPrefix = termId.getPrefix();
        if (isRelationshipOrBasicFormalOntologyTermId(idPrefix)) {
          continue;
        }
        Term term = factory.constructTerm(node, termId);
        terms.add(term);
      }
    }
    return terms;
  }

  private List<Relationship> convertEdgesToRelationships(int edgeId, List<Edge> edges) throws PhenolException{
    List<Relationship> relationships = new ArrayList<>();
    if (edges == null) {
      LOGGER.warn("No edges found in the loaded ontology.");
      throw new PhenolException("PhenolException[ERROR]: No edges found in the loaded ontology.");
    }
    for (Edge edge : edges) {
      String subId = edge.getSub();
      Optional<String> subCurie = curieUtil.getCurie(subId);
      if (!subCurie.isPresent() ) {
        LOGGER.warn("No matching curie found for edge's subject: {}", subId);
        continue;
      }
      String subCurieStr = subCurie.get();
      TermId subjectTermId = TermId.of(subCurieStr);

      String objId = edge.getObj();
      Optional<String> objCurie = curieUtil.getCurie(objId);
      if (!objCurie.isPresent()) {
        LOGGER.warn("No matching curie found for edge's object: {}", objId);
        continue;
      }
      String objCurieStr = objCurie.get();
      TermId objectTermId = TermId.of(objCurieStr);

      // Note that GO has some Terms/Relations with RO and BFO that we want to skip
      if (isRelationshipOrBasicFormalOntologyTermId(subjectTermId.getPrefix())) {
        continue; // for GO, these relations have RO or BFO in both subject and object, no need to check both
      }

      RelationshipType reltype = RelationshipType.fromString(edge.getPred());
      Relationship relationship = new Relationship(subjectTermId, objectTermId, edgeId, reltype);
      relationships.add(relationship);
      edgeId++;
    }
    return relationships;
  }

  private Term createArtificialRootTerm(Optional<TermId> firstId) {
    // getPrefix should always work actually, but if we cannot find a term for some reason, use Owl as the prefix
    String prefix = firstId.map(TermId::getPrefix).orElse("Owl");
    // Assumption: "0000000" is not used for actual terms in any OBO ontology
    TermId artificialTermId = TermId.of(prefix, "0000000");
    return Term.of(artificialTermId, "artificial root term");
  }

  private boolean isRelationshipOrBasicFormalOntologyTermId(String prefix) {
    return prefix.equals(BFO_PREFIX) || prefix.equals(RO_PREFIX);
  }


}
