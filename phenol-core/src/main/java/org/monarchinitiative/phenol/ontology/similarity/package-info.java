/**
 * Computation of similarity metrics for ontologies.
 *
 * <h5>Remarks</h5>
 *
 * <p>Arguably, the most-used similarity measure for phenotype ontologies (by the authors) is Resnik
 * similarity. The other set- and information content-based similarities are implemented mostly for
 * completness' sake. However, <i>graph-based similarity measures (GraSM)</i> have currently be
 * ignored for simplicity's sake and low expected pratical usage.
 *
 * <h5>References</h5>
 *
 * <ul>
 *   <li>Robinson, Peter N., and Sebastian Bauer. <i>Introduction to bio-ontologies.</i> CRC Press,
 *       2011.
 * </ul>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
package org.monarchinitiative.phenol.ontology.similarity;
