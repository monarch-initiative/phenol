package de.charite.compbio.ontolib.ontology.similarity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of pairwise Resnik similarity.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
final class PairwiseResnikSimilarity<T extends Term, R extends TermRelation>
    implements
      PairwiseSimilarity {

  /** {@link Ontology} to base computations on. */
  private final Ontology<T, R> ontology;

  /** {@link Map} from {@link TermID} to its information content. */
  private final Map<TermID, Double> termToIC;

  /**
   * Construct new {@link PairwiseResnikSimilarity}.
   * 
   * @param ontology {@link Ontology} to base computations on.
   * @param termToIC {@link Map} from{@link TermID} to its information content.
   */
  public PairwiseResnikSimilarity(Ontology<T, R> ontology, Map<TermID, Double> termToIC) {
    this.ontology = ontology;
    this.termToIC = termToIC;
  }

  @Override
  public double computeScore(TermID query, TermID target) {
    final Set<TermID> queryTerms = ontology.getAllAncestorTermIDs(Lists.newArrayList(query), true);
    final Set<TermID> targetTerms =
        ontology.getAllAncestorTermIDs(Lists.newArrayList(target), true);

    return Sets.intersection(queryTerms, targetTerms).stream().mapToDouble(tID -> termToIC.get(tID))
        .max().orElse(0.0);
  }

  /**
   * @return Underlying {@link Ontology}.
   */
  public Ontology<T, R> getOntology() {
    return ontology;
  }

  /**
   * @return {@link Map} from {@link TermID} to information content.
   */
  public Map<TermID, Double> getTermToIC() {
    return termToIC;
  }

}
