package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;

/**
 * A {@link TermAnnotation} links a {@link TermId} to a <code>String</code> <b>label</b> of a "world
 * object".
 *
 * <p>
 * For all annotated "world objects", the labels have to be unique for each object. The annotations
 * can then be used for, e.g., feeding into the information content computation and thus into the
 * similarity metrics computation algorithms.
 * </p>
 *
 * <p>
 * Implementing classes have to properly implement <code>hashValue()</code> and
 * <code>equals()</code>.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
public interface TermAnnotation extends Serializable, Comparable<TermAnnotation> {

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

}
