package org.monarchinitiative.phenol.io.base;

import java.io.File;
import java.io.IOException;

import org.monarchinitiative.phenol.ontology.data.Ontology;

/**
 * Interface for parsing OBO into {@link Ontology} objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @param <O> The {@link Ontology} to return.
 */
public interface OntologyOboParser<O extends Ontology> {

  /**
   * Parse and build specialized {@link Ontology}.
   *
   * @return The loaded {@link Ontology}.
   * @throws IOException In case of problem with reading from the file.
   */
  O parse() throws IOException;

  /** @return The OBO {@link File} that is loaded. */
  File getOboFile();
}
