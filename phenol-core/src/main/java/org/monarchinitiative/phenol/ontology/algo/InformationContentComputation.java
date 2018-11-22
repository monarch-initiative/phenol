package org.monarchinitiative.phenol.ontology.algo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.monarchinitiative.phenol.ontology.data.*;
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
   * achieve this using {@link TermIds#augmentWithAncestors(ImmutableOntology, Set, boolean)}.
   *
   * @param termLabels Labels (for diseases, genes, ie., the objects being annotated to ontology terms() for each {@link Term}, identified by {@link TermId}
   * @return {@link Map} from {@link TermId} to information content.
   */
  public Map<TermId, Double> computeInformationContent(
      Map<TermId, Collection<TermId>> termLabels) {
    LOGGER.info(
        "Computing IC of {} terms using {} labels...",
        new Object[] {
          ontology.countAllTerms(), termLabels.values().stream().mapToInt(Collection::size).sum()
        });

    // Build mapping from TermId -> absolute frequency
    final TermId root = ontology.getRootTermId();

    final Map<TermId, Integer> termToFrequency = new HashMap<>();
    for (TermId termId : ontology.getNonObsoleteTermIds()) {
      termToFrequency.put(termId, 0);
    }
    for (Entry<TermId, Collection<TermId>> e : termLabels.entrySet()) {
      termToFrequency.put(e.getKey(), e.getValue().size());
    }

    // Compute information content for each TermId
    final int maxFreq = termToFrequency.get(root);
    final Map<TermId, Double> termToInformationContent =
        caculateInformationContent(maxFreq, termToFrequency);

    // Fix terms with IC of zero, set it to IC of root
    int countIcZero = 0;
    final double dummyIc = -Math.log(1 / (double) maxFreq);

    for (Term t : ontology.getTerms()) {
      if (t.isObsolete()) {
        continue;
      }
      if (!termToFrequency.containsKey(t.getId())) {
        ++countIcZero;
        termToInformationContent.put(t.getId(), dummyIc);
      }
    }

    if (countIcZero > 0) {
      LOGGER.warn(
          "Frequency of {} non-obsolete terms was zero! Their IC has been set to {} = "
              + "- log(1 / {}).",
        countIcZero, dummyIc, maxFreq);
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
  private Map<TermId, Double> caculateInformationContent(
      double maxFreq, Map<TermId, Integer> termToFrequency) {
    final Map<TermId, Double> termToIc = new HashMap<>();

    for (Entry<TermId, Integer> e : termToFrequency.entrySet()) {
      final double probability = e.getValue() / maxFreq;
      final double informationContent = e.getValue()>0 ? -Math.log(probability) : 0;
      termToIc.put(e.getKey(), informationContent);
    }

    return termToIc;
  }
}
