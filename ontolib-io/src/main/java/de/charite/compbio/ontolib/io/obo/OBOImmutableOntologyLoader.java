package de.charite.compbio.ontolib.io.obo;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import de.charite.compbio.ontolib.graph.data.ImmutableDirectedGraph;
import de.charite.compbio.ontolib.graph.data.ImmutableEdge;
import de.charite.compbio.ontolib.ontology.data.ImmutableOntology;
import de.charite.compbio.ontolib.ontology.data.ImmutableTermID;
import de.charite.compbio.ontolib.ontology.data.ImmutableTermPrefix;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermPrefix;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Load OBO into an {@link ImmutableOntology}.
 *
 * <p>
 * This is a generic implementation, the actual implementation will come from implementations of the
 * {@link OBOOntologyEntryFactory} interface.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class OBOImmutableOntologyLoader<T extends Term, R extends TermRelation> {

  /** File to load from. */
  private final File file;

  /** Parser to use. */
  private final OBOParser parser;

  /**
   * Constructor.
   *
   * @param file The {@link File} to load from.
   * @param debug Whether or not to allow debugging.
   */
  public OBOImmutableOntologyLoader(File file, boolean debug) {
    this.file = file;
    this.parser = new OBOParser(debug);
  }

  /**
   * Constructor, debugging is disabled.
   *
   * @param file The {@link File} to load from.
   */
  public OBOImmutableOntologyLoader(File file) {
    this(file, false);
  }

  /**
   * @param factory The {@link OBOOntologyEntryFactory} for constructing concrete terms and term
   *        relations.
   * @return Freshly loaded and constructed {@link ImmutableOntology}.
   * @throws IOException In the case of problems with I/O.
   */
  public ImmutableOntology<T, R> load(OBOOntologyEntryFactory<T, R> factory) throws IOException {
    // Construct helper, set the term ID mapping into factory, and trigger parsing.
    final HelperListener helper = new HelperListener(factory);
    factory.setTermIDs(helper.getTermIDs());
    parser.parseFile(file, helper);

    if (helper.getAllTermIDs().size() == 0) {
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

    // Get term ID (of possibly artificial) root.
    final ImmutableTermID rootTermID = findOrCreateArtificalRoot(helper, factory);

    // Construct edge list and relation map.
    final List<ImmutableEdge<TermID>> edges = new ArrayList<>();
    final Map<Integer, R> relationMap = new HashMap<>();
    for (Entry<ImmutableTermID, List<BundledIsARelation>> e : helper.getIsATermIDPairs()
        .entrySet()) {
      for (BundledIsARelation b : e.getValue()) {
        ImmutableEdge.construct(e.getKey(), b.getDest(), b.getRelation().getID());
        relationMap.put(b.getRelation().getID(), b.getRelation());
      }
    }

    ImmutableDirectedGraph<TermID, ImmutableEdge<TermID>> graph =
        ImmutableDirectedGraph.construct(helper.getAllTermIDs(), edges, true);
    return new ImmutableOntology<T, R>(graph, rootTermID, ImmutableMap.copyOf(helper.getTerms()),
        ImmutableMap.copyOf(relationMap));
  }

  /**
   * Find one root from helper's recorded outgoing <code>is_a</code> edges.
   *
   * <p>
   * For ontologies such as GO, an artificial root is created.
   * </p>
   *
   * @param helper
   * @param factory
   * @return The one root's {@link ImmutableTermID}, might be artificially created.
   */
  private ImmutableTermID findOrCreateArtificalRoot(
      OBOImmutableOntologyLoader<T, R>.HelperListener helper,
      OBOOntologyEntryFactory<T, R> factory) {
    // Find root candidates (no outgoing is-a edges).
    final List<ImmutableTermID> rootCandidates =
        new ArrayList<>(helper.getRootCandidateStanzas().keySet());

    // Get root or construct a synthetic one. Bail out if no root candidate was found.s
    if (rootCandidates.size() == 0) {
      throw new RuntimeException("No term without outgoing is-a edge. Empty or cyclic?");
    } else if (rootCandidates.size() == 1) {
      return rootCandidates.get(0);
    } else {
      final TermPrefix rootPrefix = helper.getFirstTermID().getPrefix();
      final int rootNumber = 0;
      final ImmutableTermID rootID = new ImmutableTermID(rootPrefix, rootNumber);
      // Bail out if "PREFIX:0" is already taken.
      // TODO: implement something smarter?
      if (helper.getAllTermIDs().contains(rootID)) {
        throw new RuntimeException(
            "Tried to guess artificial root as " + rootID + " but is already taken.");
      }

      // Register term ID.
      final String rootIDString = rootPrefix.getValue() + ":" + rootNumber;
      helper.getTermIDs().put(rootIDString, rootID);
      helper.getAllTermIDs().add(rootID);

      // Create artificial root term stanza and process through helper.
      final List<StanzaEntry> rootStanzaEntries = new ArrayList<>();
      rootStanzaEntries.add(new StanzaEntryID(rootIDString, null, null));
      rootStanzaEntries.add(new StanzaEntryName("<artificial root>", null, null));
      rootStanzaEntries.add(new StanzaEntryRemark(
          "This root was automatically inserted by ontolib on loading as this ontology "
              + "had more than one term without outgoing 'is_a' relations.",
          null, null));
      helper.parsedStanza(Stanza.create(StanzaType.TERM, rootStanzaEntries));

      // Register outgoing edges for root candidates. Here, we create fake "is_a" relations.
      for (ImmutableTermID termID : rootCandidates) {
        final R artificialIsARelation =
            factory.constructTermRelation(helper.getRootCandidateStanzas().get(termID),
                new StanzaEntryIsA(rootIDString, null, null));
        final BundledIsARelation bundle = new BundledIsARelation(rootID, artificialIsARelation);
        helper.getIsATermIDPairs().put(termID, Lists.newArrayList(bundle));
      }

      // Return artificial root.
      return rootID;
    }
  }

  /**
   * Private helper class implementing the {@link OBOParseResultListener} for using event-based
   * parsing.
   *
   * <p>
   * This "little" helper class implements the interface for the event-based parsing and constructs
   * the objects from <code>ontolib-core</code> on the fly from the <code>ontolib-io</code> objects.
   * It also takes care of generating (hopefully) memory-saving representations by ensuring that
   * equal term ID strings are converted to the same identical {@link TermID} object and all equal
   * {@link TermPrefix} are the same object.
   * </p>
   */
  private class HelperListener implements OBOParseResultListener {

    /** Term prefix value to {@link ImmutableTermPrefix}. */
    private final Map<String, ImmutableTermPrefix> prefixes = new HashMap<>();

    /** First seen term ID, we will construct the artificial root term if necessary. */
    private ImmutableTermID firstTermID = null;

    /** All constructed term IDs. */
    private final Set<TermID> allTermIDs = new HashSet<>();

    // TODO: At the moment, HP:1 and HP:01 would be mapped to different objects :(
    /** Term strings to terms. */
    private final SortedMap<String, ImmutableTermID> termIDs = new TreeMap<>();

    /** Terms constructed from parsing. */
    private final SortedMap<ImmutableTermID, T> terms = new TreeMap<>();

    /** Term relations constructed from parsing "is_a" relations. */
    private final SortedMap<ImmutableTermID, List<BundledIsARelation>> isATermIDPairs =
        new TreeMap<>();

    /** Factory for creating concrete term and term relation objects, injected into helper. */
    private final OBOOntologyEntryFactory<T, R> ontologyEntryFactory;

    /** We store the {@link Stanza} objects for the root candidates (no outgoing is_a relation). */
    private final Map<ImmutableTermID, Stanza> rootCandidateStanzas = new HashMap<>();

    /**
     * We store the stanzas
     * 
     * /** Constructor.
     *
     * @param ontologyEntryFactory Factory for creating concrete term and term relation objects.
     */
    public HelperListener(OBOOntologyEntryFactory<T, R> ontologyEntryFactory) {
      this.ontologyEntryFactory = ontologyEntryFactory;
    }

    @Override
    public void parsedHeader(Header header) {
      // TODO: pass file-wide meta information into resulting ontology
    }

    @Override
    public void parsedStanza(Stanza stanza) {
      if (stanza.getType() == StanzaType.TERM) { // ignore all but the terms
        // Obtain ImmutableTermID for the source term.
        final List<StanzaEntry> idEntries = stanza.getEntryByType().get(StanzaEntryType.ID);
        if (idEntries.size() != 1) {
          throw new RuntimeException(
              "Cardinality of 'id' must be 1 but is " + idEntries.size() + "(" + stanza + ")");
        }
        final StanzaEntryID idEntry = (StanzaEntryID) idEntries.get(0);
        final ImmutableTermID sourceID = registeredTermID(idEntry.getId());

        // Construct TermID objects for all alternative ids.
        final List<StanzaEntry> altIDEntries = stanza.getEntryByType().get(StanzaEntryType.ALT_ID);
        if (altIDEntries != null) {
          for (StanzaEntry e : altIDEntries) {
            registeredTermID(((StanzaEntryAltID) e).getAltID());
          }
        }

        // TODO: generalize this registration through proper subclassing of StanzaEntry?
        // Construct TermID objects for all "xref" tags.
        final List<StanzaEntry> xRefEntries = stanza.getEntryByType().get(StanzaEntryType.XREF);
        if (xRefEntries != null) {
          for (StanzaEntry e : xRefEntries) {
            registeredTermID(((StanzaEntryXRef) e).getDBXRef().getName());
          }
        }

        // Interpret all "is_a" relation entries and register them to termIDPairs.
        final List<StanzaEntry> entries = stanza.getEntryByType().get(StanzaEntryType.IS_A);
        if (entries != null) {
          for (StanzaEntry e : entries) {
            StanzaEntryIsA isA = (StanzaEntryIsA) e;
            final ImmutableTermID destID = registeredTermID(isA.getId());
            final BundledIsARelation bundledRelation = new BundledIsARelation(destID,
                ontologyEntryFactory.constructTermRelation(stanza, isA));

            if (!isATermIDPairs.containsKey(sourceID)) {
              isATermIDPairs.put(sourceID, Lists.newArrayList(bundledRelation));
            } else {
              isATermIDPairs.get(sourceID).add(bundledRelation);
            }
          }
        }

        // Keep Stanza if this is a candidate root (no is_a relation)
        if (entries == null || entries.isEmpty()) {
          rootCandidateStanzas.put(sourceID, stanza);
        }

        // Construct the term through the factory.
        terms.put(sourceID, ontologyEntryFactory.constructTerm(stanza));
      }
    }

    /**
     * If necessary, insert term ID into {@link #termIDs} {@link #prefixes}.
     *
     * @param termIDStr String representation of term ID.
     * @return {@link TermID} present in {@link #termIDs}.
     */
    private ImmutableTermID registeredTermID(String termIDStr) {
      ImmutableTermID tmpID = termIDs.get(termIDStr);
      if (tmpID != null) {
        return tmpID;
      }

      final String tokens[] = termIDStr.split(":", 2);
      if (tokens.length != 2) {
        throw new RuntimeException("Term ID does not contain colon! " + termIDStr);
      }

      ImmutableTermPrefix tmpPrefix = prefixes.get(tokens[0]);
      if (tmpPrefix == null) {
        tmpPrefix = new ImmutableTermPrefix(tokens[0]);
        prefixes.put(tokens[0], tmpPrefix);
      }

      tmpID = new ImmutableTermID(tmpPrefix, Integer.parseInt(tokens[1]));
      termIDs.put(termIDStr, tmpID);
      allTermIDs.add(tmpID);

      // Make sure to record the first term ID.
      if (firstTermID == null) {
        firstTermID = tmpID;
      }

      return tmpID;
    }

    @Override
    public void parsedFile() {/* nop */}

    /**
     * @return Multimap-like encoding of directed terms.
     */
    public Map<ImmutableTermID, List<BundledIsARelation>> getIsATermIDPairs() {
      return isATermIDPairs;
    }

    /**
     * @return First seen term ID for constructing artificial root.
     */
    public TermID getFirstTermID() {
      return firstTermID;
    }

    /**
     * @return Set of all seen term ids.
     */
    public Set<TermID> getAllTermIDs() {
      return allTermIDs;
    }

    /**
     * @return Mapping from string term ID to term ID.
     */
    public SortedMap<String, ImmutableTermID> getTermIDs() {
      return termIDs;
    }

    /**
     * @return Mapping from immutable term ID to term with all terms.
     */
    public Map<ImmutableTermID, T> getTerms() {
      return terms;
    }

    /**
     * @return The root candidate stanza mapping.
     */
    public Map<ImmutableTermID, Stanza> getRootCandidateStanzas() {
      return rootCandidateStanzas;
    }
  }

  /**
   * Bundle an ImmutableTermID and a term relation for the is-a term pairs.
   */
  public class BundledIsARelation {

    /** Destination of is-a relation. */
    private final ImmutableTermID dest;

    /** The bundled relation. */
    private final R relation;

    /**
     * Constructor.
     *
     * @param dest Destination term ID.
     * @param relation Relation.
     */
    public BundledIsARelation(ImmutableTermID dest, R relation) {
      this.dest = dest;
      this.relation = relation;
    }

    /**
     * @return The destination term ID.
     */
    public ImmutableTermID getDest() {
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
