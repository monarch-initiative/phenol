package com.github.phenomics.ontolib.io.base;

import java.io.File;
import java.io.IOException;

import com.github.phenomics.ontolib.ontology.data.Ontology;
import com.github.phenomics.ontolib.ontology.data.Term;
import com.github.phenomics.ontolib.ontology.data.TermRelation;

/**
 * Interface for parsing OBO into {@link Ontology} objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 *
 * @param <O> The {@link Ontology} to return.
 */
public interface OntologyOboParser<
    O extends Ontology<? extends Term, ? extends TermRelation>> {

  /**
   * Parse and build specialized {@link Ontology}.
   *
   * @return The loaded {@link Ontology}.
   * @throws IOException In case of problem with reading from the file.
   */
  O parse() throws IOException;

  /**
   * @return The OBO {@link File} that is loaded.
   */
  File getOboFile();

}
