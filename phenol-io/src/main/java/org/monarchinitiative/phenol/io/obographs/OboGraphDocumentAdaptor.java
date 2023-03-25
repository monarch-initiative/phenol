package org.monarchinitiative.phenol.io.obographs;

import org.geneontology.obographs.core.model.*;
import org.geneontology.obographs.core.model.meta.BasicPropertyValue;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.io.utils.CurieUtil;
import org.monarchinitiative.phenol.io.utils.CurieUtilBuilder;
import org.monarchinitiative.phenol.ontology.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    // An option used for discarding non-propagating relationships during loading.
    private boolean discardNonPropagatingRelationships = false;

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

    public Builder discardNonPropagatingRelationships(boolean value) {
      this.discardNonPropagatingRelationships = value;
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
          if (!curieUtil.hasPrefix(prefix)) {
            unmappedIdPrefixes.add(prefix);
          }
        }
      }
      return unmappedIdPrefixes;
    }

    private Map<String, String> convertMetaData(Meta meta) {
      if (meta == null) {
        return Map.of();
      }
      SortedMap<String, String> metaMap = new TreeMap<>();
      String version = meta.getVersion() != null ? meta.getVersion() : "";
      metaMap.put("data-version", version);
      String versionInfo = null;
      if (meta.getBasicPropertyValues() != null) {
        for (BasicPropertyValue basicPropertyValue : meta.getBasicPropertyValues()) {
          if (basicPropertyValue.getPred().equalsIgnoreCase("date")) {
            String date = basicPropertyValue.getVal().trim();
            metaMap.put("date", date);
          } else if (basicPropertyValue.getPred().contains("#versionInfo")) {
            versionInfo = basicPropertyValue.getVal();
          }
        }
      }
      if (versionInfo != null)
        metaMap.put("release", versionInfo);
      else if (version != null) {
        Optional<String> releaseDate = findDate(version);
        if (releaseDate.isPresent())
          metaMap.put("release", releaseDate.get());
        else
          LOGGER.warn("Unable to parse release from IRI `{}`.", version);
      } else
        LOGGER.warn("Unable to retrieve release for ontology.");

      return Collections.unmodifiableSortedMap(metaMap);
    }

    /**
     * Find exactly one date matching `20YY-MM-DD` pattern in given payload.
     * @return date string (e.g. 2022-04-04)
     */
    private static Optional<String> findDate(String payload) {
      // A string like `http://purl.obolibrary.org/obo/hp/releases/2021-06-08/hp.json`
      Pattern datePattern = Pattern.compile("(?<value>20\\d{2}-\\d{2}-\\d{2})");
      Matcher matcher = datePattern.matcher(payload);
      String value;
      if (matcher.find()) {
        value = matcher.group("value");
      } else {
        return Optional.empty();
      }

      if (matcher.find()) {
        LOGGER.warn("More than one match for date in IRI `{}`", payload);
        return Optional.empty();
      } else {
        return Optional.of(value);
      }
    }

    private List<Term> convertNodesToTerms(List<Node> nodes) {
      List<Term> termsList = new ArrayList<>();
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
      return List.copyOf(termsList);
    }

    private List<Relationship> convertEdgesToRelationships(List<Edge> edges, List<Node> nodes) {
      Map<String, String> propertyIdLabels = nodes.stream()
        .filter(node -> node.getType() == Node.RDFTYPES.PROPERTY)
        .filter(node -> node.getId() != null && node.getLabel() != null)
        .collect(toMap(Node::getId, Node::getLabel));

      List<Relationship> relationshipsList = new ArrayList<>();
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
          if (discardNonPropagatingRelationships && !relType.propagates())
            // The user decided to drop non-propagating relationships.
            continue;

          Relationship relationship = new Relationship(subjectTermId, objectTermId, edgeId++, relType);
          relationshipsList.add(relationship);
        }
      }
      return List.copyOf(relationshipsList);
    }

    private TermId getTermIdOrNull(String id) {
      Optional<TermId> curie = curieUtil.getCurie(id);
      if (curie.isEmpty()) {
        LOGGER.warn("No matching curie found for id: {}", id);
        return null;
      }

      TermId termId = curie.get();
      // Note that GO has some Terms/Relations with RO and BFO that we want to skip
      String prefix = termId.getPrefix();
      if (wantedTermIdPrefixes.isEmpty() || wantedTermIdPrefixes.contains(prefix)) {
        return termId;
      }
      return null;
    }
  }
}
