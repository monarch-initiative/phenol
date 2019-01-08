package org.monarchinitiative.phenol.io.obo;

import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.owlapi.FromOwl;
import org.monarchinitiative.phenol.base.PhenolException;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.ImmutableOntology;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * This class loads an OBO ontology using the OWLAPI.
 * Load OWL into an {@link ImmutableOntology}.
 *
 * @author <a href="mailto:HyeongSikKim@lbl.gov">HyeongSik Kim</a>
 * @author <a href="mailto:peter.robinson@jax.org">Peter Robinson</a>
 */
public class OboOntologyLoader implements OntologyLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(OboOntologyLoader.class);

  private final InputStream obo;
  /**
   * Construct an OWL loader that can load an OBO ontology.
   * @param file Path to the OBO file
   */
  public OboOntologyLoader(File file) throws FileNotFoundException {
    this(new FileInputStream(file));
  }
  
  public OboOntologyLoader(InputStream obo) {
    this.obo = obo;
  }

  @Override
  public Ontology load() throws PhenolException {
    // We first load ontologies expressed in owl using Obographs's FromOwl class.
    OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    OWLOntology owlOntology;
    try {
      owlOntology = m.loadOntologyFromOntologyDocument(obo);
    } catch (OWLOntologyCreationException e) {
      throw new PhenolException(e);
    }
    LOGGER.debug("Finished loading OWLOntology");

    LOGGER.debug("Converting to obograph graph");
    FromOwl fromOwl = new FromOwl();
    GraphDocument gd = fromOwl.generateGraphDocument(owlOntology);

    OboGraphDocumentAdaptor graphDocumentAdaptor = new OboGraphDocumentAdaptor(gd);

    return graphDocumentAdaptor.createOntology();
  }

}
