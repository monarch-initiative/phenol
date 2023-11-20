package org.monarchinitiative.phenol.ontology.data;

import java.io.Serializable;
import java.util.Optional;

/**
 * A {@link TermAnnotation} links a {@link TermId} to {@link TermId} of a <em>"world object"</em>.
 * <p>
 * For all annotated "world objects", the labels have to be unique for each object. The
 * annotations can then be used for, e.g., feeding into the information content computation and thus
 * into the similarity metrics computation algorithms.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public interface TermAnnotation extends Identified, Serializable, Comparable<TermAnnotation> {
   long serialVersionUID = 2L;

  /**
   * Get term ID of the "world object".
   * <p>
   * Note, this is <em>NOT</em> the annotation ID which can be obtained by {@link #id()}.
   */
  TermId getItemId();

  /**
   * Get an optional with an evidence code of the annotation.
   */
  // TODO - should we really return EvidenceCode here?
  default Optional<String> getEvidenceCode() {
    return Optional.empty();
  }

  /**
   * Get an optional frequency of the annotation in <em>percent</em>.
   */
  default Optional<Float> getFrequency() {
    return Optional.empty();
  }
}
