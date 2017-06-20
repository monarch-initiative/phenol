// Note: this file is here only to document the overall package.
/**
 * Ontolib is the Java tool for working with ontologies for the algorithmically inclined
 * Bioinformatician.
 *
 * <h5>Package Layout</h5>
 *
 * <p>
 * <code>de.charite.compbio.graph.*</code> -- Graph data structures and algorithms for representing
 * the ontology topology and working with them.
 * </p>
 *
 * <p>
 * <code>de.charite.compbio.ontology.*</code> -- Data structures and algorithms for representing
 * ontologies, their terms, and term relations and working with them.
 * </p>
 *
 * <p>
 * <code>de.charite.compbio.formats.*</code> -- specialized code for the ontologies with special
 * support: GO, HPO, MPO, uPheno, and ZPO.
 * </p>
 *
 * <p>
 * <code>de.charite.compbio.utils</code> -- Utility code used in the library.
 * </p>
 *
 * <h5>Open Questions / TODOs</h5>
 * 
 * <ul>
 * <li>Support for ZPO missing, is there a working zp.obo file?</li>
 * <li>Updates for Uberpheno/uPheno available?</li>
 * <li>Which namespace do we want?</li>
 * <li>Tests for ontolib-core module</li>
 * <li>Check coverage of ontolib-io module</li>
 * <li>Further split out ontology-specific parts into their own Maven artifacts?</li>
 * <li>Will need better project-wide documentation, can do with <tt>mvn site</tt>?</li>
 * <li>So far no annotation of ZPO and GO to genes</li>
 * <li>We need to flesh out the different parsing modes for the ontologies (maybe more than one
 * mode, e.g., "slim" and "fat"?) and consolidate the copy and paste code</li>
 * <li>We should implement BOQCA?!</li>
 * <li>Provide CLI tools in ontolib-cli package, will require more library support, at least
 * versionized serialization: Resnik precomputation + serialization, score distribution
 * precomputation + serialization, matching term sets to "world object", e.g., disease or gene
 * annotation with multi-testing correction, loading ontologies and serialization</li>
 * <li>Resolve all embedded TODO comments!</li>
 * </ul>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:sebastian.koehler@charite.de">Sebastian Koehler</a>
 */
package de.charite.compbio.ontolib;