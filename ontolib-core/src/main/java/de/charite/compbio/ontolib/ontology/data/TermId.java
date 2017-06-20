package de.charite.compbio.ontolib.ontology.data;

import java.io.Serializable;


/**
 * Identifier of a {@link Term}, consisting of a {@link TermPrefix} and a local numeric ID.
 *
 * <h5>Design Notes</h5>
 *
 * <p>
 * The whole point of this interface and the implementing classes is optimization. This type
 * hierarchy is designed such that when loading a file with an ontology, one {@link TermPrefix}
 * instance is generated for each occuring term prefix value. The identifiers are assumed to be
 * numeric. This has the advantage that fast implementations of {@link Object#hashCode()} and
 * {@link Object#equals(Object)} are easy and in the end, comparison is done on the level of integer
 * and not on the level of string values.
 * </p>
 *
 * <p>
 * We are aware of the issue that, in general, ontologies and controlled vocabularies commonly used
 * are not restricted to using integers for such maps. Important examples are NCIT and UMLS.
 * However, one main assumption here is that such terms will only occur only as dbxref values and
 * not in the main reference fields for terms such as is-a relations.
 * </p>
 *
 * <p>
 * If at any time, such an ontology is to be loaded into the library, the system design has to
 * change. However, for the time being, numeric local term IDs have good performance and the
 * implementation can be kept relatively simple and maintainable.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public interface TermId extends Comparable<TermId>, Serializable {

  /**
   * Query for term ID's prefix.
   *
   * @return {@link TermPrefix} of the identifier
   */
  TermPrefix getPrefix();

  /**
   * Query for numeric term ID.
   *
   * @return See the "Remarks" section in {@link TermId} for reasoning on why to make this an
   *         <code>int</code>.
   */
  int getId();

  /**
   * Return the full term ID including prefix.
   *
   * @return The full Id.
   */
  String getIdWithPrefix();

}
