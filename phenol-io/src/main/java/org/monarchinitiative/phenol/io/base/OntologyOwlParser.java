package org.monarchinitiative.phenol.io.base;

import java.io.File;
import java.io.IOException;

import org.monarchinitiative.phenol.ontology.data.Ontology;


public interface OntologyOwlParser<O extends Ontology> {
  /**
   * Parse and build specialized {@link Ontology}.
   *
   * @return The loaded {@link Ontology}.
   * @throws IOException In case of problem with reading from the file.
   */
  O parse();

  /** @return The OWL {@link File} that is loaded. */
  File getOwlFile();
}
