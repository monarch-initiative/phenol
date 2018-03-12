package org.monarchinitiative.phenol.io.obo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.monarchinitiative.phenol.base.OntoLibRuntimeException;
import org.monarchinitiative.phenol.graph.IdLabeledEdge;
import org.monarchinitiative.phenol.graph.util.CompatibilityChecker;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermId;
import org.monarchinitiative.phenol.ontology.data.ImmutableTermPrefix;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.monarchinitiative.phenol.ontology.data.TermPrefix;
import org.monarchinitiative.phenol.ontology.data.Relationship;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;

/**
 * Load OBO into an {@link ImmutableOntology}.
 *
 * <p>This is a generic implementation, the actual implementation will come from implementations of
 * the {@link OboOntologyEntryFactory} interface.
 *
 * <h5>Note on Multi-Root Ontologies</h5>
 *
 * <p>In an {@link Ontology}, there can only be one root (vertices of non-obsolete terms that have
 * no incoming "is-a" relation are roots). In the case of multiple root candidates, this class will
 * introduce an artificial root node. The term ID will be generated by taking the prefix of the
 * first term in the OBO file and a numeric identifier of {@code 0} will then be used. All terms
 * without incoming edges will get a new "is-a" relation to this new term.
 *
 * <p>For example, in GO, the term {@code GO:0000000} will be created. This action will be loadded
 * in the log level "INFO".
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 */
public final class OboImmutableOntologyLoader<T extends Term, R extends Relationship> {

  /** The {@link Logger} object to use for logging. */
  private static final Logger LOGGER = LoggerFactory.getLogger(OboParser.class);

  /** File to load from. */
  private final File file;

  /** Parser to use. */
  private final OboParser parser;

  /**
   * Constructor.
   *
   * @param file The {@link File} to load from.
   * @param debug Whether or not to allow debugging.
   */
  public OboImmutableOntologyLoader(File file, boolean debug) {
    this.file = file;
    this.parser = new OboParser(debug);
  }

  /**
   * Constructor, debugging is disabled.
   *
   * @param file The {@link File} to load from.
   */
  public OboImmutableOntologyLoader(File file) {
    this(file, false);
  }

  /**
   * @param factory The {@link OboOntologyEntryFactory} for constructing concrete terms and term
   *     relations.
   * @return Freshly loaded and constructed {@link ImmutableOntology}.
   * @throws IOException In the case of problems with I/O.
   */
  public ImmutableOntology<T, R> load(OboOntologyEntryFactory<T, R> factory) throws IOException {
    // Construct helper, set the term Id mapping into factory, and trigger parsing.
    final HelperListener helper = new HelperListener(factory);
    factory.setTermIds(helper.getTermIds());
    parser.parseFile(file, helper);

    if (helper.getAllTermIds().size() == 0) {
      throw new OntoLibRuntimeException("No terms in ontology?!");
    }

    /*
     * Note on the effect of event-based parsing.
     *
     * If the implementation is correct, we now have a relatively compact (depending on the
     * implementation of factory, of course) representation of the ontology in memory using the
     * objects from <code>ontolib-core</code> only. All objects that have the full representation of
     * the ontology (from <code>ontolib-io</code>) can be freed by GC after this point.
     *
     * The only exception are the Stanza objects that we have to store for the candidate roots.
     */

    // Get term Id (of possibly artificial) root.
    final ImmutableTermId rootTermId = findOrCreateArtificalRoot(helper, factory);

    DefaultDirectedGraph<TermId, IdLabeledEdge> graph =
        new DefaultDirectedGraph<>(IdLabeledEdge.class);
    final ClassBasedEdgeFactory<TermId, IdLabeledEdge> edgeFactory =
        new ClassBasedEdgeFactory<>(IdLabeledEdge.class);

    // Construct edge list and relation map.
    final Map<Integer, R> relationMap = new HashMap<>();
    for (Entry<ImmutableTermId, List<BundledIsARelation>> e :
        helper.getIsATermIdPairs().entrySet()) {
      for (BundledIsARelation b : e.getValue()) {
        ImmutableTermId sourceTermId = e.getKey();
        ImmutableTermId targetTermId = b.getDest();
        graph.addVertex(e.getKey());
        graph.addVertex(b.getDest());
        IdLabeledEdge edge = edgeFactory.createEdge(sourceTermId, targetTermId);
        edge.setId(b.getRelation().getId());
        graph.addEdge(sourceTermId, targetTermId, edge);
        relationMap.put(b.getRelation().getId(), b.getRelation());
      }
    }

    CompatibilityChecker.check(graph.vertexSet(), graph.edgeSet());

    return new ImmutableOntology<T, R>(
        ImmutableSortedMap.copyOf(helper.getMetaInfo()),
        graph,
        rootTermId,
        helper.getTerms().keySet(),
        helper.getObsoleteTerms().keySet(),
        ImmutableMap.copyOf(helper.getTerms()),
        ImmutableMap.copyOf(relationMap));
  }

  /**
   * Find one root from helper's recorded outgoing <code>is_a</code> edges.
   *
   * <p>For ontologies such as GO, an artificial root is created.
   *
   * @param helper Helper to use in loading ontology.
   * @param factory Factory to use for loading ontology.
   * @return The one root's {@link ImmutableTermId}, might be artificially created.
   */
  private ImmutableTermId findOrCreateArtificalRoot(
      OboImmutableOntologyLoader<T, R>.HelperListener helper,
      OboOntologyEntryFactory<T, R> factory) {
    // Find root candidates (no outgoing is-a edges).
    final List<ImmutableTermId> rootCandidates =
        new ArrayList<>(helper.getRootCandidateStanzas().keySet());
    Collections.sort(rootCandidates);

    // Get root or construct a synthetic one. Bail out if no root candidate was found.s
    if (rootCandidates.size() == 0) {
      throw new OntoLibRuntimeException("No term without outgoing is-a edge. Empty or cyclic?");
    } else if (rootCandidates.size() == 1) {
      return rootCandidates.get(0);
    } else {
      final TermPrefix rootPrefix = helper.getFirstTermId().getPrefix();
      final String rootLocalId = "0000000"; // assumption: no term ID value "0"*7
      final ImmutableTermId rootId = new ImmutableTermId(rootPrefix, rootLocalId);
      if (helper.getAllTermIds().contains(rootId)) {
        throw new OntoLibRuntimeException(
            "Tried to guess artificial root as " + rootId + " but is already taken.");
      }

      LOGGER.info(
          "Ontology had {} root terms {}, inserting artificial root term {}",
          new Object[] {rootCandidates.size(), rootCandidates, rootId});

      // Register term Id.
      final String rootIdString = rootPrefix.getValue() + ":" + rootLocalId;
      helper.getTermIds().put(rootIdString, rootId);
      helper.getAllTermIds().add(rootId);

      // Create artificial root term stanza and process through helper.
      final List<StanzaEntry> rootStanzaEntries = new ArrayList<>();
      rootStanzaEntries.add(new StanzaEntryId(rootIdString, null, null));
      rootStanzaEntries.add(new StanzaEntryName("<artificial root>", null, null));
      rootStanzaEntries.add(
          new StanzaEntryRemark(
              "This root was automatically inserted by ontolib on loading as this ontology "
                  + "had more than one term without outgoing 'is_a' relations.",
              null,
              null));
      helper.parsedStanza(Stanza.create(StanzaType.TERM, rootStanzaEntries));

      // Register outgoing edges for root candidates. Here, we create fake "is_a" relations.
      for (ImmutableTermId termId : rootCandidates) {
        final R artificialIsARelation =
            factory.constructrelationship(
                helper.getRootCandidateStanzas().get(termId),
                new StanzaEntryIsA(rootIdString, null, null));
        final BundledIsARelation bundle = new BundledIsARelation(rootId, artificialIsARelation);
        helper.getIsATermIdPairs().put(termId, Lists.newArrayList(bundle));
      }

      // Return artificial root.
      return rootId;
    }
  }

  /**
   * Private helper class implementing the {@link OboParseResultListener} for using event-based
   * parsing.
   *
   * <p>This "little" helper class implements the interface for the event-based parsing and
   * constructs the objects from <code>ontolib-core</code> on the fly from the <code>ontolib-io
   * </code> objects. It also takes care of generating (hopefully) memory-saving representations by
   * ensuring that equal term Id strings are converted to the same identical {@link TermId} object
   * and all equal {@link TermPrefix} are the same object.
   */
  private class HelperListener implements OboParseResultListener {

    /** Term prefix value to {@link ImmutableTermPrefix}. */
    private final Map<String, ImmutableTermPrefix> prefixes = new HashMap<>();

    /** First seen term Id, we will construct the artificial root term if necessary. */
    private ImmutableTermId firstTermId = null;

    /** All TermId objects constructed from Term stanzas (only!), including obsolete terms. */
    private final List<TermId> allTermIds = new ArrayList<>();

    // TODO: At the moment, HP:1 and HP:01 would be mapped to different objects :(
    /** Term strings to terms. */
    private final SortedMap<String, ImmutableTermId> termIds = new TreeMap<>();

    /** Non-obsolete terms constructed from parsing. */
    private final SortedMap<ImmutableTermId, T> terms = new TreeMap<>();

    /** Obsolete terms constructed from parsing. */
    private final SortedMap<ImmutableTermId, T> obsoleteTerms = new TreeMap<>();

    /** Term relations constructed from parsing "is_a" relations. */
    private final SortedMap<ImmutableTermId, List<BundledIsARelation>> isATermIdPairs =
        new TreeMap<>();

    /** Factory for creating concrete term and term relation objects, injected into helper. */
    private final OboOntologyEntryFactory<T, R> ontologyEntryFactory;

    /** We store the {@link Stanza} objects for the root candidates (no outgoing is_a relation). */
    private final Map<ImmutableTermId, Stanza> rootCandidateStanzas = new HashMap<>();

    /** Mapping from string to string with file-wide meta information. */
    private final Map<String, String> metaInfo = new HashMap<>();

    /**
     * Constructor.
     *
     * @param ontologyEntryFactory Factory for creating concrete term and term relation objects.
     */
    public HelperListener(OboOntologyEntryFactory<T, R> ontologyEntryFactory) {
      this.ontologyEntryFactory = ontologyEntryFactory;
    }

    @Override
    public void parsedHeader(Header header) {
      String key = null;
      String value = null;
      for (StanzaEntry e : header.getEntries()) {
        switch (e.getType()) {
          case AUTO_GENERATED_BY:
            key = "auto-generated-by";
            value = ((StanzaEntryAutoGeneratedBy) e).getGenerator();
            break;
          case DATA_VERSION:
            key = "data-version";
            value = ((StanzaEntryDataVersion) e).getValue();
            break;
          case DATE:
            key = "date";
            value = ((StanzaEntryDate) e).getValue();
            break;
          case REMARK:
            key = "remark";
            value = ((StanzaEntryRemark) e).getText();
            break;
          case SAVED_BY:
            key = "saved-by";
            value = ((StanzaEntrySavedBy) e).getValue();
            break;
          default:
            break;
        }
        if (key != null && value != null) {
          metaInfo.put(key, value);
        }
      }
    }

    @Override
    public void parsedStanza(Stanza stanza) {
      if (stanza.getType() == StanzaType.TERM) { // ignore all but the terms
        // Obtain ImmutableTermId for the source term.
        final List<StanzaEntry> idEntries = stanza.getEntryByType().get(StanzaEntryType.ID);
        if (idEntries.size() != 1) {
          throw new RuntimeException(
              "Cardinality of 'id' must be 1 but is " + idEntries.size() + "(" + stanza + ")");
        }
        final StanzaEntryId idEntry = (StanzaEntryId) idEntries.get(0);
        final ImmutableTermId sourceId = registeredTermId(idEntry.getId());

        // Register term ID regardless of being obsolete.
        allTermIds.add(sourceId);

        // Special handling of obsolete terms, they will not go to allTermIds and not into term map.
        if (stanza.getEntryByType().get(StanzaEntryType.IS_OBSOLETE) != null) {
          for (StanzaEntry e : stanza.getEntryByType().get(StanzaEntryType.IS_OBSOLETE)) {
            StanzaEntryIsObsolete entry = (StanzaEntryIsObsolete) e;
            if (entry.getValue()) {
              // Special handling for obsolete terms.
              final T newTerm = ontologyEntryFactory.constructTerm(stanza);
              obsoleteTerms.put(sourceId, newTerm);

              // Construct TermId objects for all alternative ids.
              final List<StanzaEntry> altIdEntries =
                  stanza.getEntryByType().get(StanzaEntryType.ALT_ID);
              if (altIdEntries != null) {
                for (StanzaEntry altIdEntry : altIdEntries) {
                  final ImmutableTermId termId =
                      registeredTermId(((StanzaEntryAltId) altIdEntry).getAltId());
                  obsoleteTerms.put(termId, newTerm);
                }
              }

              return;
            }
          }
        }

        // Construct TermId objects for all alternative ids.
        final List<StanzaEntry> altIdEntries = stanza.getEntryByType().get(StanzaEntryType.ALT_ID);
        final List<ImmutableTermId> altTermIds = new ArrayList<>();
        if (altIdEntries != null) {
          for (StanzaEntry e : altIdEntries) {
            altTermIds.add(registeredTermId(((StanzaEntryAltId) e).getAltId()));
          }
        }

        // TODO: generalize this registration through proper subclassing of StanzaEntry?
        // Interpret all "is_a" relation entries and register them to termIdPairs.
        final List<StanzaEntry> entries = stanza.getEntryByType().get(StanzaEntryType.IS_A);
        if (entries != null) {
          for (StanzaEntry e : entries) {
            StanzaEntryIsA isA = (StanzaEntryIsA) e;
            final ImmutableTermId destId = registeredTermId(isA.getId());
            final BundledIsARelation bundledRelation =
                new BundledIsARelation(
                    destId, ontologyEntryFactory.constructrelationship(stanza, isA));

            if (!isATermIdPairs.containsKey(sourceId)) {
              isATermIdPairs.put(sourceId, Lists.newArrayList(bundledRelation));
            } else {
              isATermIdPairs.get(sourceId).add(bundledRelation);
            }
          }
        }

        // Keep Stanza if this is a candidate root (no is_a relation)
        if (entries == null || entries.isEmpty()) {
          rootCandidateStanzas.put(sourceId, stanza);
        }

        // Construct the term through the factory.
        final T term = ontologyEntryFactory.constructTerm(stanza);
        terms.put(sourceId, term);

        // Put into map from alternative IDs as well.
        for (ImmutableTermId altTermId : altTermIds) {
          terms.put(altTermId, term);
        }
      }
    }

    /**
     * If necessary, insert term Id into {@link #termIds} {@link #prefixes}.
     *
     * @param termIdStr String representation of term Id.
     * @return {@link TermId} present in {@link #termIds}.
     */
    private ImmutableTermId registeredTermId(String termIdStr) {
      ImmutableTermId tmpId = termIds.get(termIdStr);
      if (tmpId != null) {
        return tmpId;
      }

      final int pos = termIdStr.lastIndexOf(':');
      if (pos == -1) {
        throw new OntoLibRuntimeException("Term Id does not contain colon! " + termIdStr);
      }

      final String prefixStr = termIdStr.substring(0, pos);
      final String localIdStr = termIdStr.substring(pos + 1);
      ImmutableTermPrefix tmpPrefix = prefixes.get(prefixStr);

      if (tmpPrefix == null || prefixes.containsKey(tmpPrefix.getValue()) != true) {
        tmpPrefix = new ImmutableTermPrefix(prefixStr);
        prefixes.put(prefixStr, tmpPrefix);
      }

      tmpId = new ImmutableTermId(tmpPrefix, localIdStr);
      termIds.put(termIdStr, tmpId);

      // Make sure to record the first term Id.
      if (firstTermId == null) {
        firstTermId = tmpId;
      }

      return tmpId;
    }

    @Override
    public void parsedFile() {
      /* nop */
    }

    /** @return The map with the meta information. */
    public Map<String, String> getMetaInfo() {
      return metaInfo;
    }

    /** @return Multimap-like encoding of directed terms. */
    public Map<ImmutableTermId, List<BundledIsARelation>> getIsATermIdPairs() {
      return isATermIdPairs;
    }

    /** @return First seen term Id for constructing artificial root. */
    public TermId getFirstTermId() {
      return firstTermId;
    }

    /** @return Set of all seen term ids. */
    public List<TermId> getAllTermIds() {
      return allTermIds;
    }

    /** @return Mapping from string term Id to term Id. */
    public SortedMap<String, ImmutableTermId> getTermIds() {
      return termIds;
    }

    /** @return Mapping from immutable term Id to term with non-obsolete terms. */
    public Map<ImmutableTermId, T> getTerms() {
      return terms;
    }

    /** @return Mapping from immutable term Id to term with obsolete terms. */
    public Map<ImmutableTermId, T> getObsoleteTerms() {
      return obsoleteTerms;
    }

    /** @return The root candidate stanza mapping. */
    public Map<ImmutableTermId, Stanza> getRootCandidateStanzas() {
      return rootCandidateStanzas;
    }
  }

  /** Bundle an ImmutableTermId and a term relation for the is-a term pairs. */
  public class BundledIsARelation {

    /** Destination of is-a relation. */
    private final ImmutableTermId dest;

    /** The bundled relation. */
    private final R relation;

    /**
     * Constructor.
     *
     * @param dest Destination term Id.
     * @param relation Relation.
     */
    public BundledIsARelation(ImmutableTermId dest, R relation) {
      this.dest = dest;
      this.relation = relation;
    }

    /** @return The destination term Id. */
    public ImmutableTermId getDest() {
      return dest;
    }

    /** @return The bundled relation. */
    public R getRelation() {
      return relation;
    }
  }
}