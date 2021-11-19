package org.monarchinitiative.phenol.io.obographs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.geneontology.obographs.core.model.*;
import org.geneontology.obographs.core.model.meta.BasicPropertyValue;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.io.utils.CurieUtilBuilder;
import org.monarchinitiative.phenol.ontology.data.*;
import org.prefixcommons.CurieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.stream.Collectors.toMap;


/**
 * Adaptor class for converting {@link GraphDocument} instances to
 * {@link org.monarchinitiative.phenol.ontology.data.Ontology} instances.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class OboGraphDocumentAdaptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(OboGraphDocumentAdaptor.class);

  private final Map<String, String> metaInfo;
  private final List<Term> terms;
  private final List<Relationship> relationships;

  private OboGraphDocumentAdaptor(Builder builder) {
    this.metaInfo = builder.metaInfo;
    this.terms = builder.terms;
    this.relationships = builder.relationships;
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

  public Ontology buildOntology() {
    return ImmutableOntology.builder()
      .metaInfo(metaInfo)
      .terms(terms)
      .relationships(relationships)
      .build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    // Factory object that adds OBO-typical data to each term.
    private OboGraphTermFactory factory = new OboGraphTermFactory();
    private CurieUtil curieUtil = CurieUtilBuilder.defaultCurieUtil();
    private Set<String> wantedTermIdPrefixes = Collections.emptySet();

    private Map<String, String> metaInfo;
    private List<Term> terms;
    private List<Relationship> relationships;

    public Builder curieUtil(CurieUtil curieUtil) {
      Objects.requireNonNull(curieUtil);
      this.curieUtil = curieUtil;
      return this;
    }

    public Builder wantedTermIdPrefixes(Set<String> wantedTermIdPrefixes) {
      Objects.requireNonNull(wantedTermIdPrefixes);
      this.wantedTermIdPrefixes = wantedTermIdPrefixes;
      return this;
    }

    public OboGraphDocumentAdaptor build(GraphDocument graphDocument) {
      // check the curieUtil contains a mapping for the requested prefixes otherwise
      // they will not be included in the output and users will not get the graph they asked for
      List<String> unMappedIdPrefixes = getWantedButUnmappedIdPrefixes();
      if (!unMappedIdPrefixes.isEmpty()) {
        String message = String.format("Unable to filter terms for prefix(s) %s as these not mapped. Add the mapping to CurieUtil.", unMappedIdPrefixes);
        throw new PhenolRuntimeException(message);
      }

      Graph oboGraph = getFirstGraph(graphDocument);

      LOGGER.debug("Converting graph document...");
      LOGGER.debug("Converting metadata...");
      // Metadata about the ontology
      this.metaInfo = convertMetaData(oboGraph.getMeta());
      LOGGER.debug("Converting nodes to terms...");
      this.terms = convertNodesToTerms(oboGraph.getNodes());
      LOGGER.debug("Converting edges to relationships...");
      // Mapping edges in obographs to termIds in phenol
      this.relationships = convertEdgesToRelationships(oboGraph.getEdges(), oboGraph.getNodes());

      return new OboGraphDocumentAdaptor(this);
    }

    private Graph getFirstGraph(GraphDocument graphDocument) {
      Objects.requireNonNull(graphDocument);
      List<Graph> graphs = graphDocument.getGraphs();
      if (graphs == null || graphs.isEmpty()) {
        throw new PhenolRuntimeException("GraphDocument is empty");
      }
      // We assume there is only one graph instance in the graph document instance.
      return graphs.get(0);
    }

    private List<String> getWantedButUnmappedIdPrefixes() {
      List<String> unmappedIdPrefixes = new ArrayList<>();
      if(!wantedTermIdPrefixes.isEmpty()) {
        for (String prefix : wantedTermIdPrefixes) {
          if (!curieUtil.getCurieMap().containsKey(prefix)) {
            unmappedIdPrefixes.add(prefix);
          }
        }
      }
      return unmappedIdPrefixes;
    }

    private Map<String, String> convertMetaData(Meta meta) {
      if (meta == null) {
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

    private List<Term> convertNodesToTerms(List<Node> nodes) {
      ImmutableList.Builder<Term> termsList = new ImmutableList.Builder<>();
      if (nodes == null || nodes.isEmpty()) {
        LOGGER.warn("No nodes found in loaded ontology.");
        throw new PhenolRuntimeException("PhenolException: No nodes found in loaded ontology.");
      }
      // Mapping nodes in obographs to termIds in phenol
      for (Node node : nodes) {
//        LOGGER.info("{} {} {}", node.getType(), node.getId(), node.getLabel());
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

    private List<Relationship> convertEdgesToRelationships(List<Edge> edges, List<Node> nodes) {
      Map<String, String> propertyIdLabels = nodes.stream()
        .filter(node -> node.getType() == Node.RDFTYPES.PROPERTY)
        .filter(node -> node.getId() != null && node.getLabel() != null)
        .collect(toMap(Node::getId, Node::getLabel));

      ImmutableList.Builder<Relationship> relationshipsList = new ImmutableList.Builder<>();
      if (edges == null || edges.isEmpty()) {
        LOGGER.warn("No edges found in loaded ontology.");
        throw new PhenolRuntimeException("No edges found in loaded ontology.");
      }
      int edgeId = 1;
      for (Edge edge : edges) {
        TermId subjectTermId = getTermIdOrNull(edge.getSub());
        TermId objectTermId = getTermIdOrNull(edge.getObj());

        if (subjectTermId != null && objectTermId != null) {
          RelationshipType relType = RelationshipType.of(edge.getPred(), propertyIdLabels.getOrDefault(edge.getPred(), "unknown"));
          Relationship relationship = new Relationship(subjectTermId, objectTermId, edgeId++, relType);
          relationshipsList.add(relationship);
        }
      }
      return relationshipsList.build();
    }

    private TermId getTermIdOrNull(String id) {
      Optional<String> curie = curieUtil.getCurie(id);
      if (!curie.isPresent()) {
        LOGGER.warn("No matching curie found for id: {}", id);
        return null;
      }
      String curieStr = curie.get();
      TermId termId = TermId.of(curieStr);
      // Note that GO has some Terms/Relations with RO and BFO that we want to skip
      String prefix = termId.getPrefix();
      if (wantedTermIdPrefixes.isEmpty() || wantedTermIdPrefixes.contains(prefix)) {
        return termId;
      }
      return null;
    }
  }
}
