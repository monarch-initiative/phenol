package de.charite.compbio.ontolib.ontology.algo;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermId;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for computing information content of {@link Term} (identified by their
 * {@link TermId}s) in an {@link Ontology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class InformationContentComputation<T extends Term, R extends TermRelation> {

  /**
   * {@link Logger} object to use.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(InformationContentComputation.class);

  /**
   * {@link Ontology} to work on for computations.
   */
  private final Ontology<T, R> ontology;

  /**
   * Constructor.
   *
   * @param ontology {@link Ontology} to use for the computations.
   */
  public InformationContentComputation(Ontology<T, R> ontology) {
    this.ontology = ontology;
  }

  /**
   * Perform the actual computation.
   * 
   * @param <LabelT> Labels for objects from "the real world". This could, e.g., be
   *        <code>String</code>s with gene names. This type has to properly implement
   *        <code>equals(Object)</code> and <code>hashValue()</code> as it is to be used as keys in
   *        a {@link HashMap}.
   * 
   * @param termLabels Labels for each {@link Term}, identified by {@link TermId}
   * @return {@link Map} from {@link TermId} to information content.
   */
  public <LabelT> Map<TermId, Double> computeInformationContent(
      Map<TermId, ? extends Collection<LabelT>> termLabels) {
    LOGGER.info("Computing IC of {} terms using {} labels...", new Object[] {ontology.countTerms(),
        termLabels.values().stream().mapToInt(l -> l.size()).sum()});

    // Build mapping from TermId -> absolute frequency
    final TermId root = ontology.getRootTermId();
    final Map<TermId, Integer> termToFrequency = new HashMap<>();
    for (Entry<TermId, ? extends Collection<LabelT>> e : termLabels.entrySet()) {
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

    LOGGER.warn("Frequency of {} non-obsolete terms was zero! Setting IC of these to {} = "
        + "- log(1 / {}).", new Object[] {countIcZero, dummyIc, maxFreq});
    LOGGER.info("Computing IC is complete.");

    return termToInformationContent;

  }

  /**
   * Calculate information content for each {@link TermId}.
   * 
   * @param maxFreq Maximal frequency of any term (root's frequency).
   * @param termToFrequency {@link Map} from term to absolute frequency.
   * @return {@link Map} from {@link TermId} to information content.
   */
  private Map<TermId, Double> caculateInformationContent(double maxFreq,
      Map<TermId, Integer> termToFrequency) {
    final Map<TermId, Double> termToIc = new HashMap<>();

    for (Entry<TermId, Integer> e : termToFrequency.entrySet()) {
      final double probability = e.getValue() / maxFreq;
      final double informationContent = -Math.log(probability);
      termToIc.put(e.getKey(), informationContent);
    }

    return termToIc;
  }

}
