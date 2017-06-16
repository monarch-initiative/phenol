/**
 * This package contains the generic code for parsing OBO files.
 *
 * <p>
 * The facade classes here are {@link OBOParser} and {@link OBOImmutableOntologyLoader} classes.
 * </p>
 *
 * <p>
 * {@link OBOParser} either generates a {@link OBOFile} object with the full information from the
 * OBO file or you can use {@link OBOParseResultListener} to get the header and all OBO stanzas as
 * {@link Header} and {@link Stanza} objects as they are loaded from the OBO file. The first variant
 * might be more convenient but requires storing the full OBO information in memory while the second
 * does not allow for simple loop-style processing but is potentially memory saving.
 * </p>
 *
 * <p>
 * {@link OBOImmutableOntologyLoader} allows you to directly build an {@link ImmutableOntology} from
 * the stream of {@link Stanza} events. For each ontology to load (e.g., GO, HPO etc.), there is a
 * {@link OBOOntologyEntryFactory} implementation that has to be passed into the loader for
 * construction of the concrete {@link Term} and {@link TermRelation} implementations.
 * </p>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
package de.charite.compbio.ontolib.io.obo;
