package org.monarchinitiative.phenol.ontology.data;

import java.io.Serializable;
import java.util.Optional;

/**
 * A {@link TermAnnotation} links a {@link TermId} to a <code>String</code> <b>label</b> of a "world
 * object".
 *
 * <p>For all annotated "world objects", the labels have to be unique for each object. The
 * annotations can then be used for, e.g., feeding into the information content computation and thus
 * into the similarity metrics computation algorithms.
 *
 * <p>Implementing classes have to properly implement <code>hashValue()</code> and <code>equals()
 * </code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public interface TermAnnotation extends Serializable, Comparable<TermAnnotation> {
   long serialVersionUID = 2L;
  /**
   * Query for annotated term's ID.
   *
   * @return The annotated term's {@link TermId}.
   */
  TermId getTermId();

  /**
   * Query for "world object" label.
   *
   * @return The "world object" label that the term is annotated with.
   */
  String getLabel();

  /**
   * Query for evidence code.
   *
   * <p>The default implementation returns an empty {@link Optional} value.
   *
   * @return Optional {@code String} with the identifier of the evidence code.
   */
  default Optional<String> getEvidenceCode() {
    return Optional.empty();
  }

  /**
   * Query for frequency of annotation in percent.
   *
   * <p>The default implementation always returns {@code Optional.empty()}.
   */
  default Optional<Float> getFrequency() {
    return Optional.empty();
  }
}
