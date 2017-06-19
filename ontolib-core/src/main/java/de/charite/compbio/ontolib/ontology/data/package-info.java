/**
 * Data structures for generic representation of ontologies.
 *
 * <p>
 * The API has been designed around the following principles:
 * </p>
 *
 * <ul>
 * <li>The API should follow the principle of least surprise, i.e., offer consistency.</li>
 * <li>Redundancies should be kept to a minimum, in some places, it is possible functions are
 * offered for obtaining {@link java.util.Collection} or a {@link java.util.Iterator}, depending on
 * the user's requirements.</li>
 * <li>The API deals with {@link de.charite.compbio.ontolib.ontology.data.TermId}s on the graph
 * level. These are then linked to more richly attributed
 * {@link de.charite.compbio.ontolib.ontology.data.Term}s through {@link java.util.Map}s.</li>
 * <li>The central classes offer relatively minimal interfaces, decorator classes are used for
 * providing more extended behaviour.</li>
 * <li>Even more complex behaviour is available in the
 * <code>de.charite.compbio.ontolib.ontology.algo</code> package in a one-class-per-algorithm
 * fashion, similar to the separation between graph data structures and graph algorithms.</li>
 * </ul>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
package de.charite.compbio.ontolib.ontology.data;
