package org.monarchinitiative.phenol.io.base;

import java.io.File;
import java.io.IOException;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.Relationship;

public interface OntologyOwlParser<
	O extends Ontology<? extends Term, ? extends Relationship>> {
	/**
	 * Parse and build specialized {@link Ontology}.
	 *
	 * @return The loaded {@link Ontology}.
	 * @throws IOException In case of problem with reading from the file.
	 */
	O parse() throws IOException;

	/**
	 * @return The OWL {@link File} that is loaded.
	 */
	File getOwlFile();
}
