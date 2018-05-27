package org.monarchinitiative.phenol.io.base;

import java.io.File;

import org.monarchinitiative.phenol.ontology.data.Ontology;


public interface OntologyOwlParser<O extends Ontology> {
  /**
   * Parse and build specialized {@link Ontology}.
   *
   * @return The loaded {@link Ontology}.
   */
  O parse();

  /** @return The OWL {@link File} that is loaded. */
  File getOwlFile();
}
