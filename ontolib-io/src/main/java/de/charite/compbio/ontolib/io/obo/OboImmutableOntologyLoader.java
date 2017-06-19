package de.charite.compbio.ontolib.io.obo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import de.charite.compbio.ontolib.ontology.data.ImmutableOntology;
import de.charite.compbio.ontolib.ontology.data.ImmutableTermId;
import de.charite.compbio.ontolib.ontology.data.ImmutableTermPrefix;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermPrefix;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Load OBO into an {@link ImmutableOntology}.
 *
 * <p>
 * This is a generic implementation, the actual implementation will come from implementations of the
 * {@link OboOntologyEntryFactory} interface.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class OboImmutableOntologyLoader<T extends Term, R extends TermRelation> {

  /**
   * The {@link Logger} object to use for logging.
   */
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
   *        relations.
   * @return Freshly loaded and constructed {@link ImmutableOntology}.
   * @throws IOException In the case of problems with I/O.
   */
  public ImmutableOntology<T, R> load(OboOntologyEntryFactory<T, R> factory) throws IOException {
    // Construct helper, set the term Id mapping into factory, and trigger parsing.
    final HelperListener helper = new HelperListener(factory);
    factory.setTermIds(helper.getTermIds());
    parser.parseFile(file, helper);

    if (helper.getAllTermIds().size() == 0) {
      throw new RuntimeException("No terms in ontology?!");
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

    // Construct edge list and relation map.
    final List<ImmutableEdge<TermId>> edges = new ArrayList<>();
    final Map<Integer, R> relationMap = new HashMap<>();
    for (Entry<ImmutableTermId, List<BundledIsARelation>> e : helper.getIsATermIdPairs()
        .entrySet()) {
      for (BundledIsARelation b : e.getValue()) {
        edges.add(ImmutableEdge.construct(e.getKey(), b.getDest(), b.getRelation().getId()));
        relationMap.put(b.getRelation().getId(), b.getRelation());
      }
    }

    ImmutableDirectedGraph<TermId, ImmutableEdge<TermId>> graph =
        ImmutableDirectedGraph.construct(helper.getAllTermIds(), edges, true);
    return new ImmutableOntology<T, R>(graph, rootTermId, ImmutableMap.copyOf(helper.getTerms()),
        ImmutableMap.copyOf(relationMap));
  }

  /**
   * Find one root from helper's recorded outgoing <code>is_a</code> edges.
   *
   * <p>
   * For ontologies such as GO, an artificial root is created.
   * </p>
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
      throw new RuntimeException("No term without outgoing is-a edge. Empty or cyclic?");
    } else if (rootCandidates.size() == 1) {
      return rootCandidates.get(0);
    } else {
      final TermPrefix rootPrefix = helper.getFirstTermId().getPrefix();
      // TODO: implement something smarter?
      final String rootLocalId = helper.getFirstTermId().getId().replaceAll(".", "0");
      final ImmutableTermId rootId = new ImmutableTermId(rootPrefix, rootLocalId);
      if (helper.getAllTermIds().contains(rootId)) {
        throw new RuntimeException(
            "Tried to guess artificial root as " + rootId + " but is already taken.");
      }

      LOGGER.info("Ontology had {} root terms {}, inserting artificial root term {}",
          new Object[] {rootCandidates.size(), rootCandidates, rootId});

      // Register term Id.
      final String rootIdString = rootPrefix.getValue() + ":" + rootLocalId;
      helper.getTermIds().put(rootIdString, rootId);
      helper.getAllTermIds().add(rootId);

      // Create artificial root term stanza and process through helper.
      final List<StanzaEntry> rootStanzaEntries = new ArrayList<>();
      rootStanzaEntries.add(new StanzaEntryId(rootIdString, null, null));
      rootStanzaEntries.add(new StanzaEntryName("<artificial root>", null, null));
      rootStanzaEntries.add(new StanzaEntryRemark(
          "This root was automatically inserted by ontolib on loading as this ontology "
              + "had more than one term without outgoing 'is_a' relations.",
          null, null));
      helper.parsedStanza(Stanza.create(StanzaType.TERM, rootStanzaEntries));

      // Register outgoing edges for root candidates. Here, we create fake "is_a" relations.
      for (ImmutableTermId termId : rootCandidates) {
        final R artificialIsARelation =
            factory.constructTermRelation(helper.getRootCandidateStanzas().get(termId),
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
   * <p>
   * This "little" helper class implements the interface for the event-based parsing and constructs
   * the objects from <code>ontolib-core</code> on the fly from the <code>ontolib-io</code> objects.
   * It also takes care of generating (hopefully) memory-saving representations by ensuring that
   * equal term Id strings are converted to the same identical {@link TermId} object and all equal
   * {@link TermPrefix} are the same object.
   * </p>
   */
  private class HelperListener implements OboParseResultListener {

    /**
     * Term prefix value to {@link ImmutableTermPrefix}.
     */
    private final Map<String, ImmutableTermPrefix> prefixes = new HashMap<>();

    /** First seen term Id, we will construct the artificial root term if necessary. */
    private ImmutableTermId firstTermId = null;

    /** All TermId objects constructed from Term stanzas (only!). */
    private final List<TermId> allTermIds = new ArrayList<>();

    // TODO: At the moment, HP:1 and HP:01 would be mapped to different objects :(
    /** Term strings to terms. */
    private final SortedMap<String, ImmutableTermId> termIds = new TreeMap<>();

    /** Terms constructed from parsing. */
    private final SortedMap<ImmutableTermId, T> terms = new TreeMap<>();

    /** Term relations constructed from parsing "is_a" relations. */
    private final SortedMap<ImmutableTermId, List<BundledIsARelation>> isATermIdPairs =
        new TreeMap<>();

    /** Factory for creating concrete term and term relation objects, injected into helper. */
    private final OboOntologyEntryFactory<T, R> ontologyEntryFactory;

    /**
     * We store the {@link Stanza} objects for the root candidates (no outgoing is_a relation).
     */
    private final Map<ImmutableTermId, Stanza> rootCandidateStanzas = new HashMap<>();

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
      // TODO: pass file-wide meta information into resulting ontology
    }

    @Override
    public void parsedStanza(Stanza stanza) {
      if (stanza.getType() == StanzaType.TERM) { // ignore all but the terms
        // Obtain ImmutableTermId for the source term.
        final List<StanzaEntry> idEntries = stanza.getEntryByType().get(StanzaEntryType.Id);
        if (idEntries.size() != 1) {
          throw new RuntimeException(
              "Cardinality of 'id' must be 1 but is " + idEntries.size() + "(" + stanza + ")");
        }
        final StanzaEntryId idEntry = (StanzaEntryId) idEntries.get(0);
        final ImmutableTermId sourceId = registeredTermId(idEntry.getId());
        allTermIds.add(sourceId);

        // Construct TermId objects for all alternative ids.
        final List<StanzaEntry> altIdEntries = stanza.getEntryByType().get(StanzaEntryType.ALT_Id);
        if (altIdEntries != null) {
          for (StanzaEntry e : altIdEntries) {
            registeredTermId(((StanzaEntryAltId) e).getAltId());
          }
        }

        // TODO: generalize this registration through proper subclassing of StanzaEntry?
        // Construct TermId objects for all "xref" tags.
        final List<StanzaEntry> xRefEntries = stanza.getEntryByType().get(StanzaEntryType.XREF);
        if (xRefEntries != null) {
          for (StanzaEntry e : xRefEntries) {
            registeredTermId(((StanzaEntryXref) e).getDbXref().getName());
          }
        }

        // Interpret all "is_a" relation entries and register them to termIdPairs.
        final List<StanzaEntry> entries = stanza.getEntryByType().get(StanzaEntryType.IS_A);
        if (entries != null) {
          for (StanzaEntry e : entries) {
            StanzaEntryIsA isA = (StanzaEntryIsA) e;
            final ImmutableTermId destId = registeredTermId(isA.getId());
            final BundledIsARelation bundledRelation = new BundledIsARelation(destId,
                ontologyEntryFactory.constructTermRelation(stanza, isA));

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
        terms.put(sourceId, ontologyEntryFactory.constructTerm(stanza));
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

      final String[] tokens = termIdStr.split(":", 2);
      if (tokens.length != 2) {
        throw new RuntimeException("Term Id does not contain colon! " + termIdStr);
      }

      ImmutableTermPrefix tmpPrefix = prefixes.get(tokens[0]);
      if (tmpPrefix == null) {
        tmpPrefix = new ImmutableTermPrefix(tokens[0]);
        prefixes.put(tokens[0], tmpPrefix);
      }

      tmpId = new ImmutableTermId(tmpPrefix, tokens[1]);
      termIds.put(termIdStr, tmpId);

      // Make sure to record the first term Id.
      if (firstTermId == null) {
        firstTermId = tmpId;
      }

      return tmpId;
    }

    @Override
    public void parsedFile() {/* nop */}

    /**
     * @return Multimap-like encoding of directed terms.
     */
    public Map<ImmutableTermId, List<BundledIsARelation>> getIsATermIdPairs() {
      return isATermIdPairs;
    }

    /**
     * @return First seen term Id for constructing artificial root.
     */
    public TermId getFirstTermId() {
      return firstTermId;
    }

    /**
     * @return Set of all seen term ids.
     */
    public List<TermId> getAllTermIds() {
      return allTermIds;
    }

    /**
     * @return Mapping from string term Id to term Id.
     */
    public SortedMap<String, ImmutableTermId> getTermIds() {
      return termIds;
    }

    /**
     * @return Mapping from immutable term Id to term with all terms.
     */
    public Map<ImmutableTermId, T> getTerms() {
      return terms;
    }

    /**
     * @return The root candidate stanza mapping.
     */
    public Map<ImmutableTermId, Stanza> getRootCandidateStanzas() {
      return rootCandidateStanzas;
    }
  }

  /**
   * Bundle an ImmutableTermId and a term relation for the is-a term pairs.
   */
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

    /**
     * @return The destination term Id.
     */
    public ImmutableTermId getDest() {
      return dest;
    }

    /**
     * @return The bundled relation.
     */
    public R getRelation() {
      return relation;
    }

  }

}
