package de.charite.compbio.ontolib.io.base;

import java.io.File;
import java.io.IOException;

import de.charite.compbio.ontolib.ontology.data.Ontology;
import de.charite.compbio.ontolib.ontology.data.Term;
import de.charite.compbio.ontolib.ontology.data.TermRelation;

/**
 * Interface for parsing OBO into {@link Ontology} objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 *
 * @param <OntologyT> The {@link Ontology} to return.
 */
public interface OntologyOboParser<
    OntologyT extends Ontology<? extends Term, ? extends TermRelation>> {

  /**
   * Parse and build specialized {@link Ontology}.
   *
   * @return The loaded {@link Ontology}.
   * @throws IOException In case of problem with reading from the file.
   */
  OntologyT parse() throws IOException;

  /**
   * @return The OBO {@link File} that is loaded.
   */
  File getOboFile();

}
