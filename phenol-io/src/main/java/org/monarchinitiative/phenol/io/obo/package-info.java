/**
 * Generic code for parsing OBO files.
 *
 * <p>The facade classes here are {@link org.monarchinitiative.phenol.io.obo.OboParser} and {@link
 * org.monarchinitiative.phenol.io.obo.OboImmutableOntologyLoader} classes.
 *
 * <p>{@link org.monarchinitiative.phenol.io.obo.OboParser} either generates a {@link
 * org.monarchinitiative.phenol.io.obo.OboFile} object with the full information from the OBO file
 * or you can use {@link org.monarchinitiative.phenol.io.obo.OboParseResultListener} to get the
 * header and all OBO stanzas as {@link org.monarchinitiative.phenol.io.obo.Header} and {@link
 * org.monarchinitiative.phenol.io.obo.Stanza} objects as they are loaded from the OBO file. The
 * first variant might be more convenient but requires storing the full OBO information in memory
 * while the second does not allow for simple loop-style processing but is potentially memory
 * saving.
 *
 * <p>{@link org.monarchinitiative.phenol.io.obo.OboImmutableOntologyLoader} allows you to directly
 * build an {@link org.monarchinitiative.phenol.ontology.data.ImmutableOntology} from the stream of
 * {@link org.monarchinitiative.phenol.io.obo.Stanza} events. For each ontology to load (e.g., GO,
 * HPO etc.), there is a {@link org.monarchinitiative.phenol.io.obo.OboOntologyEntryFactory}
 * implementation that has to be passed into the loader.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
package org.monarchinitiative.phenol.io.obo;
