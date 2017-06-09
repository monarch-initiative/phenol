package de.charite.compbio.ontolib.ontology.algo;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for computing information content of {@link Term} (identified by their
 * {@link TermID}s) in an {@link Ontology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class InformationContentComputation<T extends Term, R extends TermRelation> {

  /** {@link Logger} object to use. */
  private static final Logger LOGGER = LoggerFactory.getLogger(InformationContentComputation.class);

  /** {@link Ontology} to work on for computations. */
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
   * @param <Label> Labels for objects from "the real world". This could, e.g., be
   *        <code>String</code>s with gene names. This type has to properly implement
   *        <code>equals(Object)</code> and <code>hashValue()</code> as it is to be used as keys in
   *        a {@link HashMap}.
   * 
   * @param termLabels Labels for each {@link Term}, identified by {@link TermID}
   * @return {@link Map} from {@link TermID} to information content.
   */
  public <Label> Map<TermID, Double> computeIC(
      Map<TermID, ? extends Collection<Label>> termLabels) {
    LOGGER.info("Computing IC of {} terms using {} labels...", new Object[] {ontology.countTerms(),
        termLabels.values().stream().mapToInt(l -> l.size()).sum()});

    // Build mapping from TermID -> absolute frequency
    final TermID root = ontology.getRootTermID();
    final Map<TermID, Integer> termToFrequency = new HashMap<>();
    for (Entry<TermID, ? extends Collection<Label>> e : termLabels.entrySet()) {
      termToFrequency.put(e.getKey(), e.getValue().size());
    }

    // Compute information content for each TermID
    final int maxFreq = termToFrequency.get(root);
    final Map<TermID, Double> termToInformationContent =
        caculateInformationContent(maxFreq, termToFrequency);

    // Fix terms with IC of zero, set it to IC of root
    int countICZero = 0;
    final double dummyIC = -Math.log(1 / (double) maxFreq);

    for (Term t : ontology.getTerms()) {
      if (t.isObsolete()) {
        continue;
      }
      if (!termToFrequency.containsKey(t.getID())) {
        ++countICZero;
        termToInformationContent.put(t.getID(), dummyIC);
      }
    }

    LOGGER.warn("Frequency of {} non-obsolete terms was zero! Setting IC of these to {} = "
        + "- log(1 / {}).", new Object[] {countICZero, dummyIC, maxFreq});
    LOGGER.info("Computing IC is complete.");

    return termToInformationContent;

  }

  /**
   * Calculate information content for each {@link TermID}.
   * 
   * @param maxFreq Maximal frequency of any term (root's frequency).
   * @param termToFrequency {@link Map} from term to absolute frequency.
   * @return {@link Map} from {@link TermID} to information content.
   */
  private Map<TermID, Double> caculateInformationContent(double maxFreq,
      Map<TermID, Integer> termToFrequency) {
    final Map<TermID, Double> termToIC = new HashMap<>();

    for (Entry<TermID, Integer> e : termToFrequency.entrySet()) {
      final double probability = e.getValue() / maxFreq;
      final double informationContent = -Math.log(probability);
      termToIC.put(e.getKey(), informationContent);
    }

    return termToIC;
  }

}
