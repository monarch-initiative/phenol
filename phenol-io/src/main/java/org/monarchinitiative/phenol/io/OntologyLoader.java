package org.monarchinitiative.phenol.io;

import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.ontology.data.Ontology;

/**
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public interface OntologyLoader {

  public Ontology load() throws PhenolException;

}
