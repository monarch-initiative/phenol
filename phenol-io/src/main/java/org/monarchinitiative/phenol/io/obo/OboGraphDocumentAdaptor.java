package org.monarchinitiative.phenol.io.obo;

import com.google.common.collect.ImmutableList;
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

  /** Factory object that adds OBO-typical data to each term. */
  private final OboGraphTermFactory factory = new OboGraphTermFactory();
  private final CurieUtil curieUtil = new CurieUtil(CurieMapGenerator.generate());

  private final Map<String, String> metaInfo;
  private final List<Term> terms;
  private final List<Relationship> relationships;

  public OboGraphDocumentAdaptor(GraphDocument graphDocument) throws PhenolException {
    // We assume there is only one graph instance in the graph document instance.
    Graph obograph = graphDocument.getGraphs().get(0);
    if (obograph == null) {
      LOGGER.error("No graph in the loaded ontology.");
      throw new PhenolException("No graph in the loaded ontology.");
    }
    LOGGER.debug("Finished converting to obograph graph");

    LOGGER.debug("Converting metadata...");
    // Metadata about the ontology
    this.metaInfo = convertMetaData(obograph.getMeta());
    LOGGER.debug("Converting nodes to terms...");
    this.terms = convertNodesToTerms(obograph.getNodes());
    LOGGER.debug("Converting edges to relationships...");
    // Mapping edges in obographs to termIds in phenol
    this.relationships = convertEdgesToRelationships(obograph.getEdges());
  }

  public Map<String, String> getMetaInfo() {
    return metaInfo;
  }

  public List<Term> getTerms() {
    return terms;
  }

  public List<Relationship> getRelationships() {
    return relationships;
  }

  private Map<String, String> convertMetaData(Meta meta) {
    if (meta == null){
      return ImmutableSortedMap.of();
    }
    ImmutableMap.Builder<String, String> metaMap = new ImmutableSortedMap.Builder<>(Comparator.naturalOrder());
    String version = meta.getVersion() != null ? meta.getVersion() : "";
    metaMap.put("data-version", version);
    if (meta.getBasicPropertyValues() != null) {
      for (BasicPropertyValue basicPropertyValue : meta.getBasicPropertyValues()) {
        if (basicPropertyValue.getPred().equalsIgnoreCase("date")) {
          String date = basicPropertyValue.getVal().trim();
          metaMap.put("date", date);
        }
      }
    }
    return metaMap.build();
  }

  private List<Term> convertNodesToTerms(List<Node> nodes) throws PhenolException {
    ImmutableList.Builder<Term> termsList = new ImmutableList.Builder<>();
    if (nodes == null) {
      LOGGER.warn("No nodes found in the loaded ontology.");
      throw new PhenolException("PhenolException: No nodes found in the loaded ontology.");
    }
    LOGGER.debug("Mapping nodes...");
    // Mapping nodes in obographs to termIds in phenol
    for (Node node : nodes) {
      // only take classes, otherwise we may get some OIO and IAO entities
      if (node.getType() != null && node.getType() == Node.RDFTYPES.CLASS) {
        TermId termId = getTermIdOrNull(node.getId());
        if (termId != null) {
          Term term = factory.constructTerm(node, termId);
          termsList.add(term);
        }
      }
    }
    return termsList.build();
  }

  private List<Relationship> convertEdgesToRelationships(List<Edge> edges) throws PhenolException{
    ImmutableList.Builder<Relationship> relationshipsList = new ImmutableList.Builder<>();
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
        relationshipsList.add(relationship);
        edgeId++;
      }
    }
    return relationshipsList.build();
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
