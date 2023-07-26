package org.monarchinitiative.phenol.ontology.algo;

import java.util.*;
import java.util.Map.Entry;

import org.monarchinitiative.phenol.ontology.data.*;
import org.monarchinitiative.phenol.utils.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: separate resulting precomputation DS and algorithm to equalize with graph.algo

/**
 * Utility class for computing information content of {@link Term} (identified by their {@link
 * TermId}s) in an {@link Ontology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class InformationContentComputation {

  /** {@link Logger} object to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(InformationContentComputation.class);

  /** {@link Ontology} to work on for computations. */
  private final Ontology ontology;

  /**
   * Constructor.
   *
   * @param ontology {@link Ontology} to use for the computations.
   */
  public InformationContentComputation(Ontology ontology) {
    this.ontology = ontology;
  }

  // TODO: perform expansion to ancestors here?!
  /**
   * Perform the actual computation.
   *
   * <p>Note that {@code termLabels} must already contain the implicit ancestor annotations. You can
   * achieve this using {@link TermIds#augmentWithAncestors(Ontology, Set, boolean)}.
   *
   * @param termLabels Labels (for diseases, genes, ie., the objects being annotated to ontology terms() for each {@link Term}, identified by {@link TermId}
   * @return {@link Map} from {@link TermId} to information content.
   */
  public Map<TermId, Double> computeInformationContent(Map<TermId, Collection<TermId>> termLabels) {
    LOGGER.info(
        "Computing IC of {} terms using {} labels...",
            ontology.nonObsoleteTermIdCount(), termLabels.values().stream().mapToInt(Collection::size).sum()
        );

    // Build mapping from TermId -> absolute frequency
    // Ought to use ontology.getRootTermId() ?
    final TermId root = ontology.getRootTermId();//TermId.of("HP:0000118");

    final Map<TermId, Integer> termToFrequency = new HashMap<>();
    for (TermId termId : ontology.nonObsoleteTermIds()) {
      termToFrequency.put(termId, 0);
    }
    for (Entry<TermId, Collection<TermId>> e : termLabels.entrySet()) {
      termToFrequency.put(e.getKey(), e.getValue().size());
    }

    // Compute information content for each TermId
    final int maxFreq = termToFrequency.get(root);
    final Map<TermId, Double> termToInformationContent = caculateInformationContent(maxFreq, termToFrequency);

    // Fix terms with IC of zero, set it to IC of root
    int countIcZero = 0;
    final double dummyIc = -Math.log(1 / (double) maxFreq);

    for (Term t : ontology.getTerms()) {
      if (t.isObsolete()) {
        continue;
      }
      if (!termToFrequency.containsKey(t.id())) {
        ++countIcZero;
        termToInformationContent.put(t.id(), dummyIc);
      }
    }

    if (countIcZero > 0) {
      LOGGER.warn(
          "Frequency of {} non-obsolete terms was zero! Their IC has been set to {} =  - log(1 / {}).", countIcZero, dummyIc, maxFreq);
    }
    LOGGER.info("Computing IC is complete.");

    return termToInformationContent;
  }

  /**
   * Calculate information content for each {@link TermId}.
   * We assign an information content of zero for terms that have zero frequency in our dataset
   *
   * @param maxFreq Maximal frequency of any term (root's frequency).
   * @param termToFrequency {@link Map} from term to absolute frequency.
   * @return {@link Map} from {@link TermId} to information content.
   */
  private Map<TermId, Double> caculateInformationContent(double maxFreq, Map<TermId, Integer> termToFrequency) {
    final Map<TermId, Double> termToIc = new HashMap<>();

    for (Entry<TermId, Integer> e : termToFrequency.entrySet()) {
      final double probability = e.getValue() / maxFreq;
      final double informationContent = e.getValue()>0 ? -Math.log(probability) : 0;
      termToIc.put(e.getKey(), informationContent);
    }

    return termToIc;
  }

  /**
   * This method returns the <b>most informative common ancestor (MICA)</b> of a set of
   * two terms t1 and t2. It is designed to use the map returned by {@link #computeInformationContent}.
   * @param t1 The first term
   * @param t2 the second term
   * @param ontology reference to the ontology
   * @param term2ic map of information content calculated for each term
   * @return The term representing the most informative common ancestor
   */
  public static TermId mostInformativeCommonAncestor(TermId t1, TermId t2, Ontology ontology, Map<TermId, Double> term2ic) {
    // Case 1, terms are identical
    if (t1.equals(t2)) return t1;
    // Case 2, t2 is an ancestor of t1
    Set<TermId> anc1 = ontology.getAncestorTermIds(t1,false);
    if (anc1.contains(t2)) return t2;
    // Case 3, t1 is an ancestor of t2
    Set<TermId> anc2 = ontology.getAncestorTermIds(t2,false);
    if (anc2.contains(t1)) return t1;
    // Case 4, t1 and t2 are not ancestors of one another
    Set<TermId> intersection = Sets.intersection(anc1, anc2);
    TermId mica = null;
    double maxIC = -1.0; // information content
    Deque<TermId> stack = new ArrayDeque<>();
    stack.push(t1);
    while (! stack.isEmpty()) {
      TermId t = stack.pop();
      if (intersection.contains(t)) {
        double ic = term2ic.get(t);
        if (ic>maxIC) {
          mica=t;
          maxIC=ic;
        }
      }
      for (TermId p : ontology.graph().getParents(t, false)) {
        stack.push(p);
      }
    }
    return mica;
  }
}
