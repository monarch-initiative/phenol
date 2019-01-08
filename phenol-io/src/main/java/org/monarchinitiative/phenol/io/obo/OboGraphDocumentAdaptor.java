package org.monarchinitiative.phenol.io.obo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.geneontology.obographs.model.*;
import org.geneontology.obographs.model.meta.BasicPropertyValue;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.utils.CurieMapGenerator;
import org.monarchinitiative.phenol.ontology.data.*;
import org.prefixcommons.CurieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Adaptor class for converting {@link org.geneontology.obographs.model.GraphDocument} instances to
 * {@link org.monarchinitiative.phenol.ontology.data.Ontology} instances.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class OboGraphDocumentAdaptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(OboGraphDocumentAdaptor.class);

  private final GraphDocument graphDocument;

  /** Factory object that adds OBO-typical data to each term. */
  private final OboGraphTermFactory factory = new OboGraphTermFactory();
  private final CurieUtil curieUtil = new CurieUtil(CurieMapGenerator.generate());

  public OboGraphDocumentAdaptor(GraphDocument graphDocument) {
    this.graphDocument = graphDocument;
  }

  public Ontology createOntology() throws PhenolException {
    // We assume there is only one graph instance in the graph document instance.
    Graph obograph = graphDocument.getGraphs().get(0);
    if (obograph == null) {
      LOGGER.error("No graph in the loaded ontology.");
      throw new PhenolException("No graph in the loaded ontology.");
    }
    LOGGER.debug("Finished converting to obograph graph");

    LOGGER.debug("Converting metadata...");
    // Metadata about the ontology
    Map<String, String> metaInfo = convertMetaData(obograph.getMeta());
    LOGGER.debug("Converting nodes to terms...");
    List<Term> terms = convertNodesToTerms(obograph.getNodes());
    LOGGER.debug("Converting edges to relationships...");
    // Mapping edges in obographs to termIds in phenol
    List<Relationship> relationships = convertEdgesToRelationships(obograph.getEdges());

    LOGGER.debug("Creating phenol ontology");
    Ontology ontology = ImmutableOntology.builder()
      .metaInfo(metaInfo)
      .terms(terms)
      .relationships(relationships)
      .build();
    LOGGER.debug("Done");
    return ontology;
  }

  private Map<String, String> convertMetaData(Meta meta) {
    if (meta == null){
      return ImmutableSortedMap.of();
    }
    ImmutableMap.Builder<String, String> metaInfo = new ImmutableSortedMap.Builder<>(Comparator.naturalOrder());
    String version = meta.getVersion() != null ? meta.getVersion() : "";
    metaInfo.put("data-version", version);
    if (meta.getBasicPropertyValues() != null) {
      for (BasicPropertyValue basicPropertyValue : meta.getBasicPropertyValues()) {
        if (basicPropertyValue.getPred().equalsIgnoreCase("date")) {
          String date = basicPropertyValue.getVal().trim();
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
      throw new PhenolException("PhenolException: No nodes found in the loaded ontology.");
    }
    LOGGER.debug("Mapping nodes...");
    // Mapping nodes in obographs to termIds in phenol
    for (Node node : gNodes) {
      // only take classes, otherwise we may get some OIO and IAO entities
      if (node.getType() != null && node.getType() == Node.RDFTYPES.CLASS) {
        TermId termId = getTermIdOrNull(node.getId());
        if (termId != null) {
          Term term = factory.constructTerm(node, termId);
          terms.add(term);
        }
      }
    }
    return terms;
  }

  private List<Relationship> convertEdgesToRelationships(List<Edge> edges) throws PhenolException{
    List<Relationship> relationships = new ArrayList<>();
    if (edges == null) {
      LOGGER.warn("No edges found in the loaded ontology.");
      throw new PhenolException("PhenolException: No edges found in the loaded ontology.");
    }
    int edgeId = 1;
    for (Edge edge : edges) {
      TermId subjectTermId = getTermIdOrNull(edge.getSub());
      TermId objectTermId = getTermIdOrNull(edge.getObj());

      if (subjectTermId != null && objectTermId != null) {
        RelationshipType reltype = RelationshipType.fromString(edge.getPred());
        Relationship relationship = new Relationship(subjectTermId, objectTermId, edgeId, reltype);
        relationships.add(relationship);
        edgeId++;
      }
    }
    return relationships;
  }

  private TermId getTermIdOrNull(String id) {
    Optional<String> curie = curieUtil.getCurie(id);
    if (!curie.isPresent() ) {
      LOGGER.warn("No matching curie found for id: {}", id);
      return null;
    }
    String curieStr = curie.get();
    TermId termId = TermId.of(curieStr);
    // Note that GO has some Terms/Relations with RO and BFO that we want to skip
    String prefix = termId.getPrefix();
    if (prefix.equals("BFO") || prefix.equals("RO")) {
      return null;
    }
    return termId;
  }

}
