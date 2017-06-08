package de.charite.compbio.ontolib.ontology.algo;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermID;
import de.charite.compbio.ontolib.ontology.data.TermRelation;
import java.util.Collection;
import java.util.Map;

/**
 * Utility class for computing information content of {@link Term} (identified by their
 * {@link TermID}s) in an {@link Ontology}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public final class InformationContentComputation<T extends Term, R extends TermRelation> {

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
    // TODO: continue implementation here
    return null;
  }

}
